package com.example.enfer.navigator.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class CustomReceiver extends BroadcastReceiver {
    //    //const
//    private static final String ACTION_CUSTOM_BROADCAST =
//            "com.example.enfer.powerreceiver.ACTION_CUSTOM_BROADCAST";
    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();

        String toastString = null;

        switch (intentAction){
            case Intent.ACTION_POWER_CONNECTED:
                toastString = "Power Connected!";
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                toastString = "Power Disconnected!";
                break;

        }
        Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();
    }
}
