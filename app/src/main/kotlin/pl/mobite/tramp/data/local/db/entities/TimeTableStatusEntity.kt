package pl.mobite.tramp.data.local.db.entities

import androidx.room.*


@Entity(
    tableName = "time_table_status",
    foreignKeys = [ForeignKey(
        entity = TramStopEntity::class,
        parentColumns = ["id"],
        childColumns = ["tram_stop_id"]
    )],
    indices = [Index(
        name = "time_table_status_tram_stop_id_index",
        unique = false,
        value = ["tram_stop_id"]
    )]
)
data class TimeTableStatusEntity(
    @PrimaryKey @ColumnInfo(name = "tram_stop_id") val tramStopId: String,
    @ColumnInfo(name = "update_timestamp") val updateTimestamp: Long
)