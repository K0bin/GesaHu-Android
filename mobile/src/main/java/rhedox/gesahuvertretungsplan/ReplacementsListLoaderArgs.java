package rhedox.gesahuvertretungsplan;

import android.content.Context;

import org.joda.time.LocalDate;

class ReplacementsListLoaderArgs {
    private StudentInformation studentInformation;
    private OnDownloadedListener callback;
    private LocalDate date;

    public ReplacementsListLoaderArgs(LocalDate date, StudentInformation studentInformation, OnDownloadedListener callback) {
        this.date = date;
        this.studentInformation = studentInformation;
        this.callback = callback;
    }

    public StudentInformation getStudentInformation() {
        return studentInformation;
    }

    public LocalDate getDate() { return date; }

    public OnDownloadedListener getCallback() {
        return callback;
    }
}
