package pl.mobite.tramp.data.remote.repositories

import io.reactivex.Single
import pl.mobite.tramp.R
import pl.mobite.tramp.TrampApp
import pl.mobite.tramp.data.remote.backend.TrampBackend
import pl.mobite.tramp.data.remote.backend.responses.TIME_TABLE_VALUE_KEY_TIME
import pl.mobite.tramp.data.repositories.models.TimeEntry
import pl.mobite.tramp.data.repositories.models.TimeTable
import pl.mobite.tramp.data.repositories.models.TimeTableDesc


class TimeTableRemoteRepositoryImpl(
    private val trampBackend: TrampBackend
): TimeTableRemoteRepository {

    override fun getTimeTable(timeTableDesc: TimeTableDesc): Single<TimeTable> {
        val id = TrampApp.instance.getString(R.string.tramp_backend_timetable_id)
        val apiKey = TrampApp.instance.getString(R.string.tramp_backend_api_key)
        val busStopId = timeTableDesc.stopId.subSequence(0, 4).toString()
        val busStopNumber = timeTableDesc.stopId.subSequence(4, 6).toString()
        val lineNumber = timeTableDesc.lineName

        return trampBackend
            .getTimeTable(id, apiKey, busStopId, busStopNumber, lineNumber)
            .map { timeTableBackendResponse ->

                val results = timeTableBackendResponse.result?.requireNoNulls()
                val timeValues = results?.map { result ->
                    val values = result.values?.requireNoNulls()
                    val timeValue = values?.find { it.key == TIME_TABLE_VALUE_KEY_TIME }
                        timeValue?.value
                }?.requireNoNulls() ?: emptyList()

                val timeEntries = timeValues.mapNotNull { timeValue ->
                    val hour = timeValue.subSequence(0,2).toString().toIntOrNull()
                    val minute = timeValue.subSequence(3,5).toString().toIntOrNull()
                    if (hour != null && minute != null) {
                        TimeEntry(hour, minute)
                    } else {
                        null
                    }
                }
                TimeTable(timeEntries, false)
            }
    }
}