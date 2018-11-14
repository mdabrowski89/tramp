package pl.mobite.tramp.ui.components.tramline

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import pl.mobite.tramp.data.repositories.models.FilterStopsQuery
import pl.mobite.tramp.data.repositories.models.TramLineDesc
import pl.mobite.tramp.ui.components.tramline.TramLineAction.FilterStopsAction
import pl.mobite.tramp.ui.components.tramline.TramLineAction.GetTramLineAction
import pl.mobite.tramp.ui.components.tramline.TramLineIntent.FilterStopsIntent
import pl.mobite.tramp.ui.components.tramline.TramLineIntent.GetTramLineIntent
import pl.mobite.tramp.utils.lazyPowerMock


@RunWith(PowerMockRunner::class)
@PrepareForTest(TramLineDesc::class, FilterStopsQuery::class)
class TramLineIntentInterpreterTest {

    private lateinit var interpreter: TramLineIntentInterpreter

    private val tramLineDescMock: TramLineDesc by lazyPowerMock()
    private val filterStopsQueryMock: FilterStopsQuery by lazyPowerMock()

    @Before
    fun setUp() {
        interpreter = TramLineIntentInterpreter()
    }

    @Test
    fun testIntentInterpreter() {
        Assert.assertEquals(GetTramLineAction(tramLineDescMock), interpreter.apply(GetTramLineIntent(tramLineDescMock)))

        Assert.assertEquals(FilterStopsAction(filterStopsQueryMock), interpreter.apply(FilterStopsIntent(filterStopsQueryMock)))
    }

}