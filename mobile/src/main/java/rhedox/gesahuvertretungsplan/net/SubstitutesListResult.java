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
    private int status;

    public SubstitutesListResult(List<Substitute> substitutes, String announcement, int status) {
        this.substitutes = substitutes;
        this.announcement = announcement;
        this.status = status;
    }

    public List<Substitute> getSubstitutes() {
        return new ArrayList<Substitute>(substitutes);
    }

    public String getAnnouncement() {
        return announcement;
    }

    public int getStatus() { return status; }
}
