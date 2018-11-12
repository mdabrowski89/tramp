package pl.mobite.tramp.data.repositories

import io.reactivex.Maybe
import io.reactivex.Single
import pl.mobite.tramp.data.repositories.models.TimeTable
import pl.mobite.tramp.data.repositories.models.TimeTableDesc


interface TimeTableRepository {

    fun getTimeTableFromLocal(timeTableDesc: TimeTableDesc): Maybe<TimeTable>

    fun getTimeTableFromRemote(timeTableDesc: TimeTableDesc): Single<TimeTable>
}