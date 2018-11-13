package pl.mobite.tramp.ui.components.tramline

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.data.repositories.TramLineRepository
import pl.mobite.tramp.utils.SchedulerProvider


class TramLineViewModel(
    tramLineRepository: TramLineRepository,
    timeTableRepository: TimeTableRepository,
    schedulerProvider: SchedulerProvider,
    initialState: TramLineViewState?
): ViewModel() {

    private lateinit var disposable: Disposable

    private val intentSource = PublishRelay.create<TramLineIntent>()

    val states: Observable<TramLineViewState> by lazy {
        intentSource
            .map(TramLineIntentInterpreter())
            .compose(TramLineActionProcessor(tramLineRepository, timeTableRepository, schedulerProvider))
            .scan(initialState ?: TramLineViewState.default(), TramLineReducer())
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)
    }

    fun processIntents(intents: Observable<TramLineIntent>) {
        disposable = intents.subscribe(intentSource)
    }

    fun dispose() {
        disposable.dispose()
    }
}