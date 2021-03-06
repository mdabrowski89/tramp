package pl.mobite.tramp.ui.models

import android.os.Parcel
import android.os.Parcelable
import pl.mobite.tramp.data.repositories.models.TramStop


data class TramStopDetails(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double
): Parcelable {

    constructor(source: Parcel): this(
        source.readString()!!,
        source.readString()!!,
        source.readDouble(),
        source.readDouble()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeDouble(lat)
        writeDouble(lng)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TramStopDetails> = object: Parcelable.Creator<TramStopDetails> {
            override fun createFromParcel(source: Parcel): TramStopDetails = TramStopDetails(source)
            override fun newArray(size: Int): Array<TramStopDetails?> = arrayOfNulls(size)
        }
    }
}

fun TramStop.toTramStopDetails() = TramStopDetails(id, name, lat, lng)

fun TramStopDetails.toTramStop() = TramStop(id, name, lat, lng)