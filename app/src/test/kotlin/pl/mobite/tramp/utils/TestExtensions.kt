package pl.mobite.tramp.utils

import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito


/**
 * Lazy delegate for creating mocks
 */
inline fun <reified T : Any> lazyMock(): Lazy<T> = lazy { Mockito.mock(T::class.java) }

inline fun <reified T : Any> lazyPowerMock(): Lazy<T> = lazy { PowerMockito.mock(T::class.java) }