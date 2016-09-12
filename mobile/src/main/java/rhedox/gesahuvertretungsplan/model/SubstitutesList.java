package rhedox.gesahuvertretungsplan.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.SortedList;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.util.TextUtils;

/**
 * Created by Robin on 10.07.2015.
 */
public class SubstitutesList implements Parcelable {
    private LocalDate date;
    private List<Substitute> substitutes;
    private String announcement;

    public SubstitutesList(@Nullable List<Substitute> substitutes, @Nullable String announcement, @NonNull LocalDate date) {
        this.substitutes = substitutes;
        this.announcement = announcement;
        this.date = date;
    }

	public LocalDate getDate() {
        return date;
    }

    public List<Substitute> getSubstitutes() {
        return Collections.unmodifiableList(substitutes);
    }

    public String getAnnouncement() {
        return announcement;
    }

    public boolean hasSubstitutes() {
        return substitutes != null && substitutes.size() > 0;
    }

    public boolean hasAnnouncement() {
        return announcement != null && !TextUtils.isEmpty(announcement) && !"keine".equals(announcement);
    }


	/**
	 * Sorts the list so that relevant entries are on top
	 * @param substitutes the list of substitutes
	 * @return a new sorted list
	 */
    public static List<Substitute> sort(@Nullable List<Substitute> substitutes) {
        if (substitutes == null)
            return null;

        List<Substitute> sortedList = new ArrayList<Substitute>(substitutes);
        Collections.sort(sortedList);
        return sortedList;
    }

	/**
	 * Removes all non-relevant substitutes
	 * @param substitutes the list of substitutes
	 * @return a new list where each entry is relevant
	 */
	public static List<Substitute> filterImportant(@Nullable List<Substitute> substitutes) {
		return filterImportant(substitutes, false);
	}


	/**
	 * Removes all non-relevant substitutes
	 * @param substitutes the list of substitutes
	 * @param removeDoubles whether or not it should also remove redundant entries
	 * @return a new list where each entry is relevant
	 */
    public static List<Substitute> filterImportant(@Nullable List<Substitute> substitutes, boolean removeDoubles) {
        if (substitutes == null)
            return null;

        List<Substitute> list = new ArrayList<Substitute>();
        for (Substitute substitute : substitutes) {
	        if(substitute == null || !substitute.getIsImportant())
		        continue;

	        boolean isAlreadyInList = false;
	        if(removeDoubles) {
		        for (Substitute listSub : list) {
			        if (substitute.equals(listSub))
				        isAlreadyInList = true;
		        }
	        }

	        if (!isAlreadyInList)
		        list.add(substitute);
        }

        return list;
    }

	/**
	 * Counts the amount of relevant substitutes on the given list
	 * @param substitutes the list of substitutes
	 * @return the amount of relevant substitutes
	 */
    public static int countImportant(@Nullable List<Substitute> substitutes) {
        if (substitutes == null)
            return 0;

        int counter = 0;
        for (Substitute substitute : substitutes) {
            if (substitute.getIsImportant())
                counter++;
        }
        return counter;
    }

	/**
	 * Removes redundant entries
	 * @param substitutes the list of substitutes
	 * @return a new list of substitutes where each entry is unique
	 */
    public static List<Substitute> removeDoubles(@Nullable List<Substitute> substitutes) {
        if (substitutes == null)
            return null;

        List<Substitute> list = new ArrayList<Substitute>(substitutes.size());

        for (Substitute substitute : substitutes) {
            boolean isAlreadyInList = false;

            if (substitute != null) {
                for (Substitute listSub : list) {
                    if (substitute.equals(listSub))
                        isAlreadyInList = true;
                }
            }

            if (!isAlreadyInList)
                list.add(substitute);
        }
        return list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
	    if(parcel == null)
		    return;

	    if(date != null)
	        parcel.writeDouble(date.toDateTimeAtCurrentTime().getMillis());
	    else
	        parcel.writeDouble(0);

	    parcel.writeString(announcement);
        parcel.writeTypedList(this.substitutes);
    }

	public static final Creator<SubstitutesList> CREATOR = new Creator<SubstitutesList>() {
		@Override
		public SubstitutesList createFromParcel(Parcel in) {
			double d = in.readLong();

			LocalDate date;
			if(d != 0)
				date = new DateTime(in.readLong()).toLocalDate();
			else
				date = null;

			String announcement = in.readString();
			List<Substitute> substitutes = in.createTypedArrayList(Substitute.CREATOR);

			return new SubstitutesList(substitutes, announcement, date);
		}

		@Override
		public SubstitutesList[] newArray(int size) {
			return new SubstitutesList[size];
		}
	};
}
