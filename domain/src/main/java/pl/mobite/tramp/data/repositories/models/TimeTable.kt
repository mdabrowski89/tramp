package pl.mobite.tramp.data.repositories.models


data class TimeTableDesc(
    val lineName: String,
    val lineDirection: String,
    val stopName: String,
    val stopId: String
)

data class TimeTable(
    val times: List<TimeEntry>,
    val canBeOutdated: Boolean
)

data class TimeEntry(
    val hour: Int,
    val minute: Int
)