package pl.mobite.tramp.data.local.repositories

import io.reactivex.Maybe
import io.reactivex.Single
import pl.mobite.tramp.data.repositories.models.TimeTable
import pl.mobite.tramp.data.repositories.models.TimeTableDesc


interface TimeTableLocalRepository {

    fun getTimeTable(timeTableDesc: TimeTableDesc): Maybe<TimeTable>

    fun storeTimeTable(timeTableDesc: TimeTableDesc, timeTable: TimeTable): Single<TimeTable>
}