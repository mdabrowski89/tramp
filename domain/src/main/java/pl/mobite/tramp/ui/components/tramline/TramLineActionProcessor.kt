package pl.mobite.tramp.ui.components.tramline

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.data.repositories.TramLineRepository
import pl.mobite.tramp.ui.components.tramline.TramLineAction.FilterCurrentStopsAction
import pl.mobite.tramp.ui.components.tramline.TramLineAction.GetTramLineAction
import pl.mobite.tramp.ui.components.tramline.TramLineResult.FilterCurrentStopsResult
import pl.mobite.tramp.ui.components.tramline.TramLineResult.GetTramLineResult
import pl.mobite.tramp.utils.*


class TramLineActionProcessor(
    private val tramLineRepository: TramLineRepository,
    private val timeTableRepository: TimeTableRepository,
    private val schedulerProvider: SchedulerProvider
): ObservableTransformer<TramLineAction, TramLineResult> {

    override fun apply(actions: Observable<TramLineAction>): ObservableSource<TramLineResult> {
        return actions.publish {shared ->
            Observable.merge(listOf(
                shared.ofType(GetTramLineAction::class.java).compose(getTramLineProcessor),
                shared.ofType(FilterCurrentStopsAction::class.java).compose(getCurrentStopsProcessor)
            ))
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

    private val getCurrentStopsProcessor = ObservableTransformer { actions: Observable<FilterCurrentStopsAction> ->
        actions.switchMap {action ->
            Single.fromCallable {
                val currentTime = getCurrentTime()
                val tramStopWithTimeDiffList = action.tramStops.mapNotNull { tramStop ->
                    val timeTable = timeTableRepository.getTimeTableFromLocal(tramStop.id)
                        .switchIfEmpty(timeTableRepository.getTimeTableFromRemote(tramStop.id, action.lineName))
                        .flatMap { timeTable ->
                            if (timeTable.canBeOutdated) {
                                timeTableRepository.getTimeTableFromRemote(tramStop.id, action.lineName)
                            } else {
                                Single.just(timeTable)
                            }
                        }.blockingGet()

                    val tramTimeDiff = getTramStopTimeDiff(currentTime, timeTable.times)
                    if (tramTimeDiff != null) {
                        TramStopWithTimeDiff(tramStop, tramTimeDiff)
                    } else {
                        null
                    }
                }
                getTramStopsWithTrams(tramStopWithTimeDiffList)
            }.toObservable()
                .map { currentStops -> FilterCurrentStopsResult.Success(currentStops) }
                .cast(FilterCurrentStopsResult::class.java)
                .onErrorReturn { t -> FilterCurrentStopsResult.Failure(t) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .startWith(FilterCurrentStopsResult.InFlight)
        }
    }
}