package pl.mobite.tramp.ui.components.timetable.processors

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.data.repositories.models.TimeTable
import pl.mobite.tramp.ui.components.timetable.TimeTableAction.GetTimeTableAction
import pl.mobite.tramp.ui.components.timetable.TimeTableResult
import pl.mobite.tramp.ui.components.timetable.TimeTableResult.GetTimeTableResult
import pl.mobite.tramp.utils.SchedulerProvider


class GetTimeTableProcessor(
    private val timeTableRepository: TimeTableRepository,
    private val schedulerProvider: SchedulerProvider
): ObservableTransformer<GetTimeTableAction, TimeTableResult> {

    override fun apply(actions: Observable<GetTimeTableAction>): ObservableSource<TimeTableResult> {
        return actions.switchMap { action ->
            val (lineName, _, _, stopId) = action.timeTableDesc
            getTimeTable(lineName, stopId)
                .map { timeTable -> GetTimeTableResult.Success(timeTable) }
                .cast(GetTimeTableResult::class.java)
                .onErrorReturn { t -> GetTimeTableResult.Failure(t) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .startWith(GetTimeTableResult.InFlight(action.timeTableDesc))
        }
    }

    private fun getTimeTable(lineName: String, stopId: String): Observable<TimeTable> {
        return Observable
            .create {emitter ->
                val localTimeTable = try {
                    timeTableRepository.getTimeTableFromLocal(stopId)
                } catch (t: Throwable) {
                    null
                }

                if (localTimeTable != null) {
                    emitter.onNext(localTimeTable)
                    if (localTimeTable.canBeOutdated) {
                        emitter.onNext(timeTableRepository.getTimeTableFromRemote(stopId, lineName))
                    }
                } else {
                    emitter.onNext(timeTableRepository.getTimeTableFromRemote(stopId, lineName))
                }
                emitter.onComplete()
            }
    }
}