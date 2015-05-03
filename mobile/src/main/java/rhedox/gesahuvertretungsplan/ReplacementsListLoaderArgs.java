package rhedox.gesahuvertretungsplan;

import android.content.Context;

class ReplacementsListLoaderArgs {
    private StudentInformation studentInformation;
    private Context context;
    private OnDownloadedListener callback;
    private Date date;

    public ReplacementsListLoaderArgs(Date date, StudentInformation studentInformation, Context context, OnDownloadedListener callback) {
        this.date = date;
        this.studentInformation = studentInformation;
        this.context = context;
        this.callback = callback;
    }

    public StudentInformation getStudentInformation() {
        return studentInformation;
    }

    public Context getContext() {
        return context;
    }

    public Date getDate() { return date; }

    public OnDownloadedListener getCallback() {
        return callback;
    }
}
