package com.example.enfer.navigator.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.enfer.navigator.beans.MyPlace;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;


public class Dao {
    private DBHelper dbHelper;
    private Context context;

    public Dao(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    // code to add the new place
    public void addPlace(MyPlace place) throws Exception {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBConstants.CITY, place.getCity());
            values.put(DBConstants.NAME, place.getName());
            values.put(DBConstants.ADDRESS, place.getAddress());
            values.put(DBConstants.DISTANCE, place.getDistance());
            values.put(DBConstants.LATITUDE, place.getLat());
            values.put(DBConstants.LONGITUDE, place.getLng());

            // Inserting Row
            db.insert(DBConstants.TABLE_PLACES, null, values);
            Log.i("database", db.toString());
            //2nd argument is String containing nullColumnHack
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            throw new Exception(e);
        } finally {
            db.close(); // Closing database connection

        }
    }

    public MyPlace getPlace(int id) throws Exception {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(DBConstants.TABLE_PLACES, new String[]{DBConstants._ID,
                            DBConstants.CITY, DBConstants.NAME, DBConstants.ADDRESS, DBConstants.DISTANCE,
                            DBConstants.LATITUDE, DBConstants.LONGITUDE}, DBConstants._ID + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);
            if (cursor != null){
                cursor.moveToFirst();
            }

            MyPlace place = new MyPlace(Long.parseLong(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    Integer.parseInt(cursor.getString(4)),
                    Double.parseDouble(cursor.getString(5)), Double.parseDouble(cursor.getString(6)));
            return place;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            throw new Exception(e);
        } finally {
            db.close();
        }

    }

    public boolean isSamePlace(String name, String overView) throws Exception {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getReadableDatabase();
            String[] addrWhereParams = new String[]{name, overView};
            Cursor cursor = db.query(DBConstants.TABLE_PLACES, null, DBConstants.NAME +
                            "=?" + " AND " + DBConstants.ADDRESS + "=?",
                    addrWhereParams, null, null, null, null);

            if (cursor.moveToFirst()) {
                cursor.close();
                return true;
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            throw new Exception(e);
        }
        finally {
            db.close();
        }
        return false;
    }

    public List<MyPlace> getAllPlaces() throws Exception {
        List<MyPlace> placesList = new ArrayList<MyPlace>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DBConstants.TABLE_PLACES;

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    MyPlace place = new MyPlace();
                    place.setId(Integer.parseInt(cursor.getString(0)));
                    place.setCity(cursor.getString(1));
                    place.setName(cursor.getString(2));
                    place.setAddress(cursor.getString(3));
                    place.setLat(Double.parseDouble(cursor.getString(5)));
                    place.setLng(Double.parseDouble(cursor.getString(6)));

                    //distance saved to database is based on location at that time - useless
                    SharedPreferences prefs = context.getSharedPreferences("com.example.enfer.navigator.activities", Context.MODE_PRIVATE);

                    String prefsLocation = prefs.getString("location", null);
                    String[] latlng = null;
                    if(prefsLocation != null){
                        latlng = prefsLocation.split(",");
                    }
                    Double lat = place.getLat();
                    Double lng = place.getLng();
                    Double currenLat = Double.parseDouble(latlng[0]);
                    Double currenLng = Double.parseDouble(latlng[1]);
                    place.setDistance((int) MyPlace.distance(lat, currenLat, lng, currenLng));


                    // Adding place to list
                    placesList.add(place);
                } while (cursor.moveToNext());
            }

            // return place list
            return placesList;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            throw new Exception(e);
        }

        finally {

            db.close();
        }
    }


    // code to update the single place
    public int updatePlace(MyPlace place) throws Exception {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBConstants.CITY, place.getCity());
            values.put(DBConstants.NAME, place.getName());
            values.put(DBConstants.ADDRESS, place.getAddress());
            values.put(DBConstants.DISTANCE, place.getDistance());
            values.put(DBConstants.LATITUDE, String.valueOf(place.getLat()));
            values.put(DBConstants.LONGITUDE, String.valueOf(place.getLng()));


            return db.update(DBConstants.TABLE_PLACES, values, DBConstants._ID + " = ?",
                    new String[]{String.valueOf(place.getId())});
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            throw new Exception(e);
        }
        finally {
            db.close();
        }
    }

    // Deleting single place
    public void deletePlace(MyPlace place) throws Exception {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.delete(DBConstants.TABLE_PLACES, DBConstants._ID + " = ?",
                    new String[]{String.valueOf(place.getId())});
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            throw new Exception(e);
        }

        finally {
            db.close();
        }
    }

    public void deleteAll() throws Exception {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.delete(DBConstants.TABLE_PLACES, null,
                    new String[]{});
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            throw new Exception(e);
        }
        finally {
            db.close();
        }
    }

    // Getting places Count
    public int getPlacesCount() throws Exception {
        String countQuery = "SELECT  * FROM " + DBConstants.TABLE_PLACES;
        SQLiteDatabase db = null;

        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(countQuery, null);
            // return count
            return cursor.getCount();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            throw new Exception(e);
        } finally {
            cursor.close();
            db.close();
        }

    }
}
