package pl.mobite.tramp.ui.components.tramline

import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.data.repositories.models.TramLineDesc
import pl.mobite.tramp.data.repositories.models.TramStop


sealed class TramLineResult {

    sealed class GetTramLineResult: TramLineResult() {

        data class InFlight(val tramLineDesc: TramLineDesc): GetTramLineResult()

        data class Success(val tramLine: TramLine): GetTramLineResult()

        data class Failure(val t: Throwable): GetTramLineResult()
    }

    sealed class FilterStopsResult: TramLineResult() {

        object InFlight: FilterStopsResult()

        data class Success(val tramStops: List<TramStop>): FilterStopsResult()

        data class Failure(val t: Throwable): FilterStopsResult()
    }
}