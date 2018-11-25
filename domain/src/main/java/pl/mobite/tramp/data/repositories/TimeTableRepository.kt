package pl.mobite.tramp.data.repositories

import pl.mobite.tramp.data.repositories.models.TimeTable


interface TimeTableRepository {

    @Throws(Throwable::class)
    fun getTimeTableFromLocal(tramStopId: String): TimeTable?

    @Throws(Throwable::class)
    fun getTimeTableFromRemote(tramStopId: String, lineName: String): TimeTable
}