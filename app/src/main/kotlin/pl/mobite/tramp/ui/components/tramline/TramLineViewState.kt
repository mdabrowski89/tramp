package pl.mobite.tramp.ui.components.tramline

import android.os.Parcel
import android.os.Parcelable
import pl.mobite.tramp.ui.models.TramLineDetails
import pl.mobite.tramp.ui.models.ViewStateError


data class TramLineViewState(
    val getTramLineInProgress: Boolean,
    val tramLine: TramLineDetails?,
    val getTramLineError: ViewStateError?
): Parcelable {

    constructor(source: Parcel): this(
        1 == source.readInt(),
        source.readParcelable<TramLineDetails>(TramLineDetails::class.java.classLoader),
        source.readParcelable<ViewStateError>(ViewStateError::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt((if (getTramLineInProgress) 1 else 0))
        writeParcelable(tramLine, 0)
        writeParcelable(getTramLineError, 0)
    }

    companion object {

        val PARCEL_KEY = TramLineViewState.toString()

        fun default() = TramLineViewState(false, null, null)

        @JvmField
        val CREATOR: Parcelable.Creator<TramLineViewState> = object: Parcelable.Creator<TramLineViewState> {
            override fun createFromParcel(source: Parcel): TramLineViewState = TramLineViewState(source)
            override fun newArray(size: Int): Array<TramLineViewState?> = arrayOfNulls(size)
        }
    }
}