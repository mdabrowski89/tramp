package pl.mobite.tramp.ui.components.tramline

import io.reactivex.functions.Function
import pl.mobite.tramp.data.repositories.models.TramLineDesc
import pl.mobite.tramp.ui.components.tramline.TramLineAction.GetTramLineAction
import pl.mobite.tramp.ui.components.tramline.TramLineIntent.GetTramLineIntent

sealed class TramLineIntent {

    data class GetTramLineIntent(val tramLineDesc: TramLineDesc): TramLineIntent()

}

class TramLineIntentInterpreter: Function<TramLineIntent, TramLineAction> {

    override fun apply(intent: TramLineIntent): TramLineAction {
        return when (intent) {
            is GetTramLineIntent -> GetTramLineAction(intent.tramLineDesc)
        }
    }

}