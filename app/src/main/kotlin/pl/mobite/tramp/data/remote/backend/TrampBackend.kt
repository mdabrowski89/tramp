package pl.mobite.tramp.data.remote.backend

import io.reactivex.Single
import pl.mobite.tramp.data.remote.backend.responses.TimeTableBackendResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface TrampBackend {

    @GET("api/action/dbtimetable_get/")
    fun getTimeTable(
        @Query("id") id: String,
        @Query("apikey") apiKey: String,
        @Query("busstopId") busStopId: String,
        @Query("busstopNr") busStopNumber: String,
        @Query("line") lineNumber: String
    ): Single<TimeTableBackendResponse>

}