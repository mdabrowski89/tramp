package pl.mobite.tramp.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import pl.mobite.tramp.data.repositories.models.TramLine

@Entity(
    tableName = "tram_line",
    indices = [Index(
        name = "name_direction_index",
        unique = true,
        value = ["name", "direction"]
    )]
)
data class TramLineEntity(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "direction") val direction: String
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}

fun TramLine.toTramLineEntity() = TramLineEntity(
    this.desc.name,
    this.desc.direction
)