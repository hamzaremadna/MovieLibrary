package com.example.movielib;


public class Data {
    private String title;
    private String author;
    private String date;
    private String link;
    private String img;
    private double lat;
    private double lon;



    private boolean checked;

    public Data(){
    }
   public Data(String title ,String author ,String date ,String Link,String img,double lat,double lon,boolean checked)
   {
    this.title = title;
    this.author = author;
    this.date = date;
    this.link = Link;
    this.img = img;
    this.lat = lat;
    this.lon = lon;
    this.checked = checked;
   }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) { this.img = img; }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) { this.lon = lon; }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) { this.lat = lat; }

    public boolean isChecked() {
        return checked; }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
