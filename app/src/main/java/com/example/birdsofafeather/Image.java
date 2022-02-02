package com.example.birdsofafeather;

import androidx.annotation.NonNull;

public class Image {
    private String url;

    public Image() {
        this.url = "";
    }

    public Image(String url) {
        this.url = url;
    }

    public void setImage(String url) {
        this.url = url;
    }

    public String getImage() {
        return this.url;
    }

    @NonNull
    public String toString() {
        return this.url;
    }
}
