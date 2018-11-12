package pl.mobite.tramp.data.local.repositories

import io.reactivex.Maybe
import pl.mobite.tramp.data.repositories.models.TimeTable
import pl.mobite.tramp.data.repositories.models.TimeTableDesc


class TimeTableLocalRepositoryImpl: TimeTableLocalRepository {

    override fun getTimeTable(timeTableDesc: TimeTableDesc): Maybe<TimeTable> {
        // TODO: read data from local db
        return Maybe.empty()
    }
}