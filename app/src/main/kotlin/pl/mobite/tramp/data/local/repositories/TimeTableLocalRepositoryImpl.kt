package pl.mobite.tramp.data.local.repositories

import io.reactivex.Maybe
import io.reactivex.Single
import pl.mobite.tramp.data.local.db.TrampAppDatabase
import pl.mobite.tramp.data.local.db.entities.TimeTableStatusEntity
import pl.mobite.tramp.data.local.db.entities.toTimeEntry
import pl.mobite.tramp.data.local.db.entities.toTimeEntryEntidy
import pl.mobite.tramp.data.repositories.models.TimeTable
import pl.mobite.tramp.data.repositories.models.TimeTableDesc


class TimeTableLocalRepositoryImpl(
    private val database: TrampAppDatabase
): TimeTableLocalRepository {

    override fun getTimeTable(timeTableDesc: TimeTableDesc): Maybe<TimeTable> {
        return Maybe.fromCallable {
            val tramStop = database.tramDao().getTramStop(timeTableDesc.stopId).firstOrNull()
            if (tramStop != null) {
                val tramStopId = tramStop.id
                val updateTimestamp = database.timeTableStatusDao().getTimeTableStatus(tramStopId).firstOrNull()?.updateTimestamp
                val timeEntries = database.timeEntryDao().getTimeEntries(tramStopId).map { it.toTimeEntry() }
                if (updateTimestamp != null) {
                    val dataCanBeOutdated = System.currentTimeMillis() - updateTimestamp > dataValidityInMills
                    return@fromCallable TimeTable(timeEntries, dataCanBeOutdated)
                }
            }
            null
        }
    }

    override fun storeTimeTable(timeTableDesc: TimeTableDesc, timeTable: TimeTable): Single<TimeTable> {
        return Single.fromCallable {
            val tramStop = database.tramDao().getTramStop(timeTableDesc.stopId).firstOrNull()
            if (tramStop != null) {
                val tramStopId = tramStop.id
                val timeEntryDao = database.timeEntryDao()

                timeEntryDao.delete(tramStopId)
                timeEntryDao.insert(timeTable.times.map { it.toTimeEntryEntidy(tramStopId) })

                database.timeTableStatusDao().insert(TimeTableStatusEntity(tramStopId, System.currentTimeMillis()))
            }
            timeTable
        }
    }

    companion object {

        private const val dataValidityInMills = 60 * 60 * 1000L
    }
}