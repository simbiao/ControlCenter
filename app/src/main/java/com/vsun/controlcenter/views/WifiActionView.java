package com.vsun.controlcenter.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.vsun.controlcenter.R;

/**
 * Created by ubuntu on 17-10-20.
 */

public class WifiActionView extends AppCompatImageView{
    public String TAG = "WifiActionView";
    private Context context;
    private WifiManager wifiManager;

    public WifiActionView(Context context) {
        super(context);
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        registReceiver();
    }

    public WifiActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        registReceiver();
    }

    public WifiActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        registReceiver();
    }

    private void registReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        context.registerReceiver(mReceiver, filter);
    }

    public void setImage() {
        if (wifiManager.isWifiEnabled()) {
            this.setImageResource(R.drawable.wifi_open);
        } else {
            this.setImageResource(R.drawable.wifi_close);
        }
    }

    private void setWifiState() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        } else {
            wifiManager.setWifiEnabled(true);
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())){
                setImage();
            }
        }
    };

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
                    setWifiState();
                    setImage();
                    return true;
            }
        }

        return super.onTouchEvent(event);
    }
}
