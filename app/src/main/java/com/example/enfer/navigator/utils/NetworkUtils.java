package com.example.enfer.navigator.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    private static final String BOOK_BASE_URL = "https://api.foursquare.com/v2/venues/search?"; // Base URI for the Books API
    private static final String CLIENT_ֹֹID = "client_id"; // user id
    private static final String CLIENT_SECRET = "client_secret"; // user password
    private static final String QUERY_PARAM = "v"; // version (date based)
    private static final String LAT_LNG = "ll"; // lat-lng
    private static final String RADIUS = "radius";
    private static final String QUERY = "query"; // Parameter for the place search


    public static String getPlaceInfo(String queryString) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String placeJSONString = null;
//        mock location EmpireState building if needed
//        queryString = "40.7484,-73.9857";

        Log.d(TAG, "getPlaceInfo: queryString " + queryString);
        String[] queryParams;
        queryParams = queryString.split("com.enfer.gov");

        Log.d(TAG, "getPlaceInfo: query " + Arrays.toString(queryParams));

        try {
            Uri uri = null;
            if(queryParams.length == 2){
                uri = Uri.parse(BOOK_BASE_URL).buildUpon()
                        .appendQueryParameter(CLIENT_ֹֹID, "BUJDPWBAPA51V0JBXKFGYAZT5JSBNBQONCNZ4N5HLZRU3VX3")
                        .appendQueryParameter(CLIENT_SECRET, "V5XIN2KVSN3FPFZQZON1IISOHZQBAFUHPX2NAK4DTL1ZBZPP")
                        .appendQueryParameter(QUERY_PARAM, "20130815")
                        .appendQueryParameter(LAT_LNG, queryParams[0])
                        .appendQueryParameter(RADIUS, queryParams[1])
                        .appendQueryParameter("OrderByDistance", "1")
                        .build();
            }
            else {
                uri = Uri.parse(BOOK_BASE_URL).buildUpon()
                        .appendQueryParameter(CLIENT_ֹֹID, "BUJDPWBAPA51V0JBXKFGYAZT5JSBNBQONCNZ4N5HLZRU3VX3")
                        .appendQueryParameter(CLIENT_SECRET, "V5XIN2KVSN3FPFZQZON1IISOHZQBAFUHPX2NAK4DTL1ZBZPP")
                        .appendQueryParameter(QUERY_PARAM, "20130815")
                        .appendQueryParameter(LAT_LNG, queryParams[0])
                        .appendQueryParameter(RADIUS, queryParams[1])
                        .appendQueryParameter(QUERY, queryParams[2])
                        .appendQueryParameter("OrderByDistance", "1")
                        .build();
            }

            URL requestUrl = new URL(uri.toString());

            Log.i(TAG, "getPlaceInfo: " + uri.toString());
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            placeJSONString = buffer.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "getPlaceInfo: " + placeJSONString);
            return placeJSONString;
        }
    }
}

