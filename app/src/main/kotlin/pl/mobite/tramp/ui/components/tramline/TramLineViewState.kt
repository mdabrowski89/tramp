package pl.mobite.tramp.ui.components.tramline

import kotlinx.android.parcel.Parcelize
import pl.mobite.tramp.ui.base.mvi.MviViewState
import pl.mobite.tramp.ui.base.mvi.ViewStateEvent
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineResult
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineResult.FilterStopsResult
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineResult.GetTramLineResult
import pl.mobite.tramp.ui.models.TramLineDetails
import pl.mobite.tramp.ui.models.TramStopDetails
import pl.mobite.tramp.ui.models.toTramLineDetails
import pl.mobite.tramp.ui.models.toTramStopDetails

@Parcelize
data class TramLineViewState(
    val getTramLineInProgress: Boolean,
    val tramLineDetails: TramLineDetails?,
    val tramLineStops: List<TramStopDetails>?,
    val getMarkedTramStopIdsInProgress: Boolean,
    val markedTramStopIds: List<String>?,
    val getTramLineError: ViewStateEvent<Throwable>?,
    val getMarkedTramStopIdsError: ViewStateEvent<Throwable>?
): MviViewState<TramLineResult> {

    companion object {
        fun default() = TramLineViewState(false, null, null, false, null, null, null)
    }

    override fun isSavable() = !getTramLineInProgress

    override fun reduce(result: TramLineResult): TramLineViewState {
        return when(result) {
            is GetTramLineResult.InFlight -> result.reduce()
            is GetTramLineResult.Success -> result.reduce()
            is GetTramLineResult.Failure -> result.reduce()
            is FilterStopsResult.InFlight -> result.reduce()
            is FilterStopsResult.Success -> result.reduce()
            is FilterStopsResult.Failure -> result.reduce()
        }
    }

    private fun GetTramLineResult.InFlight.reduce() = copy(
        getTramLineInProgress = true,
        getTramLineError = null,
        tramLineDetails = tramLineDesc.toTramLineDetails()
    )

    private fun GetTramLineResult.Success.reduce() = copy(
        getTramLineInProgress = false,
        tramLineStops = tramLine.stops.map { it.toTramStopDetails() },
        getTramLineError = null
    )

    private fun GetTramLineResult.Failure.reduce() = copy(
        getTramLineInProgress = false,
        getTramLineError = ViewStateEvent(t)
    )

    private fun FilterStopsResult.InFlight.reduce() = copy(
        getMarkedTramStopIdsInProgress = true,
        getMarkedTramStopIdsError = null
    )

    private fun FilterStopsResult.Success.reduce() = copy(
        getMarkedTramStopIdsInProgress = false,
        markedTramStopIds = tramStops.map { it.id },
        getMarkedTramStopIdsError = null
    )

    private fun FilterStopsResult.Failure.reduce() = copy(
        getMarkedTramStopIdsInProgress = false,
        getMarkedTramStopIdsError = ViewStateEvent(t)
    )
}