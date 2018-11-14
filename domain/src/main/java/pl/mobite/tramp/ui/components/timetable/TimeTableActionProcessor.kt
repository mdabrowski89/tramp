package pl.mobite.tramp.ui.components.timetable

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.ui.components.timetable.TimeTableAction.GetTimeTableAction
import pl.mobite.tramp.ui.components.timetable.processors.GetTimeTableProcessor
import pl.mobite.tramp.utils.SchedulerProvider


class TimeTableActionProcessor(
    timeTableRepository: TimeTableRepository,
    schedulerProvider: SchedulerProvider
): ObservableTransformer<TimeTableAction, TimeTableResult> {

    private val getTimeTableProcessor = GetTimeTableProcessor(
        timeTableRepository,
        schedulerProvider
    )

    override fun apply(actions: Observable<TimeTableAction>): ObservableSource<TimeTableResult> {
        return actions.publish {shared ->
            Observable.merge(listOf(
                shared.ofType(GetTimeTableAction::class.java).compose(getTimeTableProcessor)
            ))
        }
    }
}