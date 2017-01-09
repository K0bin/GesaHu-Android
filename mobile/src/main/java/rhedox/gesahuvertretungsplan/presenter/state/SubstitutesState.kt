package rhedox.gesahuvertretungsplan.presenter.state

import android.os.Parcel
import android.os.Parcelable
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 02.01.2017.
 */
data class SubstitutesState(override val date: LocalDate?, override val canGoUp: Boolean? = false, override val selected: Int? = null) : SubstitutesContract.State, Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(date?.unixTimeStamp ?: 0)
        parcel.writeByte(if (canGoUp == true) 1 else 0)
        parcel.writeByte(selected?.toByte() ?: -1)
    }

    override fun describeContents(): Int {
        return 0;
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SubstitutesState> = object : Parcelable.Creator<SubstitutesState> {
            override fun createFromParcel(parcel: Parcel): SubstitutesState {
                val seconds = parcel.readInt()
                val date: LocalDate?;
                if(seconds > 0) {
                    date = localDateFromUnix(seconds)
                } else {
                    date = null
                }

                val canGoUp = parcel.readInt() != 0
                val selectedInt = parcel.readInt()
                val selected = if (selectedInt >= 0) selectedInt else null

                return SubstitutesState(date, canGoUp, selected)
            }

            override fun newArray(size: Int): Array<SubstitutesState?> {
                return arrayOfNulls<SubstitutesState?>(size)
            }
        }
    }
}