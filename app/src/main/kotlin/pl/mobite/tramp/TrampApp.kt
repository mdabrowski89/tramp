package pl.mobite.tramp

import android.app.Application
import androidx.room.Room
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.android.startKoin
import pl.mobite.tramp.data.local.db.TrampAppDatabase
import pl.mobite.tramp.di.appModule


class TrampApp: Application() {

    lateinit var database: TrampAppDatabase

    override fun onCreate() {
        super.onCreate()
        instance = this

        initKoin()
        initDatabase()
        initRxJavaErrorHandler()
    }

    private fun initKoin() {
        startKoin(this, listOf(appModule))
    }

    private fun initDatabase() {
        database = Room.databaseBuilder(applicationContext, TrampAppDatabase::class.java, "tramp-app-database")
            .fallbackToDestructiveMigration()
            .build()
    }

    private fun initRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler { t: Throwable? ->
            if (t is InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
            }
        }
    }

    companion object {

        @JvmStatic
        lateinit var instance: TrampApp
            private set
    }
}