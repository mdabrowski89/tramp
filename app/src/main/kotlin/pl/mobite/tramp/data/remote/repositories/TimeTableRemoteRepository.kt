package pl.mobite.tramp.data.remote.repositories

import pl.mobite.tramp.data.repositories.models.TimeTable


interface TimeTableRemoteRepository {

    fun getTimeTable(tramStopId: String, lineName: String): TimeTable
}