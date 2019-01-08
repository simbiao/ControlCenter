package com.vsun.controlcenter.views;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.widget.AppCompatImageView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.vsun.controlcenter.R;
import com.vsun.controlcenter.utils.MobileDataSwitcher;

/**
 * Created by ubuntu on 17-10-20.
 */

public class DataActionView extends AppCompatImageView{
    public String TAG = "DataActionView";
    private Context context;

    private String DATAACTION = "com.vsun.controlcenter.data_state_change";

    private static final String INTENT_ACTION_AIRPLANE_CHANGE_DONE =
            "com.mediatek.intent.action.AIRPLANE_CHANGE_DONE";
    private TelephonyManager myTelephonyManager;

    private PhoneStateListener callStateListener = new PhoneStateListener(){
        public void onDataConnectionStateChanged(int state){
            switch(state){
                case TelephonyManager.DATA_DISCONNECTED:
                    Log.i(TAG + "State: ", "Offline");
                case TelephonyManager.DATA_CONNECTED:
                    Log.i(TAG + "State: ", "Connected");
                    Intent intent = new Intent(DATAACTION);
                    context.sendBroadcast(intent);
                    // String stateString = "Offline";
                    // Toast.makeText(getApplicationContext(),
                    // stateString, Toast.LENGTH_LONG).show();
                    break;
                case TelephonyManager.DATA_SUSPENDED:
                    Log.i(TAG + "State: ", "IDLE");
                    // stateString = "Idle";
                    // Toast.makeText(getApplicationContext(),
                    // stateString, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    public DataActionView(Context context) {
        super(context);
        this.context = context;
        registReceiver();
        myTelephonyManager=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        myTelephonyManager.listen(callStateListener,
                PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
    }

    public DataActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        registReceiver();
        myTelephonyManager=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        myTelephonyManager.listen(callStateListener,
                PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
    }

    public DataActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        registReceiver();
        myTelephonyManager=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        myTelephonyManager.listen(callStateListener,
                PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
    }

    private void registReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(DATAACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction(INTENT_ACTION_AIRPLANE_CHANGE_DONE);
        // /M: Maybe airplane mode need more time to turn on/off @{
        context.registerReceiver(mReceiver, filter);
    }

    public void setImage() {
        if (!isSIMInsert()) {
            this.setImageResource(R.drawable.no_sim_card);
            this.setClickable(false);
            return;
        }

        this.setClickable(true);
        Log.e(TAG, MobileDataSwitcher.getMobileDataState(context) + "");

        if (isAirplaneModeOnOff()) {
            Log.d(TAG, "setImage: 1");
            this.setImageResource(R.drawable.data_close);
        } else if (!isAirplaneModeOnOff()) {
            Log.d(TAG, "setImage: 2");
            if (MobileDataSwitcher.getMobileDataState(context)) {
                Log.d(TAG, "setImage: 3");
                this.setImageResource(R.drawable.data_open);
            } else if (!MobileDataSwitcher.getMobileDataState(context)) {
                Log.d(TAG, "setImage: 4");
                this.setImageResource(R.drawable.data_close);
            }
        }
    }

    private boolean isAirplaneModeOnOff() {

        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    private void setDataState() {

        if(MobileDataSwitcher.getMobileDataState(context)) {
            MobileDataSwitcher.setMobileDataState(context, false);
        } else {
            MobileDataSwitcher.setMobileDataState(context, true);
        }
    }



    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (DATAACTION.equals(intent.getAction())){
                Log.e(TAG, "onReceive");
                setImage();
            }

            if (INTENT_ACTION_AIRPLANE_CHANGE_DONE.equals(intent.getAction())) {
                Log.e(TAG, "AIRPLANE in dataaction");
                setImage();
                // @}
            }
        }
    };

    public boolean isSIMInsert() {

        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);// 取得相关系统服务
        String imsi = manager.getSubscriberId(); // 取出IMSI

        if (imsi == null || imsi.length() <= 0) {
            return false;

        } else {
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent");

        if(event != null && this.isClickable()) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    Log.e(TAG, "ACTION_DOWN");
                    animate().scaleX(1.1f).scaleY(1.1f).setDuration(150L);
                    return true;
                case MotionEvent.ACTION_UP:
                    Log.e(TAG, "ACTION_UP");
                    animate().scaleX(1.0f).scaleY(1.0f).setDuration(150L);
                    setDataState();
                    setImage();
                    return true;
            }
        }

        return super.onTouchEvent(event);
    }
}
