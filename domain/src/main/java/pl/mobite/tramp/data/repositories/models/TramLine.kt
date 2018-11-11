package pl.mobite.tramp.data.repositories.models


data class TramLineDesc(
    val name: String,
    val direction: String
)

data class TramLine(
    val desc: TramLineDesc,
    val stops: List<TramStop>
)

data class TramStop(
    val id: Long,
    val name: String,
    val lat: Double,
    val lng: Double
)