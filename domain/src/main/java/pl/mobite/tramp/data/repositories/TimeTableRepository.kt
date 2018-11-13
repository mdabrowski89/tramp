package pl.mobite.tramp.data.repositories

import io.reactivex.Maybe
import io.reactivex.Single
import pl.mobite.tramp.data.repositories.models.TimeTable


interface TimeTableRepository {

    fun getTimeTableFromLocal(tramStopId: String): Maybe<TimeTable>

    fun getTimeTableFromRemote(tramStopId: String, lineName: String): Single<TimeTable>
}