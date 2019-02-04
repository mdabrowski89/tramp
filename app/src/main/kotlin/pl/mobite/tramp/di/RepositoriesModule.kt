package pl.mobite.tramp.di

import org.koin.dsl.module.module
import pl.mobite.tramp.data.local.repositories.TimeTableLocalRepository
import pl.mobite.tramp.data.local.repositories.TimeTableLocalRepositoryImpl
import pl.mobite.tramp.data.local.repositories.TramLineLocalRepository
import pl.mobite.tramp.data.local.repositories.TramLineLocalRepositoryImpl
import pl.mobite.tramp.data.remote.repositories.TimeTableRemoteRepository
import pl.mobite.tramp.data.remote.repositories.TimeTableRemoteRepositoryImpl
import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.data.repositories.TimeTableRepositoryImpl
import pl.mobite.tramp.data.repositories.TramLineRepository
import pl.mobite.tramp.data.repositories.TramLineRepositoryImpl


val repositoriesModule = module {

    factory<TramLineLocalRepository> { TramLineLocalRepositoryImpl(get(), get()) }
    factory<TramLineRepository> { TramLineRepositoryImpl(get()) }

    factory<TimeTableLocalRepository> { TimeTableLocalRepositoryImpl(get(), get(), get()) }
    factory<TimeTableRemoteRepository> { TimeTableRemoteRepositoryImpl(get(), get()) }
    factory<TimeTableRepository> { TimeTableRepositoryImpl(get(), get()) }
}