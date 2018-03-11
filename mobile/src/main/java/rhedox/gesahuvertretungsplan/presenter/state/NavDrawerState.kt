package rhedox.gesahuvertretungsplan.presenter.state

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by robin on 02.01.2017.
 */
data class NavDrawerState(val selectedDrawerId: Int? = null): Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(selectedDrawerId ?: -1)
    }

    override fun describeContents(): Int {
        return 0;
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<NavDrawerState> = object : Parcelable.Creator<NavDrawerState> {
            override fun createFromParcel(parcel: Parcel): NavDrawerState {
                val id = parcel.readInt()

                return NavDrawerState(if (id == -1) null else id)
            }

            override fun newArray(size: Int): Array<NavDrawerState?> {
                return arrayOfNulls<NavDrawerState?>(size)
            }
        }
    }
}