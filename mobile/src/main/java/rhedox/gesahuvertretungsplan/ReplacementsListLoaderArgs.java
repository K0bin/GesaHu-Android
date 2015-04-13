package rhedox.gesahuvertretungsplan;

import android.content.Context;

class ReplacementsListLoaderArgs {
    private String schoolyear, schoolclass;
    private Context context;
    private OnDownloadedListener callback;
    private int day, month, year;
    public ReplacementsListLoaderArgs(int day, int month, int year, String schoolyear, String schoolclass, Context context, OnDownloadedListener callback) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.schoolclass = schoolclass;
        this.schoolyear =schoolyear;
        this.context = context;
        this.callback = callback;
    }

    public String getSchoolyear() {
        return schoolyear;
    }

    public String getSchoolclass() {
        return schoolclass;
    }

    public Context getContext() {
        return context;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public OnDownloadedListener getCallback() {
        return callback;
    }
}
