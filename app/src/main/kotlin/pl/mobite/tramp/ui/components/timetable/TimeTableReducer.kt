package pl.mobite.tramp.ui.components.timetable

import io.reactivex.functions.BiFunction
import pl.mobite.tramp.data.repositories.models.TimeTable
import pl.mobite.tramp.data.repositories.models.TimeTableDesc
import pl.mobite.tramp.ui.components.timetable.TimeTableResult.GetTimeTableResult
import pl.mobite.tramp.ui.models.ViewStateError
import pl.mobite.tramp.ui.models.toTimeTableDetails
import pl.mobite.tramp.ui.models.toTimeTableRows


class TimeTableReducer: BiFunction<TimeTableViewState, TimeTableResult, TimeTableViewState> {

    override fun apply(prevState: TimeTableViewState, result: TimeTableResult): TimeTableViewState {
        return when (result) {
            is GetTimeTableResult ->
                when (result) {
                    is GetTimeTableResult.InFlight ->
                        prevState.withTimeTableProgress().withTimeTableDesc(result.timeTableDesc)
                    is GetTimeTableResult.Success ->
                        prevState.withTimeTable(result.timeTable)
                    is GetTimeTableResult.Failure ->
                        prevState.withTimeTableError(result.t)
                }
        }
    }
}

fun TimeTableViewState.withTimeTableProgress() = this.copy(
    getTimeTableInProgress = true,
    getTimeTableError = null
)

fun TimeTableViewState.withTimeTableDesc(timeTableDesc: TimeTableDesc) = this.copy(
    timeTableDetails = timeTableDesc.toTimeTableDetails()
)

fun TimeTableViewState.withTimeTable(timeTable: TimeTable) = this.copy(
    getTimeTableInProgress = false,
    timeTableRows = timeTable.toTimeTableRows(),
    getTimeTableError = null
)

fun TimeTableViewState.withTimeTableError(t: Throwable) = this.copy(
    getTimeTableInProgress = false,
    getTimeTableError = ViewStateError(t)
)