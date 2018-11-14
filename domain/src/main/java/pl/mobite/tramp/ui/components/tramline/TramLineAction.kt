package pl.mobite.tramp.ui.components.tramline

import pl.mobite.tramp.data.repositories.models.FilterStopsQuery
import pl.mobite.tramp.data.repositories.models.TramLineDesc


sealed class TramLineAction {

    data class GetTramLineAction(val tramLineDesc: TramLineDesc): TramLineAction()

    data class FilterStopsAction(val query: FilterStopsQuery): TramLineAction()
}