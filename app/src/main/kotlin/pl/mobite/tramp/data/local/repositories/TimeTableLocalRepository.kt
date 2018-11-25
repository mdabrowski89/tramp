package pl.mobite.tramp.data.local.repositories

import pl.mobite.tramp.data.repositories.models.TimeTable


interface TimeTableLocalRepository {

    fun getTimeTable(tramStopId: String): TimeTable?

    fun storeTimeTable(tramStopId: String, timeTable: TimeTable): TimeTable
}