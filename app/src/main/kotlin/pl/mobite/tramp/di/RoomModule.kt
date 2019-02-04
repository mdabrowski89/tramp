package pl.mobite.tramp.di

import androidx.room.Room
import org.koin.dsl.module.module
import pl.mobite.tramp.data.local.db.TrampAppDatabase


val roomModule = module {

    single {
        Room.databaseBuilder(get(), TrampAppDatabase::class.java, "tramp-app-database")
            .fallbackToDestructiveMigration()
            .build()
    }

    factory { get<TrampAppDatabase>().timeEntryDao() }

    factory { get<TrampAppDatabase>().timeTableStatusDao() }

    factory { get<TrampAppDatabase>().tramDao() }
}