package pl.mobite.tramp.ui.components.timetable

import pl.mobite.tramp.data.repositories.models.TimeTableDesc


sealed class TimeTableAction {

    data class GetTimeTableAction(val timeTableDesc: TimeTableDesc): TimeTableAction()
}