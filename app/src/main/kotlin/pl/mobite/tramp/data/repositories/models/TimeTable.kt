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

operator fun TimeEntry.compareTo(entry: TimeEntry): Int {
    return if (hour == entry.hour && minute == entry.minute) {
        0
    } else if (hour > entry.hour || (hour == entry.hour && minute > entry.minute)) {
        1
    } else {
        -1
    }
}

operator fun TimeEntry.minus(entry: TimeEntry) = ((hour * 60) + minute) - ((entry.hour * 60) + entry.minute)