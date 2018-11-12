package pl.mobite.tramp.ui.components.timetable

import io.reactivex.functions.Function
import pl.mobite.tramp.data.repositories.models.TimeTableDesc
import pl.mobite.tramp.ui.components.timetable.TimeTableAction.GetTimeTableAction
import pl.mobite.tramp.ui.components.timetable.TimeTableIntent.GetTimeTableIntent

sealed class TimeTableIntent {

    data class GetTimeTableIntent(val timeTableDesc: TimeTableDesc): TimeTableIntent()

}

class TimeTableIntentInterpreter: Function<TimeTableIntent, TimeTableAction> {

    override fun apply(intent: TimeTableIntent): TimeTableAction {
        return when (intent) {
            is GetTimeTableIntent -> GetTimeTableAction(intent.timeTableDesc)
        }
    }

}