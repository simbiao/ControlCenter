package com.vsun.controlcenter.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by ubuntu on 17-11-14.
 */

public class PopupView extends LinearLayout{
    private String TAG = "PopupView";
    private View bottomView;
    private Context context;
    public PopupView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public PopupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public PopupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public PopupView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    private void init() {
        InnerRecevier innerReceiver = new InnerRecevier();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.registerReceiver(innerReceiver, intentFilter);
    }

    public void setBottomView(View v) {
        this.bottomView = v;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                Log.e(TAG, "KEYCODE_BACK");
                PopupView.this.setVisibility(View.GONE);
                if(bottomView != null){
                    bottomView.setVisibility(View.VISIBLE);
                }
                return true;
            case KeyEvent.KEYCODE_MENU:
                Log.e(TAG, "KEYCODE_MENU");
                // 处理自己的逻辑break;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "PopupView ACTION UP");
                PopupView.this.setVisibility(View.GONE);
                return true;
        }
        return super.onTouchEvent(event);
    }

    class InnerRecevier extends BroadcastReceiver {

        final String SYSTEM_DIALOG_REASON_KEY = "reason";

        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {

                        PopupView.this.setVisibility(View.GONE);
                        if (bottomView != null){
                            bottomView.setVisibility(View.VISIBLE);
                        }
                        Log.e(TAG, "Home");
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        Log.e(TAG, "Multi");
                    }
                }
            }
        }
    }
}
