package com.vsun.controlcenter.views;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.vsun.controlcenter.R;

/**
 * Created by ubuntu on 17-10-20.
 */

public class DisturbActionView extends AppCompatImageView{
    public String TAG = "DisturbActionView";
    private Context context;
    private NotificationManager mNotificationManager;

    public DisturbActionView(Context context) {
        super(context);
        this.context = context;
        registReceiver();
    }

    public DisturbActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        registReceiver();
    }

    public DisturbActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        registReceiver();
    }

    private void registReceiver() {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        final IntentFilter filter = new IntentFilter();
        filter.addAction(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED);
        context.registerReceiver(mReceiver, filter);
    }

    public void setImage() {

        RelativeLayout parent = (RelativeLayout)this.getParent();
        if (mNotificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_ALL) {
            this.setImageResource(R.mipmap.control_not_disturb_close);
            parent.setBackground(getResources().getDrawable(R.drawable.background1x1));
        } else {
            this.setImageResource(R.mipmap.control_not_disturb_open);
            parent.setBackground(getResources().getDrawable(R.drawable.background1x1_white));
        }
    }

    private void setDndState() {

        if (mNotificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_ALL) {
            mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS);
        } else {
            mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        }

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)){
                setImage();
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent");
        RelativeLayout parent = (RelativeLayout)this.getParent();
        if(event != null) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    Log.e(TAG, "ACTION_DOWN");
                    animate().scaleX(1.1f).scaleY(1.1f).setDuration(150L);
                    //parent.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150L);
                    return true;
                case MotionEvent.ACTION_UP:
                    Log.e(TAG, "ACTION_UP");
                    animate().scaleX(1.0f).scaleY(1.0f).setDuration(150L);
                    //parent.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150L);
                    setDndState();
                    setImage();
                    return true;
            }
        }

        return super.onTouchEvent(event);
    }

}
