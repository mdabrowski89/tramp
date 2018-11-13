package pl.mobite.tramp.utils

import pl.mobite.tramp.data.repositories.models.TimeEntry
import pl.mobite.tramp.data.repositories.models.TramStop
import pl.mobite.tramp.data.repositories.models.compareTo
import pl.mobite.tramp.data.repositories.models.minus
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue


fun getCurrentTime(): TimeEntry {
    val time = SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)
    val hour = time.subSequence(0, 2).toString().toIntOrNull() ?: 0
    val minute = time.subSequence(3, 5).toString().toIntOrNull() ?: 0
    return TimeEntry(hour, minute)
}

fun getTramStopTimeDiff(targetEntry: TimeEntry, timeEntries: List<TimeEntry>): TramTimeDiff? {
    val sortedEntries = timeEntries.sortedWith(compareBy({ it.hour }, { it.minute }))
    if (sortedEntries.isEmpty()) {
        return null
    }

    // handle case when target entry is before first entry
    if (targetEntry <= sortedEntries.first()) {
        val entryAfter = sortedEntries.first()
        val entryBefore = sortedEntries.last()
        return TramTimeDiff(entryBefore - targetEntry - 24 * 60, entryAfter - targetEntry)
    }

    // handle case when target entry is after last entry
    if (targetEntry >= sortedEntries.last()) {
        val entryAfter = sortedEntries.first()
        val entryBefore = sortedEntries.last()
        return TramTimeDiff(entryBefore - targetEntry, entryAfter - targetEntry + 24 * 60)
    }

    // handle other cases
    sortedEntries.forEachIndexed { i, entryAfter ->
       if (targetEntry < entryAfter && i > 1) {
           val entryBefore = sortedEntries[i - 1]
           return TramTimeDiff(entryBefore - targetEntry, entryAfter - targetEntry)
       }
    }

    // should not happen
    return null
}

fun getTramStopsWithTrams(tramStopWithTimeDiffList: List<TramStopWithTimeDiff>): List<TramStop> {
    val stopsWithTrams = mutableListOf<TramStop>()
    tramStopWithTimeDiffList.forEachIndexed { i, (currentStop, currentStopTimeDiff) ->
        if (i + 1 < tramStopWithTimeDiffList.size) {
            val (nextStop, nextStopTimeDiff) = tramStopWithTimeDiffList[i + 1]
            val currentStopMinutesFromTram = currentStopTimeDiff.minutesFromTram.absoluteValue
            val nextStopMinutesFromTram = nextStopTimeDiff.minutesFromTram.absoluteValue

            if (currentStopMinutesFromTram < nextStopMinutesFromTram) {
                // tram detected between those stops
                if (currentStopMinutesFromTram <=  nextStopTimeDiff.minutesToTram) {
                    stopsWithTrams.add(currentStop)
                } else {
                    stopsWithTrams.add(nextStop)
                }
            }
        }

    }
    return stopsWithTrams
}

/**
 * @param minutesFromTram minutes from the last tram, should be negative
 * @param minutesToTram minutes to next tram should be positive
 */
data class TramTimeDiff(val minutesFromTram: Int, val minutesToTram: Int)

data class TramStopWithTimeDiff(val tramStop: TramStop, val tramTimeDiff: TramTimeDiff)