package pl.mobite.tramp.ui.components.timetable

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.data.repositories.models.TimeTable
import pl.mobite.tramp.ui.components.timetable.TimeTableAction.GetTimeTableAction
import pl.mobite.tramp.ui.components.timetable.TimeTableResult.GetTimeTableResult
import pl.mobite.tramp.utils.SchedulerProvider


class TimeTableActionProcessor(
    private val timeTableRepository: TimeTableRepository,
    private val schedulerProvider: SchedulerProvider
): ObservableTransformer<TimeTableAction, TimeTableResult> {

    override fun apply(actions: Observable<TimeTableAction>): ObservableSource<TimeTableResult> {
        return actions.publish {shared ->
            Observable.merge(listOf(
                shared.ofType(GetTimeTableAction::class.java).compose(getTramStopsProcessor)
            ))
        }
    }

    private val getTramStopsProcessor = ObservableTransformer { actions: Observable<GetTimeTableAction> ->
        actions.switchMap { action ->
            val (lineName, _, _, stopId) = action.timeTableDesc
            getTimeTable(lineName, stopId)
                .map { timeTable -> GetTimeTableResult.Success(timeTable) }
                .cast(TimeTableResult::class.java)
                .onErrorReturn { t -> GetTimeTableResult.Failure(t) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .startWith(GetTimeTableResult.InFlight(action.timeTableDesc))
        }
    }

    private fun getTimeTable(lineName: String, stopId: String): Observable<TimeTable> {
        return timeTableRepository.getTimeTableFromLocal(stopId)
            .switchIfEmpty(timeTableRepository.getTimeTableFromRemote(stopId, lineName))
            .flatMapObservable { timeTable ->
                if (timeTable.canBeOutdated) {
                    // if it is outdated it is always local data so refresh from backend
                    timeTableRepository
                        .getTimeTableFromRemote(stopId, lineName)
                        .toObservable()
                        .startWith(timeTable)
                } else {
                    Observable.just(timeTable)
                }
            }
    }
}