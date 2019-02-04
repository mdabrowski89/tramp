package pl.mobite.tramp.ui.components.timetable

import pl.mobite.tramp.ui.base.mvi.MviViewModel
import pl.mobite.tramp.ui.components.timetable.mvi.TimeTableAction
import pl.mobite.tramp.ui.components.timetable.mvi.TimeTableActionProcessor
import pl.mobite.tramp.ui.components.timetable.mvi.TimeTableResult


class TimeTableViewModel(initialState: TimeTableViewState?): MviViewModel<TimeTableAction, TimeTableResult, TimeTableViewState>(
    TimeTableActionProcessor(),
    initialState ?: TimeTableViewState.default()
)