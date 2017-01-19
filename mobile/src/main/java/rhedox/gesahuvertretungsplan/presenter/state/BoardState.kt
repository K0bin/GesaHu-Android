package rhedox.gesahuvertretungsplan.presenter.state

import android.os.Parcel
import android.os.Parcelable
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.mvp.BoardContract
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 02.01.2017.
 */
data class BoardState(override val boardId: Long) : BoardContract.State, Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(boardId)
    }

    override fun describeContents(): Int {
        return 0;
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<BoardState> = object : Parcelable.Creator<BoardState> {
            override fun createFromParcel(parcel: Parcel): BoardState {
                val id = parcel.readLong()

                return BoardState(id)
            }

            override fun newArray(size: Int): Array<BoardState?> {
                return arrayOfNulls<BoardState?>(size)
            }
        }
    }
}