package pl.mobite.tramp.utils

import io.reactivex.Scheduler


interface SchedulerProvider {

    fun io(): Scheduler

    fun ui(): Scheduler
}