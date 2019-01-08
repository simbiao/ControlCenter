package com.vsun.controlcenter.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vsun.controlcenter.R;

/**
 * Created by ubuntu on 17-11-14.
 */

public class ScreenTimeout extends LinearLayout{
    private String TAG = "ScreenTimeout";
    private WindowManager windowManager;

    private View bottomView;
    private Context context;
    private PopupView popupView;
    private WindowManager.LayoutParams params;
    public ScreenTimeout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ScreenTimeout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public ScreenTimeout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public ScreenTimeout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    private void init() {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void setBottomView(View v) {
        this.bottomView = v;
    }



    /**
     * Implement this method to handle touch screen motion events.
     * <p>
     * If this method is used to detect click actions, it is recommended that
     * the actions be performed by implementing and calling
     * {@link #performClick()}. This will ensure consistent system behavior,
     * including:
     * <ul>
     * <li>obeying click sound preferences
     * <li>dispatching OnClickListener calls
     * <li>handling {@link AccessibilityNodeInfo#ACTION_CLICK ACTION_CLICK} when
     * accessibility features are enabled
     * </ul>
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent");
        if(event != null) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    Log.e(TAG, "ACTION_DOWN");
                    animate().scaleX(1.08f).scaleY(1.08f).setDuration(150L);
                    return true;
                case MotionEvent.ACTION_UP:
                    Log.e(TAG, "ACTION_UP");
                    animate().scaleX(1.0f).scaleY(1.0f).setDuration(150L);
                    chooseTime();

                    return true;
            }
        }

        return super.onTouchEvent(event);
    }

    private void chooseTime() {
        popupView = (PopupView)((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.floating_view3, null);
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels);

        params = new WindowManager.LayoutParams(
                width,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH|WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                PixelFormat.OPAQUE);

        params.gravity = Gravity.CENTER | Gravity.CENTER;
        //params.x = 0;
        //params.y = 20;
        params.windowAnimations = R.style.DropDownDown;


        popupView.setBottomView(bottomView);
        windowManager.addView(popupView, params);

        popupView.setVisibility(View.VISIBLE);

        TextView t1 = (TextView) popupView.findViewById(R.id.first);
        t1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenOffTime(15000);
                popupView.setVisibility(GONE);
            }
        });
        TextView t2 = (TextView) popupView.findViewById(R.id.second);
        t2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenOffTime(30000);
                popupView.setVisibility(GONE);
            }
        });
        TextView t3 = (TextView) popupView.findViewById(R.id.third);
        t3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenOffTime(60000);
                popupView.setVisibility(GONE);
            }
        });
        TextView t4 = (TextView) popupView.findViewById(R.id.fourth);
        t4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenOffTime(120000);
                popupView.setVisibility(GONE);
            }
        });
        TextView t5 = (TextView) popupView.findViewById(R.id.fifth);
        t5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenOffTime(600000);
                popupView.setVisibility(GONE);
            }
        });
        TextView t6 = (TextView) popupView.findViewById(R.id.sixth);
        t6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenOffTime(1800000);
                popupView.setVisibility(GONE);
            }
        });
    }

    private void setScreenOffTime(int paramInt) {
        try {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT,
                    paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        int time = paramInt/1000;
        String printTime;
        if (time < 60) {
            printTime = time+" seconds";
        } else {
            time = time/60;
            printTime = time+" minutes";
        }

        Toast.makeText(context, "ScreenTimeout "+ printTime, Toast.LENGTH_SHORT).show();
    }
}
