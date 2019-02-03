package pl.mobite.tramp.ui.components.tramline

import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.data.repositories.TramLineRepository
import pl.mobite.tramp.ui.base.mvi.MviViewModel
import pl.mobite.tramp.ui.base.mvi.SchedulerProvider
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineAction
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineActionProcessor
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineResult


class TramLineViewModel(
    schedulerProvider: SchedulerProvider,
    tramLineRepository: TramLineRepository,
    timeTableRepository: TimeTableRepository,
    initialState: TramLineViewState?
): MviViewModel<TramLineAction, TramLineResult, TramLineViewState>(
    TramLineActionProcessor(
        schedulerProvider,
        tramLineRepository,
        timeTableRepository
    ),
    initialState ?: TramLineViewState.default()
)