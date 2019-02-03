package pl.mobite.tramp.ui.components.tramline

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import pl.mobite.tramp.data.repositories.TimeTableRepository
import pl.mobite.tramp.data.repositories.TramLineRepository
import pl.mobite.tramp.data.repositories.models.FilterStopsQuery
import pl.mobite.tramp.data.repositories.models.TimeEntry
import pl.mobite.tramp.data.repositories.models.TimeTable
import pl.mobite.tramp.data.repositories.models.TramStop
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineAction.FilterStopsAction
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineActionProcessor
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineResult
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineResult.FilterStopsResult
import pl.mobite.tramp.utils.ImmediateSchedulerProvider
import pl.mobite.tramp.utils.lazyMock


class FilterStopsProcessorTest {

    private val tramLineRepositoryMock: TramLineRepository by lazyMock()
    private val timeTableRepositoryMock: TimeTableRepository by lazyMock()

    @Test
    fun testFilterStopsLocalEmptyRemoteSuccess() {
        testDataSets.forEach { testDataSet ->
            val lineName = testDataSet.filterQuery.lineName
            testDataSet.timeTables.forEach{ tramStop, timeTable ->
                val id = tramStop.id
                timeTableRepositoryMock.apply {
                    Mockito.`when`(getTimeTableFromLocal(id)).thenReturn(null)
                    Mockito.`when`(getTimeTableFromRemote(id, lineName)).thenReturn(timeTable)
                }
            }

            val actions = listOf(
                FilterStopsAction(testDataSet.filterQuery)
            )
            val expectedResults = listOf(
                FilterStopsResult.InFlight,
                FilterStopsResult.Success(testDataSet.expectedStops)
            )

            test(actions, expectedResults)
        }
    }

    @Test
    fun testFilterStopsLocalEmptyRemoteFailure() {
        testDataSets.forEach { testDataSet ->
            Mockito.reset(timeTableRepositoryMock)
            val lineName = testDataSet.filterQuery.lineName
            testDataSet.timeTables.forEach{ tramStop, _ ->
                val id = tramStop.id
                timeTableRepositoryMock.apply {
                    Mockito.`when`(getTimeTableFromLocal(id)).thenReturn(null)
                    Mockito.`when`(getTimeTableFromRemote(id, lineName)).thenThrow(remoteDummyException)
                }
            }

            val actions = listOf(
                FilterStopsAction(testDataSet.filterQuery)
            )
            val expectedResults = listOf(
                FilterStopsResult.InFlight,
                FilterStopsResult.Failure(remoteDummyException)
            )

            test(actions, expectedResults)
        }
    }

    @Test
    fun testFilterStopsLocalSuccessRemoteSuccess() {
        testDataSets.forEach { testDataSet ->
            val lineName = testDataSet.filterQuery.lineName
            testDataSet.timeTables.forEach{ tramStop, timeTable ->
                val id = tramStop.id
                timeTableRepositoryMock.apply {
                    Mockito.`when`(getTimeTableFromLocal(id)).thenReturn(timeTable)
                    Mockito.`when`(getTimeTableFromRemote(id, lineName)).thenReturn(timeTable)
                }
            }

            val actions = listOf(
                FilterStopsAction(testDataSet.filterQuery)
            )
            val expectedResults = listOf(
                FilterStopsResult.InFlight,
                FilterStopsResult.Success(testDataSet.expectedStops)
            )

            test(actions, expectedResults)
        }
    }

    @Test
    fun testFilterStopsLocalSuccessRemoteError() {
        testDataSets.forEach { testDataSet ->
            Mockito.reset(timeTableRepositoryMock)
            val lineName = testDataSet.filterQuery.lineName
            testDataSet.timeTables.forEach{ tramStop, timeTable ->
                val id = tramStop.id
                timeTableRepositoryMock.apply {
                    Mockito.`when`(getTimeTableFromLocal(id)).thenReturn(timeTable)
                    Mockito.`when`(getTimeTableFromRemote(id, lineName)).thenThrow(remoteDummyException)
                }
            }

            val actions = listOf(
                FilterStopsAction(testDataSet.filterQuery)
            )
            val expectedResults = listOf(
                FilterStopsResult.InFlight,
                FilterStopsResult.Success(testDataSet.expectedStops)
            )

            test(actions, expectedResults)
        }
    }

    @Test
    fun testFilterStopsLocalErrorRemoteSuccess() {
        testDataSets.forEach { testDataSet ->
            Mockito.reset(timeTableRepositoryMock)
            val lineName = testDataSet.filterQuery.lineName
            testDataSet.timeTables.forEach{ tramStop, timeTable ->
                val id = tramStop.id
                timeTableRepositoryMock.apply {
                    Mockito.`when`(getTimeTableFromLocal(id)).thenThrow(localDummyException)
                    Mockito.`when`(getTimeTableFromRemote(id, lineName)).thenReturn(timeTable)
                }
            }

            val actions = listOf(
                FilterStopsAction(testDataSet.filterQuery)
            )
            val expectedResults = listOf(
                FilterStopsResult.InFlight,
                FilterStopsResult.Success(testDataSet.expectedStops)
            )

            test(actions, expectedResults)
        }
    }

