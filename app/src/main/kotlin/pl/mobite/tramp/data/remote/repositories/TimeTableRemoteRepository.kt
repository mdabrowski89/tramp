package pl.mobite.tramp.data.remote.repositories

import io.reactivex.Single
import pl.mobite.tramp.data.repositories.models.TimeTable
import pl.mobite.tramp.data.repositories.models.TimeTableDesc


interface TimeTableRemoteRepository {

    fun getTimeTable(timeTableDesc: TimeTableDesc): Single<TimeTable>
}