package com.example.callkit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.callkit.database.Contact;
import com.example.callkit.database.DatabaseHandler;

import java.util.List;

public class CallReceiver extends BroadcastReceiver {
    String state, number, message;

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHandler db = new DatabaseHandler(context);
        state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            message = "phone is ringing";
            if (number != null) {
                // DatabaseHandler db = new DatabaseHandler(context);
                Contact contact = db.getContactsByNumber(number);
                Toast.makeText(context, "Incoming Call From:" + number, Toast.LENGTH_SHORT).show();
                // Intent alarmIntent = new Intent(context, AlertDialogClass.class);

                Intent alarmIntent = new Intent("android.intent.action.MAIN");
//                alarmIntent.setClass(context, AlertDialogClass.class);
                alarmIntent.setClass(context, AlertDialogClass.class);

                // Start the popup activity

                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                alarmIntent.putExtra("phoneNumber", number);
                String contactName = number;
                if (contact != null)
                    contactName = contact.getName();
                alarmIntent.putExtra("contactName", contactName);
                // Start the popup activity
                new Handler().postDelayed(() -> context.startActivity(alarmIntent), 2000);
            }
        }
        if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))) {
            Toast.makeText(context, "Call Received", Toast.LENGTH_SHORT).show();

            number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            if (number != null)
                Toast.makeText(context, "Outgoing Call to:" + number, Toast.LENGTH_SHORT).show();
        }
        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

        }
    }
}