package com.vsun.controlcenter.views;

import android.bluetooth.BluetoothAdapter;
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

public class BluetoothActionView extends AppCompatImageView{
    public String TAG = "BluetoothActionView";
    private Context context;

    public BluetoothActionView(Context context) {
        super(context);
        this.context = context;
        registReceiver();
    }

    public BluetoothActionView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        registReceiver();
    }

    public BluetoothActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;
        registReceiver();
    }

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    private void registReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        context.registerReceiver(mReceiver, filter);
    }

    public void setImage() {
        if (mBluetoothAdapter.isEnabled()) {
            this.setImageResource(R.drawable.bluetooth_open);
        } else {
            this.setImageResource(R.drawable.bluetooth_close);
        }
    }

    private void setBluetoothState() {
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        } else {
            mBluetoothAdapter.enable();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())){
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
                    setBluetoothState();
                    setImage();

                    return true;
            }
        }

        return super.onTouchEvent(event);
    }
}
