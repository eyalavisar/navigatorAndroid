package com.example.enfer.navigator.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.enfer.navigator.R;
import com.example.enfer.navigator.beans.MyPlace;
import com.example.enfer.navigator.fragments.FragmentPlaces;
import com.example.enfer.navigator.recyclers.RecyclerViewAdapter;
import com.example.enfer.navigator.utils.DataPipe;

import java.util.ArrayList;
import java.util.List;

public class NoConnectionActivity extends AppCompatActivity {
    //const
    private static final String TAG = "NoConnectionActivity";
    //vars
    private Button checkConnectionButton;
    protected RecyclerView myRecyclerView;
    public List<MyPlace> places = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);

        checkConnectionButton = findViewById(R.id.btn_check_connection);
        checkConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToMainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(backToMainIntent);
            }
        });

        places = DataPipe.getListFromJson(this);
        Log.d(TAG, "onCreate: places = " + places.size());

        myRecyclerView = findViewById(R.id.place_recyclerview);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, places);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this ));
        myRecyclerView.setAdapter(recyclerViewAdapter);
    }
}
