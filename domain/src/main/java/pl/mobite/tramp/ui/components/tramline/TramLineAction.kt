package pl.mobite.tramp.ui.components.tramline

import pl.mobite.tramp.data.repositories.models.TramLineDesc
import pl.mobite.tramp.data.repositories.models.TramStop


sealed class TramLineAction {

    data class GetTramLineAction(val tramLineDesc: TramLineDesc): TramLineAction()

    data class FilterCurrentStopsAction(val lineName: String, val tramStops: List<TramStop>): TramLineAction()
}