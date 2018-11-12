package pl.mobite.tramp.ui.components.tramline

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import pl.mobite.tramp.data.repositories.TramLineRepository
import pl.mobite.tramp.ui.components.tramline.TramLineAction.GetTramLineAction
import pl.mobite.tramp.ui.components.tramline.TramLineResult.GetTramLineResult
import pl.mobite.tramp.utils.SchedulerProvider


class TramLineActionProcessor(
    private val tramLineRepository: TramLineRepository,
    private val schedulerProvider: SchedulerProvider
): ObservableTransformer<TramLineAction, TramLineResult> {

    override fun apply(actions: Observable<TramLineAction>): ObservableSource<TramLineResult> {
        return actions.publish {shared ->
            Observable.merge(listOf(
                shared.ofType(GetTramLineAction::class.java).compose(getTramLineProcessor)
            ))
        }
    }

    private val getTramLineProcessor = ObservableTransformer { actions: Observable<GetTramLineAction> ->
        actions.switchMap { action ->
            tramLineRepository.getTramLine(action.tramLineDesc)
                .toObservable()
                .map { tramLine -> GetTramLineResult.Success(tramLine) }
                .cast(TramLineResult::class.java)
                .onErrorReturn { t -> GetTramLineResult.Failure(t) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .startWith(GetTramLineResult.InFlight)
        }
    }
}