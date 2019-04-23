package com.example.enfer.navigator.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.enfer.navigator.R;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("com.example.enfer.navigator.activities", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        Switch unitSwitch = (Switch) findViewById(R.id.btn_switch);
        unitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String unit = "Ms";
                if (isChecked) {
                    unit = "Ft";
                    Toast.makeText(getApplicationContext(),"Miles",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(),"Kms",Toast.LENGTH_LONG).show();
                }
                editor.putString("unit",unit);
                editor.apply();
            }
        });

        final EditText editTextRadius = findViewById(R.id.et_radius);
        Button saveButton = findViewById(R.id.btn_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Integer.parseInt(editTextRadius.getText().toString());
                    editor.putString("Radius",editTextRadius.getText().toString());
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }catch (Exception e){
                    Log.i(TAG, "onClick: no number entered" + e.getMessage());
                }
            }
        });
    }
}
