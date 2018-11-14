package pl.mobite.tramp.data.repositories.models


data class TramLineDesc(
    val name: String,
    val direction: String
)

data class TramLine(
    val stops: List<TramStop>
)

data class TramStop(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double
)

data class FilterStopsQuery(
    val targetTime: TimeEntry,
    val lineName: String,
    val stops: List<TramStop>
)