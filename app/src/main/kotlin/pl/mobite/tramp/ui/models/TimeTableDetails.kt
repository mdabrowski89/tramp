package pl.mobite.tramp.ui.models

import android.os.Parcel
import android.os.Parcelable
import pl.mobite.tramp.data.repositories.models.TimeTableDesc


data class TimeTableDetails(
    val lineName: String,
    val lineDirection: String,
    val stopName: String,
    val stopId: String
): Parcelable {

    constructor(source: Parcel): this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(lineName)
        writeString(lineDirection)
        writeString(stopName)
        writeString(stopId)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TimeTableDetails> = object: Parcelable.Creator<TimeTableDetails> {
            override fun createFromParcel(source: Parcel): TimeTableDetails = TimeTableDetails(source)
            override fun newArray(size: Int): Array<TimeTableDetails?> = arrayOfNulls(size)
        }
    }
}

fun TimeTableDesc.toTimeTableDetails() = TimeTableDetails(lineName, lineDirection, stopName, stopId)