package pl.mobite.tramp.ui.components.timetable

import android.os.Parcel
import android.os.Parcelable
import pl.mobite.tramp.ui.models.TimeTableDetails
import pl.mobite.tramp.ui.models.TimeTableRow
import pl.mobite.tramp.ui.models.ViewStateError


data class TimeTableViewState(
    val getTimeTableInProgress: Boolean,
    val timeTableDetails: TimeTableDetails?,
    val timeTableRows: List<TimeTableRow>?,
    val getTimeTableError: ViewStateError?
): Parcelable {

    constructor(source: Parcel): this(
        1 == source.readInt(),
        source.readParcelable<TimeTableDetails>(TimeTableDetails::class.java.classLoader),
        source.createTypedArrayList(TimeTableRow.CREATOR),
        source.readParcelable<ViewStateError>(ViewStateError::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt((if (getTimeTableInProgress) 1 else 0))
        writeParcelable(timeTableDetails, 0)
        writeTypedList(timeTableRows)
        writeParcelable(getTimeTableError, 0)
    }

    companion object {
        val PARCEL_KEY = TimeTableViewState.toString()

        fun default() = TimeTableViewState(false, null, null, null)

        @JvmField
        val CREATOR: Parcelable.Creator<TimeTableViewState> = object: Parcelable.Creator<TimeTableViewState> {
            override fun createFromParcel(source: Parcel): TimeTableViewState = TimeTableViewState(source)
            override fun newArray(size: Int): Array<TimeTableViewState?> = arrayOfNulls(size)
        }
    }
}