package pl.mobite.tramp.data.local.repositories

import pl.mobite.tramp.data.local.db.dao.TimeEntryDao
import pl.mobite.tramp.data.local.db.dao.TimeTableStatusDao
import pl.mobite.tramp.data.local.db.dao.TramDao
import pl.mobite.tramp.data.local.db.entities.TimeTableStatusEntity
import pl.mobite.tramp.data.local.db.entities.toTimeEntry
import pl.mobite.tramp.data.local.db.entities.toTimeEntryEntity
import pl.mobite.tramp.data.repositories.models.TimeTable


class TimeTableLocalRepositoryImpl(
    private val tramDao: TramDao,
    private val timeTableStatusDao: TimeTableStatusDao,
    private val timeEntryDao: TimeEntryDao
): TimeTableLocalRepository {

    override fun getTimeTable(tramStopId: String): TimeTable? {
        val tramStop = tramDao.getTramStop(tramStopId).firstOrNull()
        if (tramStop != null) {
            val updateTimestamp = timeTableStatusDao.getTimeTableStatus(tramStopId).firstOrNull()?.updateTimestamp
            val timeEntries = timeEntryDao.getTimeEntries(tramStopId).map { it.toTimeEntry() }
            if (timeEntries.isEmpty()) {
                return null
            }
            val dataCanBeOutdated = if (updateTimestamp != null) {
                 System.currentTimeMillis() - updateTimestamp > dataValidityInMills
            } else {
                true
            }
            return TimeTable(timeEntries, dataCanBeOutdated)
        }
        return null
    }

    override fun storeTimeTable(tramStopId: String, timeTable: TimeTable): TimeTable {
        val tramStop = tramDao.getTramStop(tramStopId).firstOrNull()
        if (tramStop != null) {
            timeEntryDao.delete(tramStopId)
            timeEntryDao.insert(timeTable.times.map { it.toTimeEntryEntity(tramStopId) })

            timeTableStatusDao.insert(TimeTableStatusEntity(tramStopId, System.currentTimeMillis()))
        }
        return timeTable
    }

    companion object {

        private const val dataValidityInMills = 60 * 60 * 1000L
    }
}