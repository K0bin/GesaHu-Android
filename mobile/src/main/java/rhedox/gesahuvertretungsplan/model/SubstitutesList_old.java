package rhedox.gesahuvertretungsplan.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rhedox.gesahuvertretungsplan.util.TextUtils;

/**
 * Created by Robin on 10.07.2015.
 */
public class SubstitutesList_old implements Parcelable {
    private LocalDate date;
    private List<Substitute_old> substitutes;
    private String announcement;

    public SubstitutesList_old(@Nullable List<Substitute_old> substitutes, @Nullable String announcement, @NonNull LocalDate date) {
        this.substitutes = substitutes;
        this.announcement = announcement;
        this.date = date;
    }

	public LocalDate getDate() {
        return date;
    }

    public List<Substitute_old> getSubstitutes() {
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
    public static List<Substitute_old> sort(@Nullable List<Substitute_old> substitutes) {
        if (substitutes == null)
            return null;

        List<Substitute_old> sortedList = new ArrayList<Substitute_old>(substitutes);
        Collections.sort(sortedList);
        return sortedList;
    }

	/**
	 * Removes all non-relevant substitutes
	 * @param substitutes the list of substitutes
	 * @return a new list where each entry is relevant
	 */
	public static List<Substitute_old> filterImportant(@Nullable List<Substitute_old> substitutes) {
		return filterImportant(substitutes, false);
	}


	/**
	 * Removes all non-relevant substitutes
	 * @param substitutes the list of substitutes
	 * @param removeDoubles whether or not it should also remove redundant entries
	 * @return a new list where each entry is relevant
	 */
    public static List<Substitute_old> filterImportant(@Nullable List<Substitute_old> substitutes, boolean removeDoubles) {
        if (substitutes == null)
            return null;

        List<Substitute_old> list = new ArrayList<Substitute_old>();
        for (Substitute_old substitute : substitutes) {
	        if(substitute == null || !substitute.getIsImportant())
		        continue;

	        boolean isAlreadyInList = false;
	        if(removeDoubles) {
		        for (Substitute_old listSub : list) {
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
    public static int countImportant(@Nullable List<Substitute_old> substitutes) {
        if (substitutes == null)
            return 0;

        int counter = 0;
        for (Substitute_old substitute : substitutes) {
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
    public static List<Substitute_old> removeDoubles(@Nullable List<Substitute_old> substitutes) {
        if (substitutes == null)
            return null;

        List<Substitute_old> list = new ArrayList<Substitute_old>(substitutes.size());

        for (Substitute_old substitute : substitutes) {
            boolean isAlreadyInList = false;

            if (substitute != null) {
                for (Substitute_old listSub : list) {
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

	public static final Creator<SubstitutesList_old> CREATOR = new Creator<SubstitutesList_old>() {
		@Override
		public SubstitutesList_old createFromParcel(Parcel in) {
			double d = in.readLong();

			LocalDate date;
			if(d != 0)
				date = new DateTime(in.readLong()).toLocalDate();
			else
				date = null;

			String announcement = in.readString();
			List<Substitute_old> substitutes = in.createTypedArrayList(Substitute_old.CREATOR);

			return new SubstitutesList_old(substitutes, announcement, date);
		}

		@Override
		public SubstitutesList_old[] newArray(int size) {
			return new SubstitutesList_old[size];
		}
	};
}
