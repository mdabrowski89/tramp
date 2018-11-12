package pl.mobite.tramp.data.remote.backend.responses
import com.google.gson.annotations.SerializedName



data class TimeTableBackendResponse(
    @SerializedName("result") val result: List<TimeTableResultBackendResponse?>?
)

data class TimeTableResultBackendResponse(
    @SerializedName("values") val values: List<TimeTableValueBackendResponse?>?
)

data class TimeTableValueBackendResponse(
    @SerializedName("key") val key: String?,
    @SerializedName("value") val value: String?
)

const val TIME_TABLE_VALUE_KEY_TIME = "czas"