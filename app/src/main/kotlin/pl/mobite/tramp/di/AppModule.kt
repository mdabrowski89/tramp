package pl.mobite.tramp.di

import org.koin.dsl.module.module
import pl.mobite.tramp.data.local.json.JsonDataProvider
import pl.mobite.tramp.ui.base.mvi.SchedulerProvider
import pl.mobite.tramp.utils.AndroidSchedulerProvider


val appModule = module {

    single<SchedulerProvider> { AndroidSchedulerProvider.instance }

    factory { JsonDataProvider(get()) }

}