package pl.mobite.tramp.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.mobite.tramp.data.local.db.entities.TramLineEntity
import pl.mobite.tramp.data.local.db.entities.TramStopEntity


@Dao
interface TramDao {

    @Query("SELECT * FROM tram_line WHERE name IN (:name) AND direction IN (:direction)")
    fun getTramLine(name: String, direction: String): List<TramLineEntity>

    @Query("SELECT * FROM tram_stop WHERE tram_line_id IN (:tramLineId)")
    fun getTramStops(tramLineId: Long): List<TramStopEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tramLine: TramLineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tramStops: List<TramStopEntity>)
}