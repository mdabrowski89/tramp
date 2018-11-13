package pl.mobite.tramp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.mobite.tramp.data.local.db.dao.TimeEntryDao
import pl.mobite.tramp.data.local.db.dao.TimeTableStatusDao
import pl.mobite.tramp.data.local.db.dao.TramDao
import pl.mobite.tramp.data.local.db.entities.TimeEntryEntity
import pl.mobite.tramp.data.local.db.entities.TimeTableStatusEntity
import pl.mobite.tramp.data.local.db.entities.TramLineEntity
import pl.mobite.tramp.data.local.db.entities.TramStopEntity

@Database(
    entities = [TramStopEntity::class, TramLineEntity::class, TimeEntryEntity::class, TimeTableStatusEntity::class],
    version = 2
)
abstract class TrampAppDatabase: RoomDatabase() {

    abstract fun tramDao(): TramDao

    abstract fun timeEntryDao(): TimeEntryDao

    abstract fun timeTableStatusDao(): TimeTableStatusDao
}