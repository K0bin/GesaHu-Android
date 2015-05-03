package rhedox.gesahuvertretungsplan;

/**
 * Created by Robin on 03.05.2015.
 */
public class StudentInformation {
    private final String schoolYear;
    private final String schoolClass;

    public StudentInformation(String schoolYear, String schoolClass) {
        this.schoolYear = schoolYear;
        this.schoolClass = schoolClass;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public String getSchoolClass() {
        return schoolClass;
    }
}
