package rhedox.gesahuvertretungsplan.presenter.state

import android.os.Parcel
import android.os.Parcelable
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.mvp.BoardContract
import rhedox.gesahuvertretungsplan.mvp.MarksContract
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 02.01.2017.
 */
data class MarksState(val boardId: Long): Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(boardId)
    }

    override fun describeContents(): Int {
        return 0;
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MarksState> = object : Parcelable.Creator<MarksState> {
            override fun createFromParcel(parcel: Parcel): MarksState {
                val id = parcel.readLong()

                return MarksState(id)
            }

            override fun newArray(size: Int): Array<MarksState?> {
                return arrayOfNulls<MarksState?>(size)
            }
        }
    }
}