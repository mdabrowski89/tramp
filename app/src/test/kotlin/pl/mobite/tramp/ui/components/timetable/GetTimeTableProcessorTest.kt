package pl.mobite.tramp.ui.components.timetable

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.test.KoinTest
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.data.repositories.models.TimeTable
import pl.mobite.tramp.data.repositories.models.TimeTableDesc
import pl.mobite.tramp.ui.base.mvi.SchedulerProvider
import pl.mobite.tramp.ui.components.timetable.mvi.TimeTableAction.GetTimeTableAction
import pl.mobite.tramp.ui.components.timetable.mvi.TimeTableActionProcessor
import pl.mobite.tramp.ui.components.timetable.mvi.TimeTableResult
import pl.mobite.tramp.ui.components.timetable.mvi.TimeTableResult.GetTimeTableResult
import pl.mobite.tramp.utils.ImmediateSchedulerProvider
import pl.mobite.tramp.utils.lazyMock
import pl.mobite.tramp.utils.lazyPowerMock


@RunWith(PowerMockRunner::class)
@PrepareForTest(TimeTable::class)
class GetTimeTableProcessorTest: KoinTest {

    private val timeTableRepositoryMock: TimeTableRepository by lazyMock()
    private val localTimeTableMock: TimeTable by lazyPowerMock()
    private val remoteTimeTableMock: TimeTable by lazyPowerMock()

