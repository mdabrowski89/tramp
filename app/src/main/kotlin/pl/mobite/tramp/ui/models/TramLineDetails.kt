package pl.mobite.tramp.ui.models

import android.os.Parcel
import android.os.Parcelable
import pl.mobite.tramp.data.repositories.models.TramLine


data class TramLineDetails(
    val name: String,
    val direction: String,
    val stops: List<TramStopDetails>
): Parcelable {

    constructor(source: Parcel): this(
        source.readString()!!,
        source.readString()!!,
        source.createTypedArrayList(TramStopDetails.CREATOR)!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(direction)
        writeTypedList(stops)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TramLineDetails> = object: Parcelable.Creator<TramLineDetails> {
            override fun createFromParcel(source: Parcel): TramLineDetails = TramLineDetails(source)
            override fun newArray(size: Int): Array<TramLineDetails?> = arrayOfNulls(size)
        }
    }
}

fun TramLine.toTramLineDetails() = TramLineDetails(
    this.desc.name,
    this.desc.direction,
    this.stops.map { it.toTramStopDetails() }
)