package pl.mobite.tramp.ui.components.tramline

import pl.mobite.tramp.data.repositories.models.TramLineDesc


sealed class TramLineAction {

    data class GetTramLineAction(val tramLineDesc: TramLineDesc): TramLineAction()
}