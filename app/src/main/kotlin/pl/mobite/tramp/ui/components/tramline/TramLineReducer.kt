package pl.mobite.tramp.ui.components.tramline

import io.reactivex.functions.BiFunction
import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.data.repositories.models.TramLineDesc
import pl.mobite.tramp.data.repositories.models.TramStop
import pl.mobite.tramp.ui.components.tramline.TramLineResult.FilterStopsResult
import pl.mobite.tramp.ui.components.tramline.TramLineResult.GetTramLineResult
import pl.mobite.tramp.ui.models.ViewStateError
import pl.mobite.tramp.ui.models.toTramLineDetails
import pl.mobite.tramp.ui.models.toTramStopDetails


class TramLineReducer: BiFunction<TramLineViewState, TramLineResult, TramLineViewState> {

    override fun apply(prevState: TramLineViewState, result: TramLineResult): TramLineViewState {
        return when (result) {
            is GetTramLineResult ->
                when (result) {
                    is GetTramLineResult.InFlight ->
                        prevState.withTramLineProgress().withTramLineDesc(result.tramLineDesc)
                    is GetTramLineResult.Success ->
                        prevState.withTramLine(result.tramLine)
                    is GetTramLineResult.Failure ->
                        prevState.withTramLineError(result.t)
                }
            is FilterStopsResult ->
                when (result) {
                    is FilterStopsResult.InFlight ->
                        prevState.withMarkedTramStopsProgress()
                    is FilterStopsResult.Success ->
                        prevState.withMarkedTramStops(result.tramStops)
                    is FilterStopsResult.Failure ->
                        prevState.withMarkedTramStopsError(result.t)
                }
        }
    }
}

fun TramLineViewState.withTramLineProgress() = this.copy(
    getTramLineInProgress = true,
    getTramLineError = null
)

fun TramLineViewState.withTramLineDesc(tramLineDesc: TramLineDesc) = this.copy(
    tramLineDetails = tramLineDesc.toTramLineDetails()
)

fun TramLineViewState.withTramLine(tramLine: TramLine) = this.copy(
    getTramLineInProgress = false,
    tramLineStops = tramLine.stops.map { it.toTramStopDetails() },
    getTramLineError = null
)

fun TramLineViewState.withTramLineError(t: Throwable) = this.copy(
    getTramLineInProgress = false,
    getTramLineError = ViewStateError(t)
)

fun TramLineViewState.withMarkedTramStopsProgress() = this.copy(
    getMarkedTramStopIdsInProgress = true,
    getMarkedTramStopIdsError = null
)

fun TramLineViewState.withMarkedTramStops(tramStops: List<TramStop>) = this.copy(
    getMarkedTramStopIdsInProgress = false,
    markedTramStopIds = tramStops.map { it.id },
    getMarkedTramStopIdsError = null
)

fun TramLineViewState.withMarkedTramStopsError(t: Throwable) = this.copy(
    getMarkedTramStopIdsInProgress = false,
    getMarkedTramStopIdsError = ViewStateError(t)
)