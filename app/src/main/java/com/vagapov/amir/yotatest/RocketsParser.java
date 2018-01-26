package com.vagapov.amir.yotatest;


import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class RocketsParser {


    private static final String NAME = "rocket_name";
    private static final String DATE = "launch_date_unix";
    private static final String ICON = "mission_patch";
    private static final String DETAILS = "details";
    private static final String ARTICLE_LINK = "article_link";
    private static final String LINKS = "links";
    private static final String ROCKET = "rocket";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMM, YYYY, HH:mm:ss");


    @NonNull
    ArrayList<RocketModel> parseData(String response){
        ArrayList<RocketModel> models = new ArrayList<>();

        try{
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject data = jsonArray.getJSONObject(i);
                JSONObject object = data.getJSONObject(ROCKET);
                String name = object.getString(NAME);
                String date = data.getString(DATE);
                object = data.getJSONObject(LINKS);
                String icon = object.getString(ICON);
                String articleLink = object.getString(ARTICLE_LINK);
                String details = data.getString(DETAILS);

                Date correctDate = new Date(Long.valueOf(date) * 1000L);
                date = String.valueOf(dateFormat.format(correctDate));

                RocketModel model = new RocketModel(name, date, icon, details, articleLink);
                models.add(model);
            }

        }catch (JSONException exc){
            exc.printStackTrace();
        }
        return models;
    }
}
