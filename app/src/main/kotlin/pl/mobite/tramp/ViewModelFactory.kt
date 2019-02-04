package pl.mobite.tramp

import androidx.lifecycle.ViewModel
import pl.mobite.tramp.ui.base.mvi.MviViewModelFactory
import pl.mobite.tramp.ui.components.timetable.TimeTableViewModel
import pl.mobite.tramp.ui.components.timetable.TimeTableViewState
import pl.mobite.tramp.ui.components.tramline.TramLineViewModel
import pl.mobite.tramp.ui.components.tramline.TramLineViewState

class ViewModelFactory: MviViewModelFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {

        return when (modelClass) {
            TramLineViewModel::class.java -> TramLineViewModel(args[0] as TramLineViewState?) as T
            TimeTableViewModel::class.java -> TimeTableViewModel(args[0] as TimeTableViewState?) as T
            else -> throw IllegalStateException("Unknown view model class")
        }
    }
}
