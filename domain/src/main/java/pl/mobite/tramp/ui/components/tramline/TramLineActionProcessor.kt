package pl.mobite.tramp.ui.components.tramline

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.data.repositories.TramLineRepository
import pl.mobite.tramp.ui.components.tramline.TramLineAction.FilterStopsAction
import pl.mobite.tramp.ui.components.tramline.TramLineAction.GetTramLineAction
import pl.mobite.tramp.ui.components.tramline.processors.FilterStopsProcessor
import pl.mobite.tramp.ui.components.tramline.processors.GetTramLineProcessor
import pl.mobite.tramp.utils.SchedulerProvider


class TramLineActionProcessor(
    tramLineRepository: TramLineRepository,
    timeTableRepository: TimeTableRepository,
    schedulerProvider: SchedulerProvider
): ObservableTransformer<TramLineAction, TramLineResult> {

    private val getTramLineProcessor = GetTramLineProcessor(
        tramLineRepository,
        schedulerProvider
    )

    private val filterStopsProcessor = FilterStopsProcessor(
        timeTableRepository,
        schedulerProvider
    )

    override fun apply(actions: Observable<TramLineAction>): ObservableSource<TramLineResult> {
        return actions.publish { shared ->
            Observable.merge(
                listOf(
                    shared.ofType(GetTramLineAction::class.java).compose(getTramLineProcessor),
                    shared.ofType(FilterStopsAction::class.java).compose(filterStopsProcessor)
                )
            )
        }
    }
}