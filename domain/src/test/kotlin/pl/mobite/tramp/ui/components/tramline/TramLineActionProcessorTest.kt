package pl.mobite.tramp.ui.components.tramline

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.data.repositories.TramLineRepository
import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.data.repositories.models.TramLineDesc
import pl.mobite.tramp.ui.components.tramline.TramLineAction.GetTramLineAction
import pl.mobite.tramp.ui.components.tramline.TramLineResult.GetTramLineResult
import pl.mobite.tramp.utils.ImmediateSchedulerProvider
import pl.mobite.tramp.utils.lazyMock
import pl.mobite.tramp.utils.lazyPowerMock


@RunWith(PowerMockRunner::class)
@PrepareForTest(TramLine::class, TramLineDesc::class)
class TramLineActionProcessorTest {

    private val tramLineRepositoryMock: TramLineRepository by lazyMock()
    private val timeTableRepositoryMock: TimeTableRepository by lazyMock()
    private val tramLineDescMock: TramLineDesc by lazyPowerMock()
    private val tramLineMock: TramLine by lazyPowerMock()

    @Test
    fun testGetTramLineActionSuccess() {
        Mockito.`when`(tramLineRepositoryMock.getTramLine(tramLineDescMock)).thenReturn(Single.just(tramLineMock))

        val actions = listOf(
            GetTramLineAction(tramLineDescMock)
        )
        val expectedResults = listOf(
            GetTramLineResult.InFlight(tramLineDescMock),
            GetTramLineResult.Success(tramLineMock)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testGetTramLineActionFailure() {
        Mockito.`when`(tramLineRepositoryMock.getTramLine(tramLineDescMock)).thenReturn(Single.error(dummyException))

        val actions = listOf(
            GetTramLineAction(tramLineDescMock)
        )
        val expectedResults = listOf(
            GetTramLineResult.InFlight(tramLineDescMock),
            GetTramLineResult.Failure(dummyException)
        )

        test(actions, expectedResults)
    }

    private fun test(actions: List<TramLineAction>, expectedResults: List<TramLineResult>) {
        val processor = TramLineActionProcessor(tramLineRepositoryMock, timeTableRepositoryMock,
            ImmediateSchedulerProvider.instance)
        val testObserver = TestObserver<TramLineResult>()

        processor.apply(Observable.fromIterable(actions)).subscribe(testObserver)

        testObserver.assertValueCount(expectedResults.size)

        testObserver.values().forEachIndexed {i, tested ->
            Assert.assertEquals(expectedResults[i], tested)
        }

        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    companion object {

        private val dummyException = Throwable("dummy exception")
    }
}