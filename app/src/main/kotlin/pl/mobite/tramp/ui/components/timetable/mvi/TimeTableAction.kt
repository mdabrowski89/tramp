package pl.mobite.tramp.ui.components.timetable.mvi

import pl.mobite.tramp.data.repositories.models.TimeTableDesc
import pl.mobite.tramp.ui.base.mvi.MviAction


sealed class TimeTableAction: MviAction {

    data class GetTimeTableAction(val timeTableDesc: TimeTableDesc): TimeTableAction()
}