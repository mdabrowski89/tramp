package pl.mobite.tramp.ui.base.mvi

import io.reactivex.Scheduler


interface SchedulerProvider {

    fun io(): Scheduler

    fun ui(): Scheduler
}