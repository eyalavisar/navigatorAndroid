package com.example.enfer.navigator.fragments;
//

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.enfer.navigator.R;
import com.example.enfer.navigator.activities.MainActivity;
import com.example.enfer.navigator.beans.MyPlace;
import com.example.enfer.navigator.utils.DataPipe;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;


public class FragmentMap extends Fragment implements OnMapReadyCallback {
    private GoogleMap mGoogleMap;
    private MapView mapView;
    private View v;
//    private MainActivity mainActivity;
    private LatLng currentLocation;
    private List<MyPlace> placeList = new ArrayList<>();


    public FragmentMap() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getContext().getSharedPreferences("com.example.enfer.navigator.activities", Context.MODE_PRIVATE);
        String prefsLocation = prefs.getString("location", null);
        String[] latlng = null;
        if(prefsLocation != null){
            latlng = prefsLocation.split(",");
            currentLocation = new LatLng
                    (Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        placeList = DataPipe.getListFromJson(getContext());

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_fragment_map, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = (MapView) v.findViewById(R.id.map);
        if (mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

//        statue of liberty
//        LatLng liberty = new LatLng(40.689247,-74.044582);
//        mGoogleMap.addMarker(new MarkerOptions().
//                position(currentLocation)).
//                setSnippet("You are here!");
        markPlace(currentLocation.latitude,currentLocation.longitude,BitmapDescriptorFactory.HUE_MAGENTA,"You Are:", "Here");


        MyPlace chosenPlace = new MyPlace();
        SharedPreferences prefs = getContext().getSharedPreferences("com.example.enfer.navigator.activities", Context.MODE_PRIVATE);

        Integer position = prefs.getInt("position", -1);
        if(position != -1){
            Log.d(TAG, "onResume: " + chosenPlace.toString());
            chosenPlace = placeList.get(position);
            Log.d(TAG, "onResume: " + chosenPlace.toString());
            markPlace(chosenPlace.getLat(),chosenPlace.getLng(),BitmapDescriptorFactory.HUE_RED,"Place Shown:", chosenPlace.getName());
        }
        CameraPosition currentPlace = CameraPosition.builder().
                target(currentLocation).
                zoom(16).
                bearing(0).
                tilt(45).
                build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
    }

    private void markPlace(Double lat, Double lng, float color, String title, String snippet) {
        LatLng latLng = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(color));
        markerOptions.title(title);
        markerOptions.snippet(snippet);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.addMarker(markerOptions);
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
}