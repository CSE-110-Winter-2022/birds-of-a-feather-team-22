package com.example.birdsofafeather.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.birdsofafeather.Image;

import java.util.HashMap;
import java.util.*;

@Entity(tableName = "PROFILE")
public class Profile {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo (name = "id")
    private int id;

    @ColumnInfo (name = "name")
    private String name;

    @ColumnInfo (name = "photo")
    private Image photo;

    @ColumnInfo (name = "courses")
    private List<Course> courses;



    public Profile() {
        this.name = "";
        this.photo = new Image();
        this.courses = new ArrayList<>();
    }

//    public Profile(String name, Image photo, List<Course> courses) {
//        this.name = name;
//        this.photo = photo;
//        this.courses = courses;
//    }


    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String url) {
        this.photo.setImage(url);
    }

    public void addCourse(Course course) {
        this.courses.add(course);
        course.addUserId(this.id);
    }

    public String getName() {
        return this.name;
    }

    public Image getPhoto() {
        return this.photo;
    }

    public List<Course> getCourses() {
        return this.courses;
    }

    public int getId() {
        return this.id;
    }

}