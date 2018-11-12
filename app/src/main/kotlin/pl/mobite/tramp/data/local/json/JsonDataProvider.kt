package pl.mobite.tramp.data.local.json

import com.google.gson.Gson
import pl.mobite.tramp.TrampApp
import pl.mobite.tramp.data.repositories.models.TramLineDesc
import java.io.InputStreamReader


class JsonDataProvider {

    fun getStopsJson(tramLineDesc: TramLineDesc): StopsJson? {
        return if (tramLineDesc.name == "35" && tramLineDesc.direction == "Banacha") {
            TrampApp.instance.assets
                .open("stopsForLine35Banacha.json")
                .use { inputStream ->
                    Gson().fromJson(InputStreamReader(inputStream), StopsJson::class.java
                )
            }
        } else {
            null
        }
    }
}