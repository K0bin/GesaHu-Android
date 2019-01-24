package rhedox.gesahuvertretungsplan.presenter.state

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by robin on 02.01.2017.
 */
data class MarksState(internal val boardName: String): Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(boardName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MarksState> = object : Parcelable.Creator<MarksState> {
            override fun createFromParcel(parcel: Parcel): MarksState {
                val name = parcel.readString()

                return MarksState(name ?: "")
            }

            override fun newArray(size: Int): Array<MarksState?> {
                return arrayOfNulls<MarksState?>(size)
            }
        }
    }
}