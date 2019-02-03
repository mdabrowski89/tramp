package pl.mobite.tramp.ui.components.timetable.mvi

import pl.mobite.tramp.data.repositories.models.TimeTable
import pl.mobite.tramp.data.repositories.models.TimeTableDesc
import pl.mobite.tramp.ui.base.mvi.MviResult


sealed class TimeTableResult: MviResult {

    sealed class GetTimeTableResult: TimeTableResult() {

        data class InFlight(val timeTableDesc: TimeTableDesc): GetTimeTableResult()

        data class Success(val timeTable: TimeTable): GetTimeTableResult()

        data class Failure(val t: Throwable): GetTimeTableResult()
    }
}