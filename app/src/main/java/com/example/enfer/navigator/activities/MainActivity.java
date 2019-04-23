package com.example.enfer.navigator.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.enfer.navigator.R;
import com.example.enfer.navigator.beans.MyPlace;
import com.example.enfer.navigator.dao.Dao;
import com.example.enfer.navigator.fragments.FragmentMap;
import com.example.enfer.navigator.fragments.FragmentPlaceDetails;
import com.example.enfer.navigator.fragments.FragmentPlaces;
import com.example.enfer.navigator.recievers.CustomReceiver;
import com.example.enfer.navigator.utils.DataPipe;
import com.example.enfer.navigator.utils.PlacesLoader;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String>, FragmentPlaces.OnFragmentInteractionListener {
    //const
    public static final String QUERY_STRING = "queryString";
    private static final String TAG = "MainActivity";
    private static final String ACTION_CUSTOM_BROADCAST =
            "com.example.enfer.navigator.activities.ACTION_CUSTOM_BROADCAST";
    //vars
    private PackageManager manager;
    private ComponentName name;
    private CustomReceiver receiver = new CustomReceiver();
    private FragmentPlaces fragmentPlaces;
    private FragmentPlaceDetails fragmentPlaceDetails;
    private FragmentMap fragmentMap;
    private FusedLocationProviderClient providerClient;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private Button btnResults;
    private Button btnMap;
    private Button btnToSearchResults;
    private boolean isLarge;
    private List<MyPlace> places = new ArrayList<>();
    private Location mLocation;
    private String radiusMeter = "9999";
    private String unit = "ms";
    private String searchTerm = "";
    private Dao dao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = this.getSharedPreferences("com.example.enfer.navigator.activities", Context.MODE_PRIVATE);

        if (prefs.getString("unit", null) != null) {
            unit = prefs.getString("unit", null);
        }

        if (prefs.getString("Radius", null) != null) {
                radiusMeter = prefs.getString("Radius", null);
            }
            Toast.makeText(this, "Radius = " + radiusMeter + unit, Toast.LENGTH_LONG).show();


        dao = new Dao(this);
        btnResults = findViewById(R.id.btn_fragment_left);
        btnMap = (Button) findViewById(R.id.btn_show_map);
        btnToSearchResults = (Button) findViewById(R.id.btn_retun_to_results);

        checkConnectivity();
        requestPermission();
        getLocation();

        //registering to power connection alerts
        name = new ComponentName(this, CustomReceiver.class);
        manager = getPackageManager();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(ACTION_CUSTOM_BROADCAST));

        if (findViewById(R.id.fragment_left) != null) {
            isLarge = true;
        }


        if (getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }
    }

    private boolean checkConnectivity() {
        //check for internet connection
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(getApplicationContext(),"OnRequestPermissios Failed!",Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void getLocation() {
        providerClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        providerClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d(TAG, "onSuccess: " + location.toString());
                    mLocation = location;
                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("com.example.enfer.navigator.activities", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("location", location.getLatitude() + "," + location.getLongitude());
                    editor.apply();
                    //connecting to foursquare API
                    getPlaces();
                }
            }
        });

    }

    public void getPlaces() {
        String queryString = Double.toString(mLocation.getLatitude()) + "," + Double.toString(mLocation.getLongitude());
        double conversion = 3.28; //feet to meter
        if (unit.equals("ms")){
            conversion = 1;
        }
        double distance = Double.parseDouble(radiusMeter) / conversion; //API search is by meters
        radiusMeter = distance + "";
        queryString += "com.enfer.gov" + radiusMeter;
        if (searchTerm != null && searchTerm.length() > 0) {
            queryString += "com.enfer.gov" + searchTerm;
        }
        searchTerm = "";
        Log.d(TAG, "getPlaces: searchTerm " + queryString);

        if (checkConnectivity() && queryString.length() != 0) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString(QUERY_STRING, queryString);
            getSupportLoaderManager().restartLoader(0, queryBundle, this);
        } else {
            if (queryString.length() == 0) {
                Toast.makeText(getApplicationContext(), "i have no querystring to work with...", Toast.LENGTH_LONG).show();
            } else {
                //go to no connection activity
                addFragments();
            }
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new PlacesLoader(this, bundle.getString(QUERY_STRING));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        JSONObject jsonObject = null;
        if (s == null) {
            Toast.makeText(getApplicationContext(), "onLoadFinished No Results", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            Log.i(TAG, "onLoadFinished: " + s);
            jsonObject = new JSONObject(s);
            JSONObject response = jsonObject.getJSONObject("response");
            JSONArray venuesArray = response.getJSONArray("venues");

            if (!places.isEmpty()) {
                places.clear();
            }
            //Iterate through the results
            for (int i = 0; i < venuesArray.length(); i++) {
                JSONObject venue = venuesArray.getJSONObject(i); //Get the current venue

                MyPlace place = new MyPlace();
                JSONObject location = venue.getJSONObject("location");
                try {
                    place.setName(venue.getString("name"));
                    place.setAddress(location.getString("address"));
                    double conversion = 1; // feet to meters
                    if (unit.equals("Ft")){
                        conversion = 3.28;
                    }
                    place.setDistance((int)(Math.round(location.getInt("distance") * conversion)));
                    place.setCity(location.getString("city"));
                    place.setLat(location.getDouble("lat"));
                    place.setLng(location.getDouble("lng"));
                    places.add(place);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            Log.d(TAG, "onLoadFinished: places " + Arrays.toString(places.toArray()));
            //save list as Json
            DataPipe.makeJsonFromList(this, places);

            //the places list goes to FragmentPlaces. it's time to add the fragments
            if (fragmentPlaces == null) {
                addFragments();
            } else {
                removeFragment(fragmentPlaces);
                addFragments();
            }


        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "No results were found", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    public void addFragments() {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        fragmentPlaces = new FragmentPlaces();
        fragmentMap = new FragmentMap();
        fragmentPlaceDetails = new FragmentPlaceDetails();

        if (isLarge && checkConnectivity()) {
            transaction.add(R.id.fragment_left, fragmentPlaces);
            transaction.add(R.id.fragment_right, fragmentMap);
            transaction.commit();
        } else if (checkConnectivity()) {
            transaction.add(R.id.places_list, fragmentPlaces);
            transaction.commit();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_LONG).show();
            Intent noConnectionIntent = new Intent(getApplicationContext(), NoConnectionActivity.class);
            startActivity(noConnectionIntent);
        }

    }


    @Override
    public void onRecyclerItemClicked(int position) {
        Log.d(TAG, "onRecyclerItemClicked: " + position + " " + places.get(position));
        SharedPreferences prefs = this.getSharedPreferences("com.example.enfer.navigator.activities", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("position", position);
        editor.apply();
        fragmentManager = getSupportFragmentManager();
        if (isLarge) {
            replaceFragment(R.id.fragment_left, fragmentPlaceDetails);
            replaceFragment(R.id.fragment_right, fragmentMap);
            btnResults.setVisibility(View.VISIBLE);
            btnResults.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeFragment(fragmentPlaceDetails);
                    btnResults.setVisibility(View.INVISIBLE);
                    addFragments();
                }
            });
            btnResults.dispatchSystemUiVisibilityChanged(View.VISIBLE);
        } else {
            btnMap.setVisibility(View.VISIBLE);
            btnToSearchResults.setVisibility(View.VISIBLE);
            replaceFragment(R.id.places_list, fragmentPlaceDetails);
            btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    replaceFragment(R.id.places_list, fragmentMap);
                }
            });
            btnToSearchResults.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnMap.setVisibility(View.INVISIBLE);
                    btnToSearchResults.setVisibility(View.INVISIBLE);
                    replaceFragment(R.id.places_list, fragmentPlaces);
                }
            });
        }

    }

    @Override
    public void onRecyclerItemLongClicked(final int position) {
        View anchorView = findViewById(R.id.place_item_id);
        PopupMenu popup = new PopupMenu(MainActivity.this, anchorView);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_context, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_share:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        try {
                            sendIntent.putExtra(Intent.EXTRA_TEXT, places.get(position).toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        break;
                    case R.id.menu_item_add_favorite:
                        try {
                            dao.addPlace(places.get(position));
                            Log.d(TAG, "onMenuItemClick: dao" + dao.getPlacesCount());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "onMenuItemClick: dao.addPlace\n" + e.getMessage());
                        }
                        break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu

    }


    private void replaceFragment(int fragment_container, Fragment fragment) {
        transaction = fragmentManager.beginTransaction();
        transaction.replace(fragment_container, fragment);
        transaction.commit();
    }

    private void removeFragment(Fragment fragment) {
        transaction = fragmentManager.beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }

    @Override
    public void getSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        places.clear();
        Log.d(TAG, "getSearchTerm: searchTerm " + this.searchTerm);
        getLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("radius", radiusMeter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_item_distance_setting:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_favorites:
                dao = new Dao(this);
                try {
                    places.clear();
                    places.addAll(dao.getAllPlaces());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onCreate: places " + Arrays.toString(places.toArray()));

                DataPipe.makeJsonFromList(this, places);

                if (fragmentPlaces == null) {
                    addFragments();
                } else {
                    removeFragment(fragmentPlaces);
                    addFragments();
                }

                return true;

            case R.id.menu_item_delete_favorites:
                dao = new Dao(this);
                try {
                    dao.deleteAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
        }
        return true;
    }
}
