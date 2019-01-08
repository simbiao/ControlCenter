package com.vsun.controlcenter.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vsun.controlcenter.activities.MainActivity;

/**
 * Created by ubuntu on 17-11-14.
 */

public class FloatLayout extends LinearLayout {
    private String TAG = "FloatLayout";
    private View bottomView;
    private Context context;
    public FloatLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public FloatLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public FloatLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public FloatLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
                setVisibility(View.GONE);
                if(bottomView != null){
                    bottomView.setVisibility(View.VISIBLE);
                }
                break;
            case KeyEvent.KEYCODE_MENU:
                Log.e(TAG, "KEYCODE_MENU");
                // 处理自己的逻辑break;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
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

                        setVisibility(View.GONE);
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
