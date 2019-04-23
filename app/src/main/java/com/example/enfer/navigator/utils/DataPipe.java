package com.example.enfer.navigator.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.enfer.navigator.beans.MyPlace;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataPipe {

    public static List getListFromJson(Context context) {
        List<MyPlace> places = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences("com.example.enfer.navigator.activities", Context.MODE_PRIVATE);
        String json = prefs.getString("places", null);

        if (json != null){
            Gson gson = new Gson();

            Type type = new TypeToken<ArrayList<MyPlace>>() {
            }.getType();
            places = gson.fromJson(json, type);
        }
        else {
            MyPlace place = new MyPlace();
            place.setCity("No Results");
            place.setName("Try Again");
            place.setAddress("Is This The First Run?");
            places.add(place);
        }

        return places;
    }

    public static void makeJsonFromList(Context context, List list){
        SharedPreferences prefs = context.getSharedPreferences("com.example.enfer.navigator.activities", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("places", json);
        editor.apply();
    }
}
