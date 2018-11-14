package pl.mobite.tramp.ui.components.tramline

import io.reactivex.*
import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.data.repositories.TramLineRepository
import pl.mobite.tramp.data.repositories.models.*
import pl.mobite.tramp.ui.components.tramline.TramLineAction.FilterStopsAction
import pl.mobite.tramp.ui.components.tramline.TramLineAction.GetTramLineAction
import pl.mobite.tramp.ui.components.tramline.TramLineResult.FilterStopsResult
import pl.mobite.tramp.ui.components.tramline.TramLineResult.GetTramLineResult
import pl.mobite.tramp.utils.SchedulerProvider
import kotlin.math.absoluteValue


class TramLineActionProcessor(
    private val tramLineRepository: TramLineRepository,
    private val timeTableRepository: TimeTableRepository,
    private val schedulerProvider: SchedulerProvider
): ObservableTransformer<TramLineAction, TramLineResult> {

    override fun apply(actions: Observable<TramLineAction>): ObservableSource<TramLineResult> {
        return actions.publish { shared ->
            Observable.merge(
                listOf(
                    shared.ofType(GetTramLineAction::class.java).compose(getTramLineProcessor),
                    shared.ofType(FilterStopsAction::class.java).compose(getCurrentStopsProcessor)
                )
            )
        }
    }

    private val getTramLineProcessor = ObservableTransformer { actions: Observable<GetTramLineAction> ->
        actions.switchMap { (tramLineDesc) ->
            tramLineRepository.getTramLine(tramLineDesc)
                .toObservable()
                .map { tramLine -> GetTramLineResult.Success(tramLine) }
                .cast(GetTramLineResult::class.java)
                .onErrorReturn { t -> GetTramLineResult.Failure(t) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .startWith(GetTramLineResult.InFlight(tramLineDesc))
        }
    }

    private val getCurrentStopsProcessor = ObservableTransformer { actions: Observable<FilterStopsAction> ->
        actions.switchMap { (query) ->
            val (targetTime, lineName, stops) = query
            Observable
                .fromIterable(stops)
                .concatMap { tramStop -> mapToTramStopWithTramTimeDiff(tramStop, lineName, targetTime) }
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

    private fun mapToTramStopWithTramTimeDiff(
        tramStop: TramStop,
        lineName: String,
        targetTime: TimeEntry
    ): Observable<TramStopWithLastTramTimeDiff> {
        return timeTableRepository.getTimeTableFromLocal(tramStop.id)
            .switchIfEmpty(timeTableRepository.getTimeTableFromRemote(tramStop.id, lineName))
            .flatMap { timeTable -> fetchRemoteTimeTableIfOutdated(timeTable, tramStop.id, lineName) }
            .flatMapMaybe { timeTable -> getTramStopWithLastTramTimeDiff(timeTable, targetTime, tramStop) }
            .toObservable()
    }

    private fun fetchRemoteTimeTableIfOutdated(
        timeTable: TimeTable, tramStopId: String, lineName: String
    ): Single<TimeTable> {
        return if (timeTable.canBeOutdated) {
            timeTableRepository.getTimeTableFromRemote(tramStopId, lineName)
        } else {
            Single.just(timeTable)
        }
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
        if (targetTime <= sortedTimeTable.first()) {
            val timeAfter = sortedTimeTable.first()
            val timeBefore = sortedTimeTable.last()
            return LastTramTimeDiff(timeBefore - targetTime - 24 * 60, timeAfter - targetTime)
        }

        // handle case when target entry is after last entry
        if (targetTime >= sortedTimeTable.last()) {
            val timeAfter = sortedTimeTable.first()
            val timeBefore = sortedTimeTable.last()
            return LastTramTimeDiff(timeBefore - targetTime, timeAfter - targetTime + 24 * 60)
        }

        // handle other cases
        sortedTimeTable.forEachIndexed { i, timeAfter ->
            if (targetTime < timeAfter && i > 1) {
                val timeBefore = sortedTimeTable[i - 1]
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

                if (currentStopMinutesFromTram < nextStopMinutesFromTram) {
                    // tram detected between those stops
                    if (currentStopMinutesFromTram <  nextStopTimeDiff.minutesToTram) {
                        stopsWithTrams.add(currentStop)
                    } else {
                        stopsWithTrams.add(nextStop)
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