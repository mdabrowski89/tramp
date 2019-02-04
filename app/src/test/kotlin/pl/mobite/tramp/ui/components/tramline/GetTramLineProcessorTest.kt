package pl.mobite.tramp.ui.components.tramline

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.data.repositories.TramLineRepository
import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.data.repositories.models.TramLineDesc
import pl.mobite.tramp.ui.base.mvi.SchedulerProvider
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineAction.GetTramLineAction
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineActionProcessor
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineResult
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineResult.GetTramLineResult
import pl.mobite.tramp.utils.ImmediateSchedulerProvider
import pl.mobite.tramp.utils.lazyMock
import pl.mobite.tramp.utils.lazyPowerMock


@RunWith(PowerMockRunner::class)
@PrepareForTest(TramLine::class, TramLineDesc::class)
class GetTramLineProcessorTest {

    private val tramLineRepositoryMock: TramLineRepository by lazyMock()
    private val timeTableRepositoryMock: TimeTableRepository by lazyMock()
    private val localTramLineMock: TramLine by lazyPowerMock()
    private val remoteTramLineMock: TramLine by lazyPowerMock()
    private val tramLineDescMock: TramLineDesc by lazyPowerMock()

    @Test
    fun testGetTramLineLocalEmptyRemoteSuccess() {
        Mockito.`when`(localTramLineMock.canBeOutdated).thenReturn(false)
        Mockito.`when`(remoteTramLineMock.canBeOutdated).thenReturn(false)
        tramLineRepositoryMock.apply {
            Mockito.`when`(getTramLineFromLocal(tramLineDescMock)).thenReturn(null)
            Mockito.`when`(getTramLineFromRemote(tramLineDescMock)).thenReturn(remoteTramLineMock)
        }

        val actions = listOf(
            GetTramLineAction(tramLineDescMock)
        )
        val expectedResults = listOf(
            GetTramLineResult.InFlight(tramLineDescMock),
            GetTramLineResult.Success(remoteTramLineMock)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testGetTramLineLocalEmptyRemoteFailure() {
        Mockito.`when`(localTramLineMock.canBeOutdated).thenReturn(false)
        Mockito.`when`(remoteTramLineMock.canBeOutdated).thenReturn(false)
        tramLineRepositoryMock.apply {
            Mockito.`when`(getTramLineFromLocal(tramLineDescMock)).thenReturn(null)
            Mockito.`when`(getTramLineFromRemote(tramLineDescMock)).thenThrow(remoteDummyException)
        }

        val actions = listOf(
            GetTramLineAction(tramLineDescMock)
        )
        val expectedResults = listOf(
            GetTramLineResult.InFlight(tramLineDescMock),
            GetTramLineResult.Failure(remoteDummyException)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testGetTramLineLocalSuccessRemoteSuccess() {
        Mockito.`when`(localTramLineMock.canBeOutdated).thenReturn(false)
        Mockito.`when`(remoteTramLineMock.canBeOutdated).thenReturn(false)
        tramLineRepositoryMock.apply {
            Mockito.`when`(getTramLineFromLocal(tramLineDescMock)).thenReturn(localTramLineMock)
            Mockito.`when`(getTramLineFromRemote(tramLineDescMock)).thenReturn(remoteTramLineMock)
        }

        val actions = listOf(
            GetTramLineAction(tramLineDescMock)
        )
        val expectedResults = listOf(
            GetTramLineResult.InFlight(tramLineDescMock),
            GetTramLineResult.Success(localTramLineMock)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testGetTramLineLocalSuccessRemoteFailure() {
        Mockito.`when`(localTramLineMock.canBeOutdated).thenReturn(false)
        Mockito.`when`(remoteTramLineMock.canBeOutdated).thenReturn(false)
        tramLineRepositoryMock.apply {
            Mockito.`when`(getTramLineFromLocal(tramLineDescMock)).thenReturn(localTramLineMock)
            Mockito.`when`(getTramLineFromRemote(tramLineDescMock)).thenThrow(remoteDummyException)
        }

        val actions = listOf(
            GetTramLineAction(tramLineDescMock)
        )
        val expectedResults = listOf(
            GetTramLineResult.InFlight(tramLineDescMock),
            GetTramLineResult.Success(localTramLineMock)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testGetTramLineLocalOutdatedRemoteSuccess() {
        Mockito.`when`(localTramLineMock.canBeOutdated).thenReturn(true)
        Mockito.`when`(remoteTramLineMock.canBeOutdated).thenReturn(false)
        tramLineRepositoryMock.apply {
            Mockito.`when`(getTramLineFromLocal(tramLineDescMock)).thenReturn(localTramLineMock)
            Mockito.`when`(getTramLineFromRemote(tramLineDescMock)).thenReturn(remoteTramLineMock)
        }

        val actions = listOf(
            GetTramLineAction(tramLineDescMock)
        )
        val expectedResults = listOf(
            GetTramLineResult.InFlight(tramLineDescMock),
            GetTramLineResult.Success(localTramLineMock),
            GetTramLineResult.Success(remoteTramLineMock)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testGetTramLineLocalOutdatedRemoteFailure() {
        Mockito.`when`(localTramLineMock.canBeOutdated).thenReturn(true)
        Mockito.`when`(remoteTramLineMock.canBeOutdated).thenReturn(false)
        tramLineRepositoryMock.apply {
            Mockito.`when`(getTramLineFromLocal(tramLineDescMock)).thenReturn(localTramLineMock)
            Mockito.`when`(getTramLineFromRemote(tramLineDescMock)).thenThrow(remoteDummyException)
        }

        val actions = listOf(
            GetTramLineAction(tramLineDescMock)
        )
        val expectedResults = listOf(
            GetTramLineResult.InFlight(tramLineDescMock),
            GetTramLineResult.Success(localTramLineMock),
            GetTramLineResult.Failure(remoteDummyException)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testGetTramLineLocalFailureRemoteSuccess() {
        Mockito.`when`(localTramLineMock.canBeOutdated).thenReturn(false)
        Mockito.`when`(remoteTramLineMock.canBeOutdated).thenReturn(false)
        tramLineRepositoryMock.apply {
            Mockito.`when`(getTramLineFromLocal(tramLineDescMock)).thenThrow(localDummyException)
            Mockito.`when`(getTramLineFromRemote(tramLineDescMock)).thenReturn(remoteTramLineMock)
        }

        val actions = listOf(
            GetTramLineAction(tramLineDescMock)
        )
        val expectedResults = listOf(
            GetTramLineResult.InFlight(tramLineDescMock),
            GetTramLineResult.Success(remoteTramLineMock)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testGetTramLineLocalFailureRemoteFailure() {
        Mockito.`when`(localTramLineMock.canBeOutdated).thenReturn(false)
        Mockito.`when`(remoteTramLineMock.canBeOutdated).thenReturn(false)
        tramLineRepositoryMock.apply {
            Mockito.`when`(getTramLineFromLocal(tramLineDescMock)).thenThrow(localDummyException)
            Mockito.`when`(getTramLineFromRemote(tramLineDescMock)).thenThrow(remoteDummyException)
        }

        val actions = listOf(
            GetTramLineAction(tramLineDescMock)
        )
        val expectedResults = listOf(
            GetTramLineResult.InFlight(tramLineDescMock),
            GetTramLineResult.Failure(remoteDummyException)
        )

        test(actions, expectedResults)
    }

    private fun test(actions: List<GetTramLineAction>, expectedResults: List<GetTramLineResult>) {
        StandAloneContext.loadKoinModules(listOf(module {
            factory(override = true) { timeTableRepositoryMock }
            factory(override = true) { tramLineRepositoryMock }
            single<SchedulerProvider>(override = true) { ImmediateSchedulerProvider.instance }
        }))
        val processor = TramLineActionProcessor()
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

        private val localDummyException = Throwable("local dummy exception")
        private val remoteDummyException = Throwable("remote dummy exception")
    }
}