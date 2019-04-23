package com.example.enfer.navigator.recyclers;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.enfer.navigator.R;
import com.example.enfer.navigator.activities.MainActivity;
import com.example.enfer.navigator.beans.MyPlace;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List <MyPlace> mData;

    public RecyclerViewAdapter(Context mContext, List<MyPlace> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;

        v = LayoutInflater.from(mContext).inflate(R.layout.item_place, viewGroup, false);
        final MyViewHolder viewHolder = new MyViewHolder(v);

        viewHolder.item_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(mContext, viewHolder.tvName.getText().toString(), Toast.LENGTH_LONG).show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.tvName.setText(mData.get(i).getName());

        SharedPreferences prefs = mContext.getSharedPreferences("com.example.enfer.navigator.activities", Context.MODE_PRIVATE);
        String unit = "ms";
        if (prefs.getString("unit", null) != null) {
            unit = prefs.getString("unit", null);
        }
        myViewHolder.tvDistance.setText(mData.get(i).getDistance() + unit);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout item_place;
        private TextView tvName;
        private TextView tvDistance;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_place = (LinearLayout) itemView.findViewById(R.id.place_item_id);
            tvName = (TextView) itemView.findViewById(R.id.name_place);
            tvDistance = (TextView) itemView.findViewById(R.id.distance_place);
        }


    }


}
