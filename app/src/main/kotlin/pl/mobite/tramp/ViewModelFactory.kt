package pl.mobite.tramp

import androidx.lifecycle.ViewModel
import pl.mobite.tramp.data.local.json.JsonDataProvider
import pl.mobite.tramp.data.local.repositories.TimeTableLocalRepositoryImpl
import pl.mobite.tramp.data.local.repositories.TramLineLocalRepositoryImpl
import pl.mobite.tramp.data.remote.backend.RetrofitProvider
import pl.mobite.tramp.data.remote.backend.TrampBackend
import pl.mobite.tramp.data.remote.repositories.TimeTableRemoteRepositoryImpl
import pl.mobite.tramp.data.repositories.TimeTableRepositoryImpl
import pl.mobite.tramp.data.repositories.TramLineRepositoryImpl
import pl.mobite.tramp.ui.base.mvi.MviViewModelFactory
import pl.mobite.tramp.ui.components.timetable.TimeTableViewModel
import pl.mobite.tramp.ui.components.timetable.TimeTableViewState
import pl.mobite.tramp.ui.components.tramline.TramLineViewModel
import pl.mobite.tramp.ui.components.tramline.TramLineViewState
import pl.mobite.tramp.utils.AndroidSchedulerProvider

class ViewModelFactory: MviViewModelFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {

        val sp = AndroidSchedulerProvider.instance
        val db = TrampApp.instance.database

        val jsonDataProvider by lazy { JsonDataProvider() }
        val tramLineLocalRepository by lazy { TramLineLocalRepositoryImpl(jsonDataProvider, db) }
        val tramLineRepo by lazy { TramLineRepositoryImpl(tramLineLocalRepository) }

        val timeTableLocalRepository by lazy { TimeTableLocalRepositoryImpl(db) }
        val trampBackend by lazy { RetrofitProvider.instance.create(TrampBackend::class.java) }
        val timeTableRemoteRepository by lazy { TimeTableRemoteRepositoryImpl(trampBackend) }
        val timeTableRepo by lazy { TimeTableRepositoryImpl(timeTableLocalRepository, timeTableRemoteRepository) }

        return when (modelClass) {
            TramLineViewModel::class.java -> TramLineViewModel(sp, tramLineRepo, timeTableRepo, args[0] as TramLineViewState?) as T
            TimeTableViewModel::class.java -> TimeTableViewModel(sp, timeTableRepo, args[0] as TimeTableViewState?) as T
            else -> throw IllegalStateException("Unknown view model class")
        }
    }
}
