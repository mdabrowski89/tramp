package pl.mobite.tramp.data.repositories

import pl.mobite.tramp.data.local.repositories.TimeTableLocalRepository
import pl.mobite.tramp.data.remote.repositories.TimeTableRemoteRepository
import pl.mobite.tramp.data.repositories.models.TimeTable


class TimeTableRepositoryImpl(
    private val timeTableLocalRepository: TimeTableLocalRepository,
    private val timeTableRemoteRepository: TimeTableRemoteRepository
): TimeTableRepository {

    override fun getTimeTableFromLocal(tramStopId: String): TimeTable? {
        return timeTableLocalRepository
            .getTimeTable(tramStopId)
    }

    override fun getTimeTableFromRemote(tramStopId: String, lineName: String): TimeTable {
        val timeTable = timeTableRemoteRepository.getTimeTable(tramStopId, lineName)
        timeTableLocalRepository.storeTimeTable(tramStopId, timeTable)
        return timeTable
    }
}