package rhedox.gesahuvertretungsplan.net;

import java.util.ArrayList;
import java.util.List;

import rhedox.gesahuvertretungsplan.model.Substitute;

/**
 * Created by Robin on 10.07.2015.
 */
public class SubstitutesListResult {
    private List<Substitute> substitutes;
    private String announcement;

    public SubstitutesListResult(List<Substitute> substitutes, String announcement) {
        this.substitutes = substitutes;
        this.announcement = announcement;
    }

    public List<Substitute> getSubstitutes() {
        return new ArrayList<Substitute>(substitutes);
    }

    public String getAnnouncement() {
        return announcement;
    }
}