    @Test
    fun testFilterStopsLocalErrorRemoteError() {
        testDataSets.forEach { testDataSet ->
            Mockito.reset(timeTableRepositoryMock)
            val lineName = testDataSet.filterQuery.lineName
            testDataSet.timeTables.forEach{ tramStop, _ ->
                val id = tramStop.id
                timeTableRepositoryMock.apply {
                    Mockito.`when`(getTimeTableFromLocal(id)).thenThrow(localDummyException)
                    Mockito.`when`(getTimeTableFromRemote(id, lineName)).thenThrow(remoteDummyException)
                }
            }

            val actions = listOf(
                FilterStopsAction(testDataSet.filterQuery)
            )
            val expectedResults = listOf(
                FilterStopsResult.InFlight,
                FilterStopsResult.Failure(remoteDummyException)
            )

            test(actions, expectedResults)
        }
    }


    private fun test(actions: List<FilterStopsAction>, expectedResults: List<FilterStopsResult>) {
        val processor = TramLineActionProcessor(ImmediateSchedulerProvider.instance, tramLineRepositoryMock, timeTableRepositoryMock)
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

        private val testDataSeed = TestDataSeed(
            listOf(
                TimeEntry(10, 5),
                TimeEntry(10, 25),
                TimeEntry(10, 27),
                TimeEntry(11, 5),
                TimeEntry(11, 17),
                TimeEntry(12, 54),
                TimeEntry(13, 11)
            ),
            listOf(0, 3, 10, 17, 23, 29)
        )
        /* testDataSeed represents the current time table:
        Stops: Times:
            0) 10:05 | 10:25 | 10:27 | 11:05 | 11:17 | 12:54 | 13:11
            1) 10:08 | 10:28 | 10:30 | 11:08 | 11:20 | 12:57 | 13:14
            2) 10:15 | 10:35 | 10:37 | 11:15 | 11:27 | 13:04 | 13:21
            3) 10:22 | 10:42 | 10:44 | 11:22 | 11:34 | 13:11 | 13:28
            4) 10:28 | 10:48 | 10:50 | 11:28 | 11:40 | 13:17 | 13:34
            5) 10:34 | 10:54 | 10:56 | 11:34 | 11:46 | 13:23 | 13:40
         */

        private val testDataSets: List<TestDataSet> = listOf(
            // before first stop
            createTestDataSet(
                testDataSeed,
                TimeEntry(9, 32),
                emptyList()
            ),

            // first tram first stop
            createTestDataSet(
                testDataSeed,
                TimeEntry(10, 5),
                listOf(0)
            ),

            // two trams for one stop
            createTestDataSet(
                testDataSeed,
                TimeEntry(10, 44),
                listOf(3)
            ),

            // new tramp start
            createTestDataSet(
                testDataSeed,
                TimeEntry(11, 16),
                listOf(2)
            ),
            createTestDataSet(
                testDataSeed,
                TimeEntry(11, 17),
                listOf(0, 2)
            ),

            // transition between stops
            createTestDataSet(
                testDataSeed,
                TimeEntry(11, 24),
                listOf(2, 3)
            ),
            createTestDataSet(
                testDataSeed,
                TimeEntry(11, 25),
                listOf(2, 4)
            ),

            // transition between stops
            createTestDataSet(
                testDataSeed,
                TimeEntry(13, 12),
                listOf(0, 3)
            ),
            createTestDataSet(
                testDataSeed,
                TimeEntry(13, 13),
                listOf(1, 3)
            ),

            // last tram last stop
            createTestDataSet(
                testDataSeed,
                TimeEntry(13, 40),
                listOf(5)
            ),
            // after last tram
            createTestDataSet(
                testDataSeed,
                TimeEntry(15, 17),
                emptyList()
            )
        )

        /**
         * @param testDataSeed - seed data which describes tram stops and timetable
         * @param targetTime - time on which filtering is performed
         * @param expectedStopIndexes - indexes of stops which should be filtered
         */
        private fun createTestDataSet(
            testDataSeed: TestDataSeed,
            targetTime: TimeEntry,
            expectedStopIndexes: List<Int>
        ): TestDataSet {
            val stops = testDataSeed.timeDiffs.mapIndexed { i, _ ->
                createStop(
                    i
                )
            }
            val timeTables = stops.mapIndexed { i, tramStop ->
                tramStop to TimeTable(
                    createTimeEntries(
                        testDataSeed.timeDiffs[i],
                        testDataSeed.initialTimeEntries
                    ), false)
            }.toMap()
            val filterQuery = FilterStopsQuery(targetTime, "lineName", stops)
            val expectedStops = expectedStopIndexes.map { stops[it] }
            return TestDataSet(
                timeTables,
                filterQuery,
                expectedStops
            )
        }

        private fun createStop(number: Int) = TramStop("$number", "Stop $number", number.toDouble(), number.toDouble())

        /**
         * @param timeDiff - minutes from first stop to the current one
         * @param initialEntries - entries for trams on the first tram stop
         */
        private fun createTimeEntries(timeDiff: Int, initialEntries: List<TimeEntry>) =
            initialEntries.map { timeEntry ->
                val newMinutes = timeEntry.minute + timeDiff
                val additionalHours = newMinutes / 60
                TimeEntry((timeEntry.hour + additionalHours) % 24, newMinutes % 60)
            }

        private val localDummyException = Throwable("local dummy exception")
        private val remoteDummyException = Throwable("local dummy exception")
    }

    /**
     * @param initialTimeEntries - tram times on the first tram stop
     * @param timeDiffs - time diffs between stops
     */
    private data class TestDataSeed(
        val initialTimeEntries: List<TimeEntry>,
        val timeDiffs: List<Int>
    )

    private data class TestDataSet(
        val timeTables: Map<TramStop, TimeTable>,
        val filterQuery: FilterStopsQuery,
        val expectedStops: List<TramStop>
    )
}