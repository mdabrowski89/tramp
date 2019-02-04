package pl.mobite.tramp

import android.app.Application
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.android.startKoin
import pl.mobite.tramp.di.appModule
import pl.mobite.tramp.di.repositoriesModule
import pl.mobite.tramp.di.retrofitModule
import pl.mobite.tramp.di.roomModule


class TrampApp: Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin()
        initRxJavaErrorHandler()
    }

    private fun initKoin() {
        startKoin(this, listOf(appModule, retrofitModule, roomModule, repositoriesModule))
    }

    private fun initRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler { t: Throwable? ->
            if (t is InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
            }
        }
    }
}