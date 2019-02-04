package pl.mobite.tramp.ui.components.tramline

import pl.mobite.tramp.ui.base.mvi.MviViewModel
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineAction
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineActionProcessor
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineResult


class TramLineViewModel(initialState: TramLineViewState?): MviViewModel<TramLineAction, TramLineResult, TramLineViewState>(
    TramLineActionProcessor(),
    initialState ?: TramLineViewState.default()
)