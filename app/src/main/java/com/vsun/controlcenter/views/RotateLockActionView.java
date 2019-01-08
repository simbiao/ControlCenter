package com.vsun.controlcenter.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vsun.controlcenter.R;



/**
 * Created by ubuntu on 17-10-20.
 */

public class RotateLockActionView extends AppCompatImageView{
    public String TAG = "RotateLockActionView";
    private static final String INTENT_ACTION_AIRPLANE_CHANGE_DONE =
            "com.mediatek.intent.action.AIRPLANE_CHANGE_DONE";
    private Context context;

    public RotateLockActionView(Context context) {
        super(context);
        this.context = context;
        registReceiver();
    }

    public RotateLockActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        registReceiver();
    }

    public RotateLockActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        registReceiver();
    }

    /**
     * 是否开启自动旋转
     * @return
     */
    public boolean isRotationOn() {
        int status = 0;

        try {
            status = android.provider.Settings.System.getInt(
                    context.getContentResolver(),
                    android.provider.Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return (status == 0)? false:true;
    }


    public void setRotation() {
        int status = 0;

        Uri uri = android.provider.Settings.System
                .getUriFor("accelerometer_rotation");
        if (!isRotationOn()) {
            status = 1;
        } else if (status == 1) {
            status = 0;
        }
        android.provider.Settings.System.putInt(context.getContentResolver(),
                "accelerometer_rotation", status);

        context.getContentResolver().notifyChange(uri, null);
    }

    private ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // show a toast here
            setImage();
        }
    };


    private void registReceiver() {
        context.getContentResolver().registerContentObserver(Settings.System.getUriFor
                        (Settings.System.ACCELEROMETER_ROTATION), true,contentObserver);

    }

    public void setImage() {
        RelativeLayout parent = (RelativeLayout) this.getParent();
        if (!isRotationOn()) {
            parent.setBackground(getResources().getDrawable(R.drawable.background1x1_white));
            this.setImageResource(R.mipmap.control_lock_screen_open);
        } else {
            parent.setBackground(getResources().getDrawable(R.drawable.background1x1));
            this.setImageResource(R.mipmap.control_lock_screen_close);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent");
        if(event != null) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    Log.e(TAG, "ACTION_DOWN");
                    animate().scaleX(1.1f).scaleY(1.1f).setDuration(150L);
                    return true;
                case MotionEvent.ACTION_UP:
                    Log.e(TAG, "ACTION_UP");
                    animate().scaleX(1.0f).scaleY(1.0f).setDuration(150L);
                    setRotation();
                    setImage();
                    return true;
            }
        }

        return super.onTouchEvent(event);
    }

}
