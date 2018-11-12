package pl.mobite.tramp

import android.app.Application
import androidx.room.Room
import pl.mobite.tramp.data.local.db.TrampAppDatabase


class TrampApp: Application() {

    lateinit var database: TrampAppDatabase

    override fun onCreate() {
        super.onCreate()
        instance = this

        initDatabase()
    }

    private fun initDatabase() {
        database = Room.databaseBuilder(applicationContext, TrampAppDatabase::class.java, "tramp-app-database")
            .fallbackToDestructiveMigration()
            .build()
    }

    companion object {

        @JvmStatic
        lateinit var instance: TrampApp
            private set
    }
}