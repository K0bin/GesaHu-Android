package rhedox.gesahuvertretungsplan.presenter.state

import android.os.Parcel
import android.os.Parcelable
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 02.01.2017.
 */
data class SupervisionsState(val date: LocalDate?, val selected: Int? = null) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(date?.unixTimeStamp ?: 0)
        parcel.writeInt(selected ?: -1)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SupervisionsState> = object : Parcelable.Creator<SupervisionsState> {
            override fun createFromParcel(parcel: Parcel): SupervisionsState {
                val seconds = parcel.readInt()
                val date: LocalDate?
                if(seconds > 0) {
                    date = localDateFromUnix(seconds)
                } else {
                    date = null
                }

                val selectedInt = parcel.readInt()
                val selected = if (selectedInt >= 0) selectedInt else null

                return SupervisionsState(date, selected)
            }

            override fun newArray(size: Int): Array<SupervisionsState?> {
                return arrayOfNulls<SupervisionsState?>(size)
            }
        }
    }
}
