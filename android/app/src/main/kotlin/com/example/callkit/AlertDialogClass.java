package com.example.callkit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlertDialogClass extends Activity implements View.OnTouchListener{
    AlertDialog.Builder mAlertDlgBuilder;
    AlertDialog mAlertDialog;
    View mDialogView = null;
    ImageButton mCloseBtn;
    TextView userNameTextView, phoneNumberTextView;

    String TAG = "AlertDialogCK";

    @SuppressLint("StaticFieldLeak")
    private static LinearLayout windowView;

    private static WindowManager wm;

    private Context mContext;

    private float offsetX;
    private float offsetY;
    private int originalXPos;
    private int originalYPos;
    private boolean moving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        ----------------------------------------------------
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getLayoutInflater();
        String contactName = getIntent().getStringExtra("contactName");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        // Build the dialog
        mAlertDlgBuilder = new AlertDialog.Builder(this);
        mDialogView = inflater.inflate(R.layout.dialog_layout, null);
        phoneNumberTextView = (TextView) mDialogView.findViewById(R.id.phone_number);
        userNameTextView = (TextView) mDialogView.findViewById(R.id.user_name);
        mCloseBtn = (ImageButton) mDialogView.findViewById(R.id.close_dialogue);

        userNameTextView.setText(contactName);
        phoneNumberTextView.setText(phoneNumber);

        mCloseBtn.setOnClickListener(mDialogbuttonClickListener);
        mAlertDlgBuilder.setCancelable(false);
        mAlertDlgBuilder.setInverseBackgroundForced(true);
        mAlertDlgBuilder.setView(mDialogView);

        mAlertDialog = mAlertDlgBuilder.create();

        mAlertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }else{
            mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
        mAlertDialog.setCanceledOnTouchOutside(true);


        mAlertDialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = mAlertDialog.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setDimAmount(0);
        window.setGravity(Gravity.CENTER);
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);


    }

    @SuppressLint("ClickableViewAccessibility")
    private void buildWindowView(LinearLayout.LayoutParams params, boolean enableDraggable) {
        windowView = new LinearLayout(this);
        windowView.setOrientation(LinearLayout.VERTICAL);
        windowView.setBackgroundColor(Color.WHITE);
        windowView.setLayoutParams(params);
        windowView.removeAllViews();
            windowView.addView(mDialogView);
        if (enableDraggable)
            windowView.setOnTouchListener(this);
    }

    private void closeOverlayService() {
        Log.d(TAG, "Ending the service process");
        try {
            if (wm != null) {
                if(windowView != null) {
                    wm.removeView(windowView);
                }
            }
            wm = null;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "view not found");
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (null != wm) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float x = event.getRawX();
                float y = event.getRawY();
                moving = false;
                int[] location = new int[2];
                windowView.getLocationOnScreen(location);
                originalXPos = location[0];
                originalYPos = location[1];
                offsetX = originalXPos - x;
                offsetY = originalYPos - y;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float x = event.getRawX();
                float y = event.getRawY();
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) windowView.getLayoutParams();
                int newX = (int) (offsetX + x);
                int newY = (int) (offsetY + y);
                if (Math.abs(newX - originalXPos) < 1 && Math.abs(newY - originalYPos) < 1 && !moving) {
                    return false;
                }
                params.x = newX;
                params.y = newY;
                wm.updateViewLayout(windowView, params);
                moving = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                return moving;
            }
        }
        return false;
    }


    View.OnClickListener mDialogbuttonClickListener = v -> {
        if (v.getId() == R.id.close_dialogue) {
            finish();
            System.exit(0);
        }
    };
}