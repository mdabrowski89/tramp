package pl.mobite.tramp.di

import org.koin.dsl.module.module
import pl.mobite.tramp.TrampApp
import pl.mobite.tramp.data.local.json.JsonDataProvider
import pl.mobite.tramp.data.local.repositories.TimeTableLocalRepositoryImpl
import pl.mobite.tramp.data.local.repositories.TramLineLocalRepositoryImpl
import pl.mobite.tramp.data.remote.backend.RetrofitProvider
import pl.mobite.tramp.data.remote.backend.TrampBackend
import pl.mobite.tramp.data.remote.repositories.TimeTableRemoteRepositoryImpl
import pl.mobite.tramp.data.repositories.TimeTableRepositoryImpl
import pl.mobite.tramp.data.repositories.TramLineRepositoryImpl
import pl.mobite.tramp.utils.AndroidSchedulerProvider


val appModule = module {
    single { AndroidSchedulerProvider.instance }
    single { TrampApp.instance.database }

    factory { JsonDataProvider() }
    factory { TramLineLocalRepositoryImpl(get(), get()) }
    factory { TramLineRepositoryImpl(get()) }
    factory { TimeTableLocalRepositoryImpl(get()) }

    factory { RetrofitProvider.instance.create(TrampBackend::class.java) }
    factory { TimeTableRemoteRepositoryImpl(get()) }
    factory { TimeTableRepositoryImpl(get(), get()) }
}