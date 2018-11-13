package pl.mobite.tramp.data.local.db.entities

import androidx.room.*
import pl.mobite.tramp.data.repositories.models.TimeEntry


@Entity(
    tableName = "time_entry",
    foreignKeys = [ForeignKey(
        entity = TramStopEntity::class,
        parentColumns = ["id"],
        childColumns = ["tram_stop_id"]
    )],
    indices = [Index(
        name = "time_entry_tram_stop_id_index",
        unique = false,
        value = ["tram_stop_id"]
    )]
)
data class TimeEntryEntity(
    @ColumnInfo(name = "tram_stop_id") val tramStopId: String,
    @ColumnInfo(name = "hour") val hour: Int,
    @ColumnInfo(name = "minute") val minute: Int
) {

    @PrimaryKey(autoGenerate = true) var id: Long = 0
}


fun TimeEntry.toTimeEntryEntity(tramStopId: String) = TimeEntryEntity(
    tramStopId,
    hour,
    minute
)

fun TimeEntryEntity.toTimeEntry() = TimeEntry(hour, minute)
