package com.example.database.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post {
    public String downloadeUrl;
    public String uid;
    public String author;
    public String title;
    public String body;
    public Map<String, String> Timeadd;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

//    public Post(String image) {
//        this.Image = image;
//    }
//

    public Post(String uid, String author, String title, String body, String downloadUrl) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.downloadeUrl = downloadUrl;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("downloadeUrl", downloadeUrl);
        result.put("TimeAdd", getTime());


        return result;

    }
    public Map<String, String> getTime() {

        Timeadd = ServerValue.TIMESTAMP ;
        return Timeadd;
    }



    public String getImage() {

        return downloadeUrl;

    }

    public void setImage(String downloadeUrl) {

        this.downloadeUrl = downloadeUrl;
    }

    public String author() {

        return author;
    }



    public void gettitle (String title) {

        this.title = title;
    }

    public String body() {

        return body;
    }



}
