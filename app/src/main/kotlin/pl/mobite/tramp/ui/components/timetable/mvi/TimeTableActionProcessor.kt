package pl.mobite.tramp.ui.components.timetable.mvi

import io.reactivex.Observable
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.ui.base.mvi.*
import pl.mobite.tramp.ui.components.timetable.mvi.TimeTableAction.GetTimeTableAction
import pl.mobite.tramp.ui.components.timetable.mvi.TimeTableResult.GetTimeTableResult.*


class TimeTableActionProcessor: MviActionsProcessor<TimeTableAction, TimeTableResult>(), KoinComponent {

    private val schedulerProvider: SchedulerProvider by inject()
    private val timeTableRepository: TimeTableRepository by inject()

    override fun getActionProcessors(shared: Observable<TimeTableAction>) = listOf(
        shared.connect(getTimeTableProcessor)
    )

    private val getTimeTableProcessor = createActionProcessor<GetTimeTableAction, TimeTableResult>(
        schedulerProvider,
        { InFlight(it.timeTableDesc) },
        { Failure(it) }
    ) { action ->
        val (lineName, _, _, stopId) = action.timeTableDesc
        val localTimeTable = try {
            timeTableRepository.getTimeTableFromLocal(stopId)
        } catch (t: Throwable) {
            null
        }

        if (localTimeTable != null) {
            onNextSafe(Success(localTimeTable))
        }
        if (localTimeTable == null || localTimeTable.canBeOutdated) {
            val remoteTimeTable = timeTableRepository.getTimeTableFromRemote(stopId, lineName)
            onNextSafe(Success(remoteTimeTable))
        }
        onCompleteSafe()
    }
}