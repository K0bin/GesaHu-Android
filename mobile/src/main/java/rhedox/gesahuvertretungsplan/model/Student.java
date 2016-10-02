package rhedox.gesahuvertretungsplan.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import rhedox.gesahuvertretungsplan.util.TextUtils;

/**
 * Created by Robin on 03.05.2015.
 */
public class Student implements Parcelable {
    @NonNull private final String schoolYear;
    @NonNull private final String schoolClass;

    public Student(@NonNull String schoolYear, @NonNull String schoolClass) {
        this.schoolYear = schoolYear;
        this.schoolClass = schoolClass;
    }

    private Student(Parcel parcel) {
        if(parcel!= null) {
            this.schoolYear = parcel.readString();
            this.schoolClass = parcel.readString();
        } else {
            this.schoolYear = "";
            this.schoolClass = "";
        }
    }

    @NonNull
    public String getSchoolYear() {
        return schoolYear;
    }

    @NonNull
    public String getSchoolClass() {
        return schoolClass;
    }

    public boolean getIsEmpty() {
        return TextUtils.isEmpty(schoolYear) && TextUtils.isEmpty(schoolClass);
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

    public static final Parcelable.Creator<Student> CREATOR = new Parcelable.Creator<Student>() {

        @Override
        public Student createFromParcel(Parcel source) {
            return new Student(source);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };
}
