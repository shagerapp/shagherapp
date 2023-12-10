package com.example.parking.Models;

public class NotifictionModel {

    private  String title;
    private  String body;
    private  String topic;
    private  String tag;
    private int  type=0;

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getTopic() {
        return topic;
    }

    public int getType() {
        return type;
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

    public void setType(Integer type) {
        this.type = (int)type;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }
}
