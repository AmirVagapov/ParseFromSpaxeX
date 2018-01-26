package com.vagapov.amir.yotatest;

public class RocketModel {

    private String name;

    private String date;

    private String icon;

    private String details;

    private String articleLink;


    RocketModel(String name, String date, String icon, String details, String articleLink) {
        this.name = name;
        this.date = date;
        this.icon = icon;
        this.details = details;
        this.articleLink = articleLink;
    }

    String getArticleLink() {
        return articleLink;
    }


    public String getName() {
        return name;
    }


    String getDate() {
        return date;
    }


    public String getIcon() {
        return icon;
    }


    String getDetails() {
        return details;
    }

}
