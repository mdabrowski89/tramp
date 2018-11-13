package pl.mobite.tramp.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.mobite.tramp.data.local.db.entities.TimeTableStatusEntity


@Dao
interface TimeTableStatusDao {

    @Query("SELECT * FROM time_table_status WHERE tram_stop_id IN (:tramStopId)")
    fun getTimeTableStatus(tramStopId: String): List<TimeTableStatusEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(timeTableStatusEntity: TimeTableStatusEntity)
}