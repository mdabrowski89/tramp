package pl.mobite.tramp

import android.app.Application


class TrampApp: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

    }

    companion object {

        @JvmStatic
        lateinit var instance: TrampApp
            private set
    }
}