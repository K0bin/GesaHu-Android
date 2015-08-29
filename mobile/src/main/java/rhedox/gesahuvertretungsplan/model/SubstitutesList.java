package rhedox.gesahuvertretungsplan.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import rhedox.gesahuvertretungsplan.model.Substitute;

/**
 * Created by Robin on 10.07.2015.
 */
public class SubstitutesList {
    private List<Substitute> substitutes;
    private String announcement;

    public SubstitutesList(@Nullable List<Substitute> substitutes, @Nullable String announcement) {
        this.substitutes = substitutes;
        this.announcement = announcement;
    }

    public List<Substitute> getSubstitutes() {
        return new ArrayList<Substitute>(substitutes);
    }

    public String getAnnouncement() {
        return announcement;
    }

    public static List<Substitute> filterImportant(@NonNull Context context, @Nullable List<Substitute> substitutes) {
        if(substitutes == null)
            return null;

        List<Substitute> list = new ArrayList<Substitute>();
        for(Substitute substitute : substitutes) {
            if(substitute.getIsImportant())
                list.add(substitute);
        }

        if(list.size() == 0)
            list.add(Substitute.makeEmptyListSubstitute(context));

        return list;
    }

    public static int countImportant(@Nullable List<Substitute> substitutes) {
        if(substitutes == null)
            return 0;

        int counter = 0;
        for(Substitute substitute : substitutes) {
            if(substitute.getIsImportant())
                counter++;
        }
        return counter;
    }
}
