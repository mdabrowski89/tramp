package pl.mobite.tramp.ui.components.tramline.processors

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.data.repositories.models.*
import pl.mobite.tramp.ui.components.tramline.TramLineAction.FilterStopsAction
import pl.mobite.tramp.ui.components.tramline.TramLineResult.FilterStopsResult
import pl.mobite.tramp.utils.SchedulerProvider
import kotlin.math.absoluteValue


class FilterStopsProcessor(
    private val timeTableRepository: TimeTableRepository,
    private val schedulerProvider: SchedulerProvider
): ObservableTransformer<FilterStopsAction, FilterStopsResult> {

    override fun apply(actions: Observable<FilterStopsAction>): ObservableSource<FilterStopsResult> {
        return actions.switchMap { (query) ->
            val (targetTime, lineName, stops) = query
            Observable
                .fromIterable(stops)
                .concatMap { tramStop -> mapToTramStopWithTimeTable(tramStop, lineName) }
                .toList()
                .toObservable()
                .concatMapIterable { list -> list }
                .concatMapMaybe { (tramStop, timeTable) -> getTramStopWithLastTramTimeDiff(timeTable, targetTime, tramStop) }
                .toList()
                .map { tramStopWithTimeDiffList -> getTramStopsWithTrams(tramStopWithTimeDiffList) }
                .toObservable()
                .map { currentStops -> FilterStopsResult.Success(currentStops) }
                .cast(FilterStopsResult::class.java)
                .onErrorReturn { t -> FilterStopsResult.Failure(t) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .startWith(FilterStopsResult.InFlight)
        }
    }

    private fun mapToTramStopWithTimeTable(
        tramStop: TramStop,
        lineName: String
    ): Observable<TramStopWithTimeTable> {
        return timeTableRepository.getTimeTableFromLocal(tramStop.id)
            .onErrorResumeNext(Maybe.empty())
            .switchIfEmpty(timeTableRepository.getTimeTableFromRemote(tramStop.id, lineName))
            .map { timeTable -> TramStopWithTimeTable(tramStop, timeTable) }
            .toObservable()
    }

    private fun getTramStopWithLastTramTimeDiff(
        timeTable: TimeTable,
        targetTime: TimeEntry,
        tramStop: TramStop
    ): Maybe<TramStopWithLastTramTimeDiff> {
        val lastTramTimeDiff = getLastTramTimeDiff(targetTime, timeTable.times)
        return if (lastTramTimeDiff != null) {
            Maybe.just(TramStopWithLastTramTimeDiff(tramStop, lastTramTimeDiff))
        } else {
            Maybe.empty<TramStopWithLastTramTimeDiff>()
        }
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

    data class TramStopWithTimeTable(val tramStop: TramStop, val timeTable: TimeTable)
}