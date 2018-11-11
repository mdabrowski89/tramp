package pl.mobite.tramp.ui.models

import android.os.Parcel
import android.os.Parcelable
import java.util.concurrent.atomic.AtomicBoolean


data class ViewStateError(
    val throwable: Throwable,
    val isConsumed: AtomicBoolean = AtomicBoolean(false)
): Parcelable {

    constructor(source: Parcel): this(
        source.readSerializable() as Throwable,
        source.readSerializable() as AtomicBoolean
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeSerializable(throwable)
        writeSerializable(isConsumed)
    }

    fun shouldBeDisplayed() = !isConsumed.getAndSet(true)

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ViewStateError> = object: Parcelable.Creator<ViewStateError> {
            override fun createFromParcel(source: Parcel): ViewStateError = ViewStateError(source)
            override fun newArray(size: Int): Array<ViewStateError?> = arrayOfNulls(size)
        }
    }
}