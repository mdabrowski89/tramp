package pl.mobite.tramp.ui.components.timetable

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import pl.mobite.tramp.data.repositories.TimeTableRepository
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
            timeTableRepository.getTimeTableFromLocal(stopId)
                .switchIfEmpty(timeTableRepository.getTimeTableFromRemote(stopId, lineName))
                .flatMapObservable { timeTable ->
                    if (timeTable.canBeOutdated) {
                        timeTableRepository
                            .getTimeTableFromRemote(stopId, lineName)
                            .toObservable()
                            .startWith(timeTable)
                    } else {
                        Observable.just(timeTable)
                    }
                }
                .map { tramLine -> GetTimeTableResult.Success(tramLine) }
                .cast(TimeTableResult::class.java)
                .onErrorReturn { t -> GetTimeTableResult.Failure(t) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .startWith(GetTimeTableResult.InFlight(action.timeTableDesc))
        }
    }
}