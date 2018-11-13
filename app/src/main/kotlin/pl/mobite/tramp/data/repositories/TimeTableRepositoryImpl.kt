package pl.mobite.tramp.data.repositories

import io.reactivex.Maybe
import io.reactivex.Single
import pl.mobite.tramp.data.local.repositories.TimeTableLocalRepository
import pl.mobite.tramp.data.remote.repositories.TimeTableRemoteRepository
import pl.mobite.tramp.data.repositories.models.TimeTable
import pl.mobite.tramp.data.repositories.models.TimeTableDesc
import pl.mobite.tramp.data.repositories.models.TramStop


class TimeTableRepositoryImpl(
    private val timeTableLocalRepository: TimeTableLocalRepository,
    private val timeTableRemoteRepository: TimeTableRemoteRepository
): TimeTableRepository {

    override fun getTimeTableFromLocal(tramStopId: String): Maybe<TimeTable> {
        return timeTableLocalRepository
            .getTimeTable(tramStopId)
    }

    override fun getTimeTableFromRemote(tramStopId: String, lineName: String): Single<TimeTable> {
        return timeTableRemoteRepository
            .getTimeTable(tramStopId, lineName)
            .flatMap { timeTable -> timeTableLocalRepository.storeTimeTable(tramStopId, timeTable) }
    }
}