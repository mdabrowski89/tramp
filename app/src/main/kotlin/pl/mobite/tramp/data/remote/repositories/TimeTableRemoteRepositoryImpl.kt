package pl.mobite.tramp.data.remote.repositories

import android.content.Context
import pl.mobite.tramp.R
import pl.mobite.tramp.data.remote.backend.TrampBackend
import pl.mobite.tramp.data.remote.backend.responses.TIME_TABLE_VALUE_KEY_TIME
import pl.mobite.tramp.data.repositories.models.TimeEntry
import pl.mobite.tramp.data.repositories.models.TimeTable


class TimeTableRemoteRepositoryImpl(
    private val context: Context,
    private val trampBackend: TrampBackend
): TimeTableRemoteRepository {

    override fun getTimeTable(tramStopId: String, lineName: String): TimeTable {
        val id = context.getString(R.string.tramp_backend_timetable_id)
        val apiKey = context.getString(R.string.tramp_backend_api_key)
        val busStopId = tramStopId.subSequence(0, 4).toString()
        val busStopNumber = tramStopId.subSequence(4, 6).toString()

        val timeTableBackendResponse = trampBackend.getTimeTable(id, apiKey, busStopId, busStopNumber, lineName)
        val results = timeTableBackendResponse.result?.requireNoNulls()
        val timeValues = results?.map { result ->
            result.values?.requireNoNulls()?.find { it.key == TIME_TABLE_VALUE_KEY_TIME }?.value
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
        return TimeTable(timeEntries, false)
    }
}