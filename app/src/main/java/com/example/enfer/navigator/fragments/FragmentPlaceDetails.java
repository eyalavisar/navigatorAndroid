package com.example.enfer.navigator.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.enfer.navigator.R;
import com.example.enfer.navigator.activities.MainActivity;
import com.example.enfer.navigator.beans.MyPlace;
import com.example.enfer.navigator.utils.DataPipe;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPlaceDetails extends Fragment {


    private View v;
    private MainActivity mainActivity;
    private TextView tvCity;
    private TextView tvName;
    private TextView tvAddress;
    private TextView tvDistance;

    public FragmentPlaceDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_fragment_place_details, container, false);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity = (MainActivity) getActivity();
        Integer distance = null;
        SharedPreferences prefs = getContext().getSharedPreferences("com.example.enfer.navigator.activities", Context.MODE_PRIVATE);

        //place clicked in list. initialised to a none value.
        Integer mPositionClicked = prefs.getInt("position", -1);

        ArrayList<MyPlace> myPlaces = (ArrayList<MyPlace>) DataPipe.getListFromJson(getContext());
        tvCity = v.findViewById(R.id.city);
        tvCity.setText(myPlaces.get(mPositionClicked).getCity());

        tvName = v.findViewById(R.id.name);
        tvName.setText(myPlaces.get(mPositionClicked).getName());

        tvAddress = v.findViewById(R.id.address);
        tvAddress.setText(myPlaces.get(mPositionClicked).getAddress());

        tvDistance = v.findViewById(R.id.distance);

        String unit = "ms";
        if (prefs.getString("unit", null) != null) {
            unit = prefs.getString("unit", null);
        }
        if(distance != null){
            tvDistance.setText("0" + unit);
        }
        else {
            tvDistance.setText(myPlaces.get(mPositionClicked).getDistance() + unit);
        }
    }
}
