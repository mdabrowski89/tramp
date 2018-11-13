package pl.mobite.tramp.data.local.repositories

import io.reactivex.Maybe
import io.reactivex.Single
import pl.mobite.tramp.data.repositories.models.TimeTable


interface TimeTableLocalRepository {

    fun getTimeTable(tramStopId: String): Maybe<TimeTable>

    fun storeTimeTable(tramStopId: String, timeTable: TimeTable): Single<TimeTable>
}