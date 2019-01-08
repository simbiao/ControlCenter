package com.vsun.controlcenter.views;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.mediatek.internal.telephony.ITelephonyEx;
import com.vsun.controlcenter.R;

/**
 * Created by ubuntu on 17-10-20.
 */

public class FlightModeActionView extends AppCompatImageView{
    private static final String INTENT_ACTION_AIRPLANE_CHANGE_DONE =
            "com.mediatek.intent.action.AIRPLANE_CHANGE_DONE";
    private static final String EXTRA_AIRPLANE_MODE = "airplaneMode";


    public String TAG = "FlightModeActionView";
    private Context context;
    private ContentResolver cr;
    public FlightModeActionView(Context context) {
        super(context);
        this.context = context;
        cr = context.getContentResolver();
        registReceiver();
    }

    public FlightModeActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        cr = context.getContentResolver();
        registReceiver();
    }


    public FlightModeActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        cr = context.getContentResolver();
        registReceiver();
    }

    private void registReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        // /M: Maybe airplane mode need more time to turn on/off @{
        filter.addAction(INTENT_ACTION_AIRPLANE_CHANGE_DONE);
        // @}
        context.registerReceiver(mReceiver, filter);
        if (!isAirplanemodeAvailableNow()) {
            Log.d(TAG, "setListening() Airplanemode not Available, start anim.");
            //startAnimation();
        }

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(event);
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
                    setAirplaneMode();
                    setImage();

                    animate().scaleX(1.0f).scaleY(1.0f).setDuration(150L);
                    return true;
            }
        }

        return super.onTouchEvent(event);
    }

    public void setImage() {
        if (isAirplaneModeOnOff()) {
            this.setImageResource(R.drawable.flight_mode_open);
        } else {
            this.setImageResource(R.drawable.flight_mode_close);
        }
    }

    private void setAirplaneMode() {
        if ( isAirplaneModeOnOff()) {
            setAirplaneEnabled(false);

        } else {
            setAirplaneEnabled(true);
        }
    }

    private void setAirplaneEnabled(boolean enabled) {
        Log.d(TAG, "setEnabled = " + enabled);
        final ConnectivityManager mgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mgr.setAirplaneMode(enabled);
    }


    private boolean isAirplaneModeOnOff() {

        return Settings.Global.getInt(cr, Settings.Global.AIRPLANE_MODE_ON, 0) != 0;


    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
                // M: Maybe airplane mode need more time to turn on/off
                // refreshState();
            }
            // /M: Maybe airplane mode need more time to turn on/off @{
            else if (INTENT_ACTION_AIRPLANE_CHANGE_DONE.equals(intent.getAction())) {
                boolean airplaneModeOn = intent.getBooleanExtra(EXTRA_AIRPLANE_MODE, false);
                Log.d(TAG, "onReceive() AIRPLANE_CHANGE_DONE,  airplaneModeOn= " + airplaneModeOn);
                setImage();
                // @}
            }
        }
    };

    private boolean isAirplanemodeAvailableNow() {
        ITelephonyEx telephonyEx = ITelephonyEx.Stub.asInterface(ServiceManager
                .getService(Context.TELEPHONY_SERVICE_EX));
        boolean isAvailable = true;
        try {
            if (telephonyEx != null) {
                isAvailable = telephonyEx.isAirplanemodeAvailableNow();
            } else {
                Log.w(TAG, "telephonyEx == null");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "isAirplaneModeAvailable = " + isAvailable);
        return isAvailable;
    }
}
