package pl.mobite.tramp.ui.models

import android.os.Parcel
import android.os.Parcelable
import pl.mobite.tramp.data.repositories.models.TimeTable


data class TimeTableRow(
    val hour: Int,
    val minutes: List<Int>
): Parcelable {

    constructor(source: Parcel): this(
        source.readInt(),
        ArrayList<Int>().apply { source.readList(this, Int::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(hour)
        writeList(minutes)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TimeTableRow> = object: Parcelable.Creator<TimeTableRow> {
            override fun createFromParcel(source: Parcel): TimeTableRow = TimeTableRow(source)
            override fun newArray(size: Int): Array<TimeTableRow?> = arrayOfNulls(size)
        }
    }
}

fun TimeTable.toTimeTableRows(): List<TimeTableRow> {
    val rows = mutableListOf<TimeTableRow>()
    this.times.groupBy { it.hour }.forEach { (hour, timeEntries) ->
        rows.add(TimeTableRow(hour, timeEntries.map { it.minute }))
    }
    return rows
}