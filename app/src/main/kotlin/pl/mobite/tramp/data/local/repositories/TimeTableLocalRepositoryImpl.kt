package pl.mobite.tramp.data.local.repositories

import io.reactivex.Maybe
import io.reactivex.Single
import pl.mobite.tramp.data.local.db.TrampAppDatabase
import pl.mobite.tramp.data.local.db.entities.TimeTableStatusEntity
import pl.mobite.tramp.data.local.db.entities.toTimeEntry
import pl.mobite.tramp.data.local.db.entities.toTimeEntryEntity
import pl.mobite.tramp.data.repositories.models.TimeTable


class TimeTableLocalRepositoryImpl(
    private val database: TrampAppDatabase
): TimeTableLocalRepository {

    override fun getTimeTable(tramStopId: String): Maybe<TimeTable> {
        return Maybe.fromCallable {
            val tramStop = database.tramDao().getTramStop(tramStopId).firstOrNull()
            if (tramStop != null) {
                val updateTimestamp = database.timeTableStatusDao().getTimeTableStatus(tramStopId).firstOrNull()?.updateTimestamp
                val timeEntries = database.timeEntryDao().getTimeEntries(tramStopId).map { it.toTimeEntry() }
                if (timeEntries.isEmpty()) {
                    return@fromCallable null
                }
                val dataCanBeOutdated = if (updateTimestamp != null) {
                     System.currentTimeMillis() - updateTimestamp > dataValidityInMills
                } else {
                    true
                }
                return@fromCallable TimeTable(timeEntries, dataCanBeOutdated)
            }
            return@fromCallable null
        }
    }

    override fun storeTimeTable(tramStopId: String, timeTable: TimeTable): Single<TimeTable> {
        return Single.fromCallable {
            val tramStop = database.tramDao().getTramStop(tramStopId).firstOrNull()
            if (tramStop != null) {
                val timeEntryDao = database.timeEntryDao()

                timeEntryDao.delete(tramStopId)
                timeEntryDao.insert(timeTable.times.map { it.toTimeEntryEntity(tramStopId) })

                database.timeTableStatusDao().insert(TimeTableStatusEntity(tramStopId, System.currentTimeMillis()))
            }
            timeTable
        }
    }

    companion object {

        private const val dataValidityInMills = 60 * 60 * 1000L
    }
}