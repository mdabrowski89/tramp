package pl.mobite.tramp.ui.components.tramline.mvi

import io.reactivex.Observable
import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.data.repositories.TramLineRepository
import pl.mobite.tramp.data.repositories.models.*
import pl.mobite.tramp.ui.base.mvi.*
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineAction.FilterStopsAction
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineAction.GetTramLineAction
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineResult.FilterStopsResult
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineResult.GetTramLineResult.*
import kotlin.math.absoluteValue


class TramLineActionProcessor(
    schedulerProvider: SchedulerProvider,
    tramLineRepository: TramLineRepository,
    timeTableRepository: TimeTableRepository
): MviActionsProcessor<TramLineAction, TramLineResult>() {

    override fun getActionProcessors(shared: Observable<TramLineAction>) = listOf(
        shared.connect(getTramLineActionProcessor),
        shared.connect(filterStopsActionProcessor)
    )

    private val getTramLineActionProcessor = createActionProcessor<GetTramLineAction, TramLineResult>(
        schedulerProvider,
        { InFlight(it.tramLineDesc) },
        { Failure(it) }
    ) { action ->
        val localTramLine = try {
            tramLineRepository.getTramLineFromLocal(action.tramLineDesc)
        } catch (t: Throwable) {
            null
        }
        if (localTramLine != null) {
            onNextSafe(Success(localTramLine))
        }
        if (localTramLine == null || localTramLine.canBeOutdated) {
            val remoteTramLine = tramLineRepository.getTramLineFromRemote(action.tramLineDesc)
            onNextSafe(Success(remoteTramLine))
        }
        onCompleteSafe()
    }

    private val filterStopsActionProcessor = createActionProcessor<FilterStopsAction, TramLineResult>(
        schedulerProvider,
        { FilterStopsResult.InFlight },
        { FilterStopsResult.Failure(it) }
    ) { action ->
        val stopWithTimeDiffList = action.query.stops
            .mapNotNull { tramStop ->
                val timeTable = getTimeTable(timeTableRepository, tramStop, action.query.lineName)
                val lastTramTimeDiff = getLastTramTimeDiff(action.query.targetTime, timeTable.times)
                if (lastTramTimeDiff != null) {
                    return@mapNotNull TramStopWithLastTramTimeDiff(
                        tramStop,
                        lastTramTimeDiff
                    )
                }
                null
            }
        onNextSafe(FilterStopsResult.Success(getTramStopsWithTrams(stopWithTimeDiffList)))
        onCompleteSafe()
    }

    private fun getTimeTable(timeTableRepository: TimeTableRepository, tramStop: TramStop, lineName: String): TimeTable {
        var timeTable = try {
            timeTableRepository.getTimeTableFromLocal(tramStop.id)
        } catch (t: Throwable) { null }
        if (timeTable == null || timeTable.canBeOutdated) {
            timeTable = timeTableRepository.getTimeTableFromRemote(tramStop.id, lineName)
        }
        return timeTable
    }

    private fun getLastTramTimeDiff(targetTime: TimeEntry, timeTable: List<TimeEntry>): LastTramTimeDiff? {
        val sortedTimeTable = timeTable.sortedWith(compareBy({ it.hour }, { it.minute }))
        if (sortedTimeTable.isEmpty()) {
            return null
        }

        // handle case when target entry is before first entry
        if (targetTime < sortedTimeTable.first()) {
            val timeAfter = sortedTimeTable.first()
            val timeBefore = sortedTimeTable.last()
            return LastTramTimeDiff(timeBefore - targetTime - 24 * 60, timeAfter - targetTime)
        }

        // handle case when target entry is after last entry
        if (targetTime > sortedTimeTable.last()) {
            val timeAfter = sortedTimeTable.first()
            val timeBefore = sortedTimeTable.last()
            return LastTramTimeDiff(timeBefore - targetTime, timeAfter - targetTime + 24 * 60)
        }

        // handle other cases
        sortedTimeTable.forEachIndexed { i, timeEntry ->
            if (targetTime == timeEntry) {
                return LastTramTimeDiff(0, 0)
            } else if (targetTime < timeEntry && i > 0) {
                val timeBefore = sortedTimeTable[i - 1]
                val timeAfter = timeEntry
                return LastTramTimeDiff(timeBefore - targetTime, timeAfter - targetTime)
            }
        }

        // should not happen
        return null
    }

    private fun getTramStopsWithTrams(tramStopWithLastTramTimeDiffList: List<TramStopWithLastTramTimeDiff>): List<TramStop> {
        val stopsWithTrams = mutableListOf<TramStop>()
        tramStopWithLastTramTimeDiffList.forEachIndexed { i, (currentStop, currentStopTimeDiff) ->
            if (i + 1 < tramStopWithLastTramTimeDiffList.size) {
                val (nextStop, nextStopTimeDiff) = tramStopWithLastTramTimeDiffList[i + 1]
                val currentStopMinutesFromTram = currentStopTimeDiff.minutesFromTram.absoluteValue
                val nextStopMinutesFromTram = nextStopTimeDiff.minutesFromTram.absoluteValue

                val timesFromDiff = currentStopTimeDiff.minutesFromTram - nextStopTimeDiff.minutesFromTram
                val timesToDiff = currentStopTimeDiff.minutesToTram - nextStopTimeDiff.minutesToTram
                if (timesFromDiff != timesToDiff || currentStopMinutesFromTram <= nextStopMinutesFromTram) {
                    // tram detected between those stops
                    val stopsToAdd = if (currentStopMinutesFromTram <  nextStopTimeDiff.minutesToTram) {
                        currentStop
                    } else {
                        nextStop
                    }
                    if (!stopsWithTrams.contains(stopsToAdd)) {
                        stopsWithTrams.add(stopsToAdd)
                    }
                }
            }
        }
        return stopsWithTrams
    }

    /**
     * @param minutesFromTram minutes from the last tram, should be negative
     * @param minutesToTram minutes to next tram should be positive
     */
    data class LastTramTimeDiff(val minutesFromTram: Int, val minutesToTram: Int)

    data class TramStopWithLastTramTimeDiff(val tramStop: TramStop, val lastTramTimeDiff: LastTramTimeDiff)
}

