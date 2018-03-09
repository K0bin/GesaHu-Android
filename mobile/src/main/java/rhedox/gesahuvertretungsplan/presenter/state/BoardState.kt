package rhedox.gesahuvertretungsplan.presenter.state

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by robin on 02.01.2017.
 */
data class BoardState(val boardName: String): Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(boardName)
    }

    override fun describeContents(): Int {
        return 0;
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<BoardState> = object : Parcelable.Creator<BoardState> {
            override fun createFromParcel(parcel: Parcel): BoardState {
                val name = parcel.readString()

                return BoardState(name)
            }

            override fun newArray(size: Int): Array<BoardState?> {
                return arrayOfNulls<BoardState?>(size)
            }
        }
    }
}