    @Test
    fun testGetTimeTableLocalEmptyRemoteSuccess() {
        Mockito.`when`(localTimeTableMock.canBeOutdated).thenReturn(false)
        Mockito.`when`(remoteTimeTableMock.canBeOutdated).thenReturn(false)
        val (lineName, _, _, stopId) = dummyTimeTableDesc
        timeTableRepositoryMock.apply {
            Mockito.`when`(getTimeTableFromLocal(stopId)).thenReturn(null)
            Mockito.`when`(getTimeTableFromRemote(stopId, lineName)).thenReturn(remoteTimeTableMock)
        }

        val actions = listOf(
            GetTimeTableAction(dummyTimeTableDesc)
        )
        val expectedResults = listOf(
            GetTimeTableResult.InFlight(dummyTimeTableDesc),
            GetTimeTableResult.Success(remoteTimeTableMock)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testGetTimeTableLocalEmptyRemoteFailure() {
        Mockito.`when`(localTimeTableMock.canBeOutdated).thenReturn(false)
        Mockito.`when`(remoteTimeTableMock.canBeOutdated).thenReturn(false)
        val (lineName, _, _, stopId) = dummyTimeTableDesc
        timeTableRepositoryMock.apply {
            Mockito.`when`(getTimeTableFromLocal(stopId)).thenReturn(null)
            Mockito.`when`(getTimeTableFromRemote(stopId, lineName)).thenThrow(remoteDummyException)
        }

        val actions = listOf(
            GetTimeTableAction(dummyTimeTableDesc)
        )
        val expectedResults = listOf(
            GetTimeTableResult.InFlight(dummyTimeTableDesc),
            GetTimeTableResult.Failure(remoteDummyException)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testGetTimeTableLocalSuccessRemoteSuccess() {
        Mockito.`when`(localTimeTableMock.canBeOutdated).thenReturn(false)
        Mockito.`when`(remoteTimeTableMock.canBeOutdated).thenReturn(false)
        val (lineName, _, _, stopId) = dummyTimeTableDesc
        timeTableRepositoryMock.apply {
            Mockito.`when`(getTimeTableFromLocal(stopId)).thenReturn(localTimeTableMock)
            Mockito.`when`(getTimeTableFromRemote(stopId, lineName)).thenReturn(remoteTimeTableMock)
        }

        val actions = listOf(
            GetTimeTableAction(dummyTimeTableDesc)
        )
        val expectedResults = listOf(
            GetTimeTableResult.InFlight(dummyTimeTableDesc),
            GetTimeTableResult.Success(localTimeTableMock)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testGetTimeTableLocalSuccessRemoteFailure() {
        Mockito.`when`(localTimeTableMock.canBeOutdated).thenReturn(false)
        Mockito.`when`(remoteTimeTableMock.canBeOutdated).thenReturn(false)
        val (lineName, _, _, stopId) = dummyTimeTableDesc
        timeTableRepositoryMock.apply {
            Mockito.`when`(getTimeTableFromLocal(stopId)).thenReturn(localTimeTableMock)
            Mockito.`when`(getTimeTableFromRemote(stopId, lineName)).thenThrow(remoteDummyException)
        }

        val actions = listOf(
            GetTimeTableAction(dummyTimeTableDesc)
        )
        val expectedResults = listOf(
            GetTimeTableResult.InFlight(dummyTimeTableDesc),
            GetTimeTableResult.Success(localTimeTableMock)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testGetTimeTableLocalOutdatedRemoteSuccess() {
        Mockito.`when`(localTimeTableMock.canBeOutdated).thenReturn(true)
        Mockito.`when`(remoteTimeTableMock.canBeOutdated).thenReturn(false)
        val (lineName, _, _, stopId) = dummyTimeTableDesc
        timeTableRepositoryMock.apply {
            Mockito.`when`(getTimeTableFromLocal(stopId)).thenReturn(localTimeTableMock)
            Mockito.`when`(getTimeTableFromRemote(stopId, lineName)).thenReturn(remoteTimeTableMock)
        }

        val actions = listOf(
            GetTimeTableAction(dummyTimeTableDesc)
        )
        val expectedResults = listOf(
            GetTimeTableResult.InFlight(dummyTimeTableDesc),
            GetTimeTableResult.Success(localTimeTableMock),
            GetTimeTableResult.Success(remoteTimeTableMock)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testGetTimeTableLocalOutdatedRemoteFailure() {
        Mockito.`when`(localTimeTableMock.canBeOutdated).thenReturn(true)
        Mockito.`when`(remoteTimeTableMock.canBeOutdated).thenReturn(false)
        val (lineName, _, _, stopId) = dummyTimeTableDesc
        timeTableRepositoryMock.apply {
            Mockito.`when`(getTimeTableFromLocal(stopId)).thenReturn(localTimeTableMock)
            Mockito.`when`(getTimeTableFromRemote(stopId, lineName)).thenThrow(remoteDummyException)
        }

        val actions = listOf(
            GetTimeTableAction(dummyTimeTableDesc)
        )
        val expectedResults = listOf(
            GetTimeTableResult.InFlight(dummyTimeTableDesc),
            GetTimeTableResult.Success(localTimeTableMock),
            GetTimeTableResult.Failure(remoteDummyException)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testGetTimeTableLocalFailureRemoteSuccess() {
        Mockito.`when`(localTimeTableMock.canBeOutdated).thenReturn(false)
        Mockito.`when`(remoteTimeTableMock.canBeOutdated).thenReturn(false)
        val (lineName, _, _, stopId) = dummyTimeTableDesc
        timeTableRepositoryMock.apply {
            Mockito.`when`(getTimeTableFromLocal(stopId)).thenThrow(localDummyException)
            Mockito.`when`(getTimeTableFromRemote(stopId, lineName)).thenReturn(remoteTimeTableMock)
        }

        val actions = listOf(
            GetTimeTableAction(dummyTimeTableDesc)
        )
        val expectedResults = listOf(
            GetTimeTableResult.InFlight(dummyTimeTableDesc),
            GetTimeTableResult.Success(remoteTimeTableMock)
        )

        test(actions, expectedResults)
    }

    @Test
    fun testGetTimeTableLocalFailureRemoteFailure() {
        Mockito.`when`(localTimeTableMock.canBeOutdated).thenReturn(false)
        Mockito.`when`(remoteTimeTableMock.canBeOutdated).thenReturn(false)
        val (lineName, _, _, stopId) = dummyTimeTableDesc
        timeTableRepositoryMock.apply {
            Mockito.`when`(getTimeTableFromLocal(stopId)).thenThrow(localDummyException)
            Mockito.`when`(getTimeTableFromRemote(stopId, lineName)).thenThrow(remoteDummyException)
        }

        val actions = listOf(
            GetTimeTableAction(dummyTimeTableDesc)
        )
        val expectedResults = listOf(
            GetTimeTableResult.InFlight(dummyTimeTableDesc),
            GetTimeTableResult.Failure(remoteDummyException)
        )

        test(actions, expectedResults)
    }

    private fun test(actions: List<GetTimeTableAction>, expectedResults: List<GetTimeTableResult>) {
        loadKoinModules(listOf(module {
            factory(override = true) { timeTableRepositoryMock }
            single<SchedulerProvider>(override = true) { ImmediateSchedulerProvider.instance }
        }))
        val processor = TimeTableActionProcessor()
        val testObserver = TestObserver<TimeTableResult>()

        processor.apply(Observable.fromIterable(actions)).subscribe(testObserver)

        testObserver.assertValueCount(expectedResults.size)

        testObserver.values().forEachIndexed {i, tested ->
            Assert.assertEquals(expectedResults[i], tested)
        }

        testObserver.assertComplete()
        testObserver.assertNoErrors()

    }

    companion object {

        private val dummyTimeTableDesc = TimeTableDesc("lineName", "lineDirection",
            "stopName", "stopId")
        private val localDummyException = Throwable("local dummy exception")
        private val remoteDummyException = Throwable("remote dummy exception")
    }
}