package pl.mobite.tramp.ui.components.timetable

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import pl.mobite.tramp.data.repositories.models.TimeTableDesc
import pl.mobite.tramp.ui.components.timetable.TimeTableAction.GetTimeTableAction
import pl.mobite.tramp.ui.components.timetable.TimeTableIntent.GetTimeTableIntent
import pl.mobite.tramp.utils.lazyPowerMock


@RunWith(PowerMockRunner::class)
@PrepareForTest(TimeTableDesc::class)
class TimeTableIntentInterpreterTest {

    private lateinit var interpreter: TimeTableIntentInterpreter

    private val timeTableDescMock: TimeTableDesc by lazyPowerMock()

    @Before
    fun setUp() {
        interpreter = TimeTableIntentInterpreter()
    }

    @Test
    fun testIntentInterpreter() {
        Assert.assertEquals(GetTimeTableAction(timeTableDescMock), interpreter.apply(GetTimeTableIntent(timeTableDescMock)))
    }

}