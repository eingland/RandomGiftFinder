package com.ericingland.randomgiftfinder;


public class Item {
    private int id;
    private String title;
    private String url;
    private String imageUrl;

    public Item() {}

//    public Item(int id, String title, String imageUrl, String url) {
//        this.id = id;
//        this.title = title;
//        this.imageUrl = imageUrl;
//        this.url = url;
//    }

    @SuppressWarnings("unused")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }
}
