package com.example.enfer.navigator.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.enfer.navigator.beans.MyPlace;
import com.example.enfer.navigator.R;
import com.example.enfer.navigator.recyclers.RecyclerTouchListener;
import com.example.enfer.navigator.recyclers.RecyclerViewAdapter;
import com.example.enfer.navigator.utils.DataPipe;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPlaces extends Fragment {

    private static final String TAG = "FragmentPlaces";
    protected View v;
    protected RecyclerView myRecyclerView;
    protected List<MyPlace> placeList = new ArrayList<>();
    private EditText etSearchTerm;
    private Button searchByTermBtn;
    private Button searchNearByBtn;


    private OnFragmentInteractionListener mListener;

    public FragmentPlaces() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get list
        placeList = DataPipe.getListFromJson(getContext());

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_fragment_places, container, false);

        etSearchTerm = (EditText) v.findViewById(R.id.et_search_term);
        searchByTermBtn = (Button) v.findViewById(R.id.btn_search_term);
        searchByTermBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etSearchTerm.getText() != null && etSearchTerm.getText().toString().length() > 0){
                    mListener.getSearchTerm(etSearchTerm.getText().toString());
                }
                else {
                    Toast.makeText(getContext(),"No SearchTerm Found",Toast.LENGTH_LONG).show();
                }
            }
        });
        searchNearByBtn = (Button) v.findViewById(R.id.btn_search_nearby);
        searchNearByBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.getSearchTerm(etSearchTerm.getText().toString());
            }
        });


        myRecyclerView = v.findViewById(R.id.place_recyclerview);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(), placeList);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecyclerView.setAdapter(recyclerViewAdapter);

        myRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), myRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                //   Toast.makeText(getActivity(), position+ " is selected successfully", Toast.LENGTH_SHORT).show();

                //handle click event
                mListener.onRecyclerItemClicked(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                mListener.onRecyclerItemLongClicked(position);
            }
        }));

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + "must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        void onRecyclerItemClicked(int position);
        void onRecyclerItemLongClicked(int position);
        void getSearchTerm(String searchTerm);
    }
}
