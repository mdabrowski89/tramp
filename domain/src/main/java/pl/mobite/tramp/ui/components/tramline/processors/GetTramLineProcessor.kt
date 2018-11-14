package pl.mobite.tramp.ui.components.tramline.processors

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import pl.mobite.tramp.data.repositories.TramLineRepository
import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.data.repositories.models.TramLineDesc
import pl.mobite.tramp.ui.components.tramline.TramLineAction.GetTramLineAction
import pl.mobite.tramp.ui.components.tramline.TramLineResult.GetTramLineResult
import pl.mobite.tramp.utils.SchedulerProvider


class GetTramLineProcessor(
    private val tramLineRepository: TramLineRepository,
    private val schedulerProvider: SchedulerProvider
): ObservableTransformer<GetTramLineAction, GetTramLineResult> {

    override fun apply(actions: Observable<GetTramLineAction>): ObservableSource<GetTramLineResult> {
        return actions.switchMap { (tramLineDesc) ->
            getTramLine(tramLineDesc)
                .map { tramLine -> GetTramLineResult.Success(tramLine) }
                .cast(GetTramLineResult::class.java)
                .onErrorReturn { t -> GetTramLineResult.Failure(t) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .startWith(GetTramLineResult.InFlight(tramLineDesc))
        }
    }

    private fun getTramLine(tramLineDesc: TramLineDesc): Observable<TramLine> {
        return tramLineRepository.getTramLineFromLocal(tramLineDesc)
            .onErrorResumeNext(Maybe.empty())
            .switchIfEmpty(tramLineRepository.getTramLineFromRemote(tramLineDesc))
            .flatMapObservable { tramLine ->
                if (tramLine.canBeOutdated) {
                    // if it is outdated it is always local data so refresh from backend
                    tramLineRepository
                        .getTramLineFromRemote(tramLineDesc)
                        .toObservable()
                        .startWith(tramLine)
                } else {
                    Observable.just(tramLine)
                }
            }
    }

}