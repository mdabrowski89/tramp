package pl.mobite.tramp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.mobite.tramp.data.local.json.JsonDataProvider
import pl.mobite.tramp.data.local.repositories.TramLineLocalRepositoryImpl
import pl.mobite.tramp.data.repositories.TramLineRepositoryImpl
import pl.mobite.tramp.ui.components.tramline.TramLineViewModel
import pl.mobite.tramp.ui.components.tramline.TramLineViewState
import pl.mobite.tramp.utils.AndroidSchedulerProvider

class ViewModelFactory private constructor(private val args: Array<out Any?>): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {

        // TODO: replace creation with DI
        val jsonDataProvider = JsonDataProvider()
        val database = TrampApp.instance.database
        val tramLineLocalRepository = TramLineLocalRepositoryImpl(jsonDataProvider, database)
        val tramLineRepo = TramLineRepositoryImpl(tramLineLocalRepository)
        val sp = AndroidSchedulerProvider.instance

        return when (modelClass) {
            TramLineViewModel::class.java -> TramLineViewModel(tramLineRepo, sp, args[0] as TramLineViewState?) as T
            else -> throw IllegalStateException("Unknown view model class")
        }
    }

    companion object {

        fun getInstance(vararg args: Any?) = ViewModelFactory(args)
    }
}
