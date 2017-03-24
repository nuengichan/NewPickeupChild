package com.example.database;

import com.example.database.models.Post;
import com.example.database.models.Topic;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kedkamon on 2/23/2017 AD.
 */

public class ExcelObject implements Serializable {

    public String uid;
    public String author;
    public String title;
    private ArrayList<Post> mCheckInUserNews;

    public String getActivityName() {
        return author;
    }

    public void setActivityName(String activityName) {
        this.author = activityName;
    }

    public String getTimeStart() {
        return author;
    }

    public void setTimeStart(String timeStart) {
        this.author = timeStart;
    }

    public String getLocationName() {
        return title;
    }

    public void setLocationName(String locationName) {
        this.title = locationName;
    }

    public ArrayList<Post> getmCheckInUserNews() {
        return mCheckInUserNews;
    }

    public void setmCheckInUserNews(ArrayList<Post> mCheckInUserNews) {
        this.mCheckInUserNews = mCheckInUserNews;
    }
}