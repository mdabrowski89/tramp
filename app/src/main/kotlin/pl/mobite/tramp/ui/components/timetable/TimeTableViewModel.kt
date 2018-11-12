package pl.mobite.tramp.ui.components.timetable

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.utils.SchedulerProvider


class TimeTableViewModel(
    timeTableRepository: TimeTableRepository,
    schedulerProvider: SchedulerProvider,
    initialState: TimeTableViewState?
): ViewModel() {

    private lateinit var disposable: Disposable

    private val intentSource = PublishRelay.create<TimeTableIntent>()

    val states: Observable<TimeTableViewState> by lazy {
        intentSource
            .map(TimeTableIntentInterpreter())
            .compose(TimeTableActionProcessor(timeTableRepository, schedulerProvider))
            .scan(initialState ?: TimeTableViewState.default(), TimeTableReducer())
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)
    }

    fun processIntents(intents: Observable<TimeTableIntent>) {
        disposable = intents.subscribe(intentSource)
    }

    fun dispose() {
        disposable.dispose()
    }
}