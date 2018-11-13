package pl.mobite.tramp.data.repositories

import io.reactivex.Maybe
import io.reactivex.Single
import pl.mobite.tramp.data.local.repositories.TimeTableLocalRepository
import pl.mobite.tramp.data.remote.repositories.TimeTableRemoteRepository
import pl.mobite.tramp.data.repositories.models.TimeTable
import pl.mobite.tramp.data.repositories.models.TimeTableDesc


class TimeTableRepositoryImpl(
    private val timeTableLocalRepository: TimeTableLocalRepository,
    private val timeTableRemoteRepository: TimeTableRemoteRepository
): TimeTableRepository {

    override fun getTimeTableFromLocal(timeTableDesc: TimeTableDesc): Maybe<TimeTable> {
        return timeTableLocalRepository
            .getTimeTable(timeTableDesc)
    }

    override fun getTimeTableFromRemote(timeTableDesc: TimeTableDesc): Single<TimeTable> {
        return timeTableRemoteRepository
            .getTimeTable(timeTableDesc)
            .flatMap { timeTable -> timeTableLocalRepository.storeTimeTable(timeTableDesc, timeTable) }
    }
}