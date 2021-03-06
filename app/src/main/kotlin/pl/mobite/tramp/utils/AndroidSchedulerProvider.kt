package pl.mobite.tramp.utils

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pl.mobite.tramp.ui.base.mvi.SchedulerProvider


class AndroidSchedulerProvider private constructor(): SchedulerProvider {

    override fun io(): Scheduler = Schedulers.io()

    override fun ui(): Scheduler = AndroidSchedulers.mainThread()

    companion object {

        val instance = AndroidSchedulerProvider()
    }
}