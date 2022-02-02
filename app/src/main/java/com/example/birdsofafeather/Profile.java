package com.example.birdsofafeather;

public class Profile {
    private String name;
    private Image picture;
    private CourseHistory courses;

    public Profile() {
        this.name = "";
        this.picture = new Image();
        this.courses = new CourseHistory();
    }

    public Profile(String name, Image picture, CourseHistory courses) {
        this.name = name;
        this.picture = picture;
        this.courses = courses;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPicture(String url) {
        this.picture.setImage(url);
    }

    public void addCourse(Course course) {
        this.courses.addCourse(course);
    }

    public String getName() {
        return this.name;
    }

    public Image getPicture() {
        return this.picture;
    }

    public CourseHistory getCourseHistory() {
        return this.courses;
    }

}