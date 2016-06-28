package rhedox.gesahuvertretungsplan.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.SortedList;

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
public class SubstitutesList {
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

    public static List<Substitute> sort(@Nullable List<Substitute> substitutes) {
        if (substitutes == null)
            return null;

        List<Substitute> sortedList = new ArrayList<Substitute>(substitutes);
        Collections.sort(sortedList);
        return sortedList;
    }

	public static List<Substitute> filterImportant(@Nullable List<Substitute> substitutes) {
		return filterImportant(substitutes, false);
	}


		public static List<Substitute> filterImportant(@Nullable List<Substitute> substitutes, boolean removeDoubles) {
        if (substitutes == null)
            return null;

        List<Substitute> list = new ArrayList<Substitute>();
        for (Substitute substitute : substitutes) {
	        if(substitute == null || !substitute.getIsImportant())
		        continue;

	        boolean isAlreadyInList = false;
	        for (Substitute listSub : list) {
		        if (substitute.equals(listSub))
			        isAlreadyInList = true;
	        }

	        if (!isAlreadyInList)
		        list.add(substitute);
        }

        return list;
    }

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
}
