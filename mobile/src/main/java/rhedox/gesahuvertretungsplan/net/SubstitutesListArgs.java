package rhedox.gesahuvertretungsplan.net;

import org.joda.time.LocalDate;

import rhedox.gesahuvertretungsplan.model.StudentInformation;

class SubstitutesListArgs {
    private StudentInformation studentInformation;
    private OnDownloadedListener callback;
    private LocalDate date;

    public SubstitutesListArgs(LocalDate date, StudentInformation studentInformation, OnDownloadedListener callback) {
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
