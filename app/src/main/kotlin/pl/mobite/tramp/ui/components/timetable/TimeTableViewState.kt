package pl.mobite.tramp.ui.components.timetable

import kotlinx.android.parcel.Parcelize
import pl.mobite.tramp.ui.base.mvi.MviViewState
import pl.mobite.tramp.ui.base.mvi.ViewStateEvent
import pl.mobite.tramp.ui.components.timetable.mvi.TimeTableResult
import pl.mobite.tramp.ui.models.TimeTableDetails
import pl.mobite.tramp.ui.models.TimeTableRow
import pl.mobite.tramp.ui.models.toTimeTableDetails
import pl.mobite.tramp.ui.models.toTimeTableRows

@Parcelize
data class TimeTableViewState(
    val getTimeTableInProgress: Boolean,
    val timeTableDetails: TimeTableDetails?,
    val timeTableRows: List<TimeTableRow>?,
    val getTimeTableError: ViewStateEvent<Throwable>?
): MviViewState<TimeTableResult> {

    companion object {

        fun default() = TimeTableViewState(false, null, null, null)
    }

    override fun isSavable() = !getTimeTableInProgress

    override fun reduce(result: TimeTableResult): TimeTableViewState {
        return when(result) {
            is TimeTableResult.GetTimeTableResult.InFlight -> result.reduce()
            is TimeTableResult.GetTimeTableResult.Success -> result.reduce()
            is TimeTableResult.GetTimeTableResult.Failure -> result.reduce()
        }
    }

    private fun TimeTableResult.GetTimeTableResult.InFlight.reduce() = copy(
        getTimeTableInProgress = true,
        getTimeTableError = null,
        timeTableDetails = timeTableDesc.toTimeTableDetails()
    )

    private fun TimeTableResult.GetTimeTableResult.Success.reduce() = copy(
        getTimeTableInProgress = false,
        timeTableRows = timeTable.toTimeTableRows(),
        getTimeTableError = null
    )

    private fun TimeTableResult.GetTimeTableResult.Failure.reduce() = copy(
        getTimeTableInProgress = false,
        getTimeTableError = ViewStateEvent(t)
    )
}