package rhedox.gesahuvertretungsplan.presenter.state

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by robin on 02.01.2017.
 */
data class LessonsState(val boardName: String): Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(boardName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<LessonsState> = object : Parcelable.Creator<LessonsState> {
            override fun createFromParcel(parcel: Parcel): LessonsState {
                val name = parcel.readString()

                return LessonsState(name ?: "")
            }

            override fun newArray(size: Int): Array<LessonsState?> {
                return arrayOfNulls<LessonsState?>(size)
            }
        }
    }
}