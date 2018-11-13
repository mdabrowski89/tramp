package pl.mobite.tramp.data.local.json
import com.google.gson.annotations.SerializedName



data class StopsJson(
    @SerializedName("stops") val stops: List<StopJson?>?
)

data class StopJson(
    @SerializedName("direction") val direction: String?,
    @SerializedName("lat") val lat: Double?,
    @SerializedName("line") val line: String?,
    @SerializedName("lon") val lon: Double?,
    @SerializedName("name") val name: String?,
    @SerializedName("stopId") val stopId: String?,
    @SerializedName("order") val order: Int?
)