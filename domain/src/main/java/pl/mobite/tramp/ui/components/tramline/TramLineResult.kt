package pl.mobite.tramp.ui.components.tramline

import pl.mobite.tramp.data.repositories.models.TramLine


sealed class TramLineResult {

    sealed class GetTramLineResult: TramLineResult() {

        object InFlight: GetTramLineResult()

        data class Success(val tramLine: TramLine): GetTramLineResult()

        data class Failure(val t: Throwable): GetTramLineResult()
    }
}