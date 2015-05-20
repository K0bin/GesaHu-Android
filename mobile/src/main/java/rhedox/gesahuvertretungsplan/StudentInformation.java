package rhedox.gesahuvertretungsplan;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Robin on 03.05.2015.
 */
public class StudentInformation implements Parcelable {
    private final String schoolYear;
    private final String schoolClass;

    public StudentInformation(String schoolYear, String schoolClass) {
        this.schoolYear = schoolYear;
        this.schoolClass = schoolClass;
    }

    private StudentInformation(Parcel parcel) {
        if(parcel!= null) {
            this.schoolYear = parcel.readString();
            this.schoolClass = parcel.readString();
        } else {
            this.schoolYear = "";
            this.schoolClass = "";
        }
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public String getSchoolClass() {
        return schoolClass;
    }

    public boolean isEmpty() {
        return (schoolYear == null || schoolYear.equals("") || schoolYear.equals(" ")) && (schoolClass == null || schoolClass.equals("") || schoolClass.equals(" "));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(schoolYear);
        dest.writeString(schoolClass);
    }

    public static final Parcelable.Creator<StudentInformation> CREATOR = new Parcelable.Creator<StudentInformation>() {

        @Override
        public StudentInformation createFromParcel(Parcel source) {
            return new StudentInformation(source);
        }

        @Override
        public StudentInformation[] newArray(int size) {
            return new StudentInformation[size];
        }
    };
}
