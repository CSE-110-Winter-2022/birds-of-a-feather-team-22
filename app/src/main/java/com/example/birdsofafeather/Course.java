package com.example.birdsofafeather;

import androidx.annotation.NonNull;

public class Course {
    private String year;
    private String quarter;
    private String subject;
    private String number;

    public Course() {
        this.year = "";
        this.quarter = "";
        this.subject = "";
        this.number = "";
    }

    public Course(String year, String quarter, String subject, String number) {
        this.year = year;
        this.quarter = quarter;
        this.subject = subject;
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass() ) {
            return false;
        }

        Course course = (Course) o;
        return this.year.equals(course.year) &&
                this.quarter.equals(course.quarter) &&
                this.subject.equals(course.subject) &&
                this.number.equals(course.number);
    }

    @NonNull
    public String toString() {
        return "(" + this.year + "," + this.quarter + "," + this.subject + "," + this.number + ")";
    }
}
