package pl.mobite.tramp.ui.components.tramline

import io.reactivex.functions.Function
import pl.mobite.tramp.data.repositories.models.TramLineDesc
import pl.mobite.tramp.ui.components.tramline.TramLineAction.FilterCurrentStopsAction
import pl.mobite.tramp.ui.components.tramline.TramLineAction.GetTramLineAction
import pl.mobite.tramp.ui.components.tramline.TramLineIntent.FilterCurrentStopsIntent
import pl.mobite.tramp.ui.components.tramline.TramLineIntent.GetTramLineIntent
import pl.mobite.tramp.ui.models.TramStopDetails
import pl.mobite.tramp.ui.models.toTramStop

sealed class TramLineIntent {

    data class GetTramLineIntent(val tramLineDesc: TramLineDesc): TramLineIntent()

    data class FilterCurrentStopsIntent(val lineName: String, val tramStops: List<TramStopDetails>): TramLineIntent()
}

class TramLineIntentInterpreter: Function<TramLineIntent, TramLineAction> {

    override fun apply(intent: TramLineIntent): TramLineAction {
        return when (intent) {
            is GetTramLineIntent -> GetTramLineAction(intent.tramLineDesc)
            is FilterCurrentStopsIntent -> FilterCurrentStopsAction(
                intent.lineName,
                intent.tramStops.map { it.toTramStop() })
        }
    }

}