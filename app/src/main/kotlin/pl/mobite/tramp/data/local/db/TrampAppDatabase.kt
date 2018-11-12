package pl.mobite.tramp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.mobite.tramp.data.local.db.dao.TramDao
import pl.mobite.tramp.data.local.db.entities.TramLineEntity
import pl.mobite.tramp.data.local.db.entities.TramStopEntity

@Database(entities = [TramStopEntity::class, TramLineEntity::class], version = 1)
abstract class TrampAppDatabase: RoomDatabase() {

    abstract fun tramDao(): TramDao
}