package pl.mobite.tramp.ui.components.timetable

import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.ui.base.mvi.MviViewModel
import pl.mobite.tramp.ui.base.mvi.SchedulerProvider
import pl.mobite.tramp.ui.components.timetable.mvi.TimeTableAction
import pl.mobite.tramp.ui.components.timetable.mvi.TimeTableActionProcessor
import pl.mobite.tramp.ui.components.timetable.mvi.TimeTableResult


class TimeTableViewModel(
    schedulerProvider: SchedulerProvider,
    timeTableRepository: TimeTableRepository,
    initialState: TimeTableViewState?
): MviViewModel<TimeTableAction, TimeTableResult, TimeTableViewState>(
    TimeTableActionProcessor(
        schedulerProvider,
        timeTableRepository
    ),
    initialState ?: TimeTableViewState.default()
)