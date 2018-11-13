package pl.mobite.tramp.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.mobite.tramp.data.local.db.entities.TimeEntryEntity


@Dao
interface TimeEntryDao {

    @Query("SELECT * FROM time_entry WHERE tram_stop_id IN (:tramStopId)")
    fun getTimeEntries(tramStopId: String): List<TimeEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(timeEntries: List<TimeEntryEntity>)

    @Query("DELETE FROM time_entry WHERE tram_stop_id IN (:tramStopId)")
    fun delete(tramStopId: String)
}