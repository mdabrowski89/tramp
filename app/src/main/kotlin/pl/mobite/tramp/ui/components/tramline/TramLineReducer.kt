package pl.mobite.tramp.ui.components.tramline

import io.reactivex.functions.BiFunction
import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.ui.components.tramline.TramLineResult.GetTramLineResult
import pl.mobite.tramp.ui.models.ViewStateError
import pl.mobite.tramp.ui.models.toTramLineDetails


class TramLineReducer: BiFunction<TramLineViewState, TramLineResult, TramLineViewState> {

    override fun apply(prevState: TramLineViewState, result: TramLineResult): TramLineViewState {
        return when (result) {
            is GetTramLineResult ->
                when (result) {
                    is GetTramLineResult.InFlight ->
                        prevState.getTramLineInProgress()
                    is GetTramLineResult.Success ->
                        prevState.getTramLineCompleted(result.tramLine)
                    is GetTramLineResult.Failure ->
                        prevState.getTramLineError(result.t)
                }
        }
    }
}

fun TramLineViewState.getTramLineInProgress() = this.copy(
    getTramLineInProgress = true,
    getTramLineError = null
)

fun TramLineViewState.getTramLineCompleted(tramLine: TramLine) = this.copy(
    getTramLineInProgress = false,
    tramLine = tramLine.toTramLineDetails(),
    getTramLineError = null
)

fun TramLineViewState.getTramLineError(t: Throwable) = this.copy(
    getTramLineInProgress = false,
    getTramLineError = ViewStateError(t)
)