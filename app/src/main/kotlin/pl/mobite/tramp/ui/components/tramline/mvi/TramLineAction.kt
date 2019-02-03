package pl.mobite.tramp.ui.components.tramline.mvi

import pl.mobite.tramp.data.repositories.models.FilterStopsQuery
import pl.mobite.tramp.data.repositories.models.TramLineDesc
import pl.mobite.tramp.ui.base.mvi.MviAction


sealed class TramLineAction: MviAction {

    data class GetTramLineAction(val tramLineDesc: TramLineDesc): TramLineAction()

    data class FilterStopsAction(val query: FilterStopsQuery): TramLineAction()
}