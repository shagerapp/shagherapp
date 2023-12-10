package com.example.parking.Models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResponseNotifiction implements Serializable {


    private  transient String  key;

    @SerializedName("title")
    @Expose
    private   String  title;


    @SerializedName("body")
    @Expose
    private   String  body;

    @SerializedName("Topic")
    @Expose
    private   String  topic;

    @SerializedName("tag")
    @Expose
    private   String  tag="";

    @SerializedName("date")
    @Expose
    private   String  date;

    @SerializedName("type")
    @Expose
    private int type;

    @SerializedName("status")
    @Expose
    private int status;

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getTopic() {
        return topic;
    }

    public String getDate() {
        return date;
    }

    public int getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
