package com.example.birdsofafeather;

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

}
