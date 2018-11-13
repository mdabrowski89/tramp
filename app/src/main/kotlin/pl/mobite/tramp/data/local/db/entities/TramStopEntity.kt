package pl.mobite.tramp.data.local.db.entities

import androidx.room.*
import pl.mobite.tramp.data.repositories.models.TramStop

@Entity(
    tableName = "tram_stop",
    foreignKeys = [ForeignKey(
        entity = TramLineEntity::class,
        parentColumns = ["id"],
        childColumns = ["tram_line_id"]
    )],
    indices = [Index(
        name = "tram_stop_tram_line_id_index",
        unique = false,
        value = ["tram_line_id"]
    )]
)
data class TramStopEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "tram_line_id") val tramLineId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lng") val lng: Double
)

fun TramStop.toTramStopEntity(tramLineId: Long) = TramStopEntity(id, tramLineId, name, lat, lng)

fun TramStopEntity.toTramStop() = TramStop(id, name, lat, lng)