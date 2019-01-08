package com.vsun.controlcenter.services;

/**
 * Created by ubuntu on 17-10-20.
 */

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.widget.AppCompatImageView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vsun.controlcenter.R;
import com.vsun.controlcenter.customui.RectProgress;
import com.vsun.controlcenter.utils.Utils;
import com.vsun.controlcenter.views.BluetoothActionView;
import com.vsun.controlcenter.views.BottomButtonActionView;
import com.vsun.controlcenter.views.DataActionView;
import com.vsun.controlcenter.views.DisturbActionView;
import com.vsun.controlcenter.views.FlightModeActionView;
import com.vsun.controlcenter.views.FloatLayout;
import com.vsun.controlcenter.views.RotateLockActionView;
import com.vsun.controlcenter.views.ScreenTimeout;
import com.vsun.controlcenter.views.WifiActionView;

import java.util.Timer;
import java.util.TimerTask;


public class ControlCenterService extends Service {

    private String TAG = "ControlCenterService";
    WindowManager.LayoutParams params;
    WindowManager.LayoutParams bottomParams;

    private WindowManager windowManager;
    private FloatLayout overlayView;
    private TextView title;
    private AppCompatImageView play;
    private View overlayBottom;

    private FlightModeActionView flightMode;
    private WifiActionView wifiAction;
    private BluetoothActionView bluetoothAction;
    private DataActionView dataAction;

    private BottomButtonActionView first_first;
    private BottomButtonActionView first_second;
    private BottomButtonActionView first_third;
    private BottomButtonActionView first_fourth;

    private BottomButtonActionView second_first;
    private BottomButtonActionView second_second;
    private BottomButtonActionView second_third;
    private BottomButtonActionView second_fourth;

    private RectProgress light_progressbar;
    private RectProgress volum_progressbar;

    private ScreenTimeout screen_timeout;
    private RotateLockActionView lock;
    private DisturbActionView disturb;
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDSTOP = "stop";
    private Timer timer;

    private BroadcastReceiver uiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean isForeground = intent.getBooleanExtra("foreground", false);
            if (isForeground) {
                overlayBottom.setVisibility(View.VISIBLE);
                Log.e(TAG, "bottom visible");
            } else {
                overlayBottom.setVisibility(View.GONE);
                Log.e(TAG, "bottom GONE");
            }
        }
    };

    private BroadcastReceiver musicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            Log.d("Music", cmd + " :" + action);
            String artist = intent.getStringExtra("artist");
            String album = intent.getStringExtra("album");
            String track = intent.getStringExtra("track");
            boolean playing = intent.getBooleanExtra("playing", false);
            Log.d("Music", artist + " :" + album + " :" + track);
            if (!playing) {
                title.setText(track);
                play.setImageResource(R.mipmap.control_music_play_normal);
            } else {
                //title.setText(track);
                play.setImageResource(R.mipmap.control_music_pause_normal);
                title.setText(artist + "\n" + album + "\n" + track);
                String uri = Uri.parse("http://api.vagalume.com.br/search.php")
                        . buildUpon()
                        . appendQueryParameter("mus", track)
                        . appendQueryParameter("art", artist)
                        . build().toString();
                if (uri!= null) {
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        startTimer();

        addOverlayView();


        return super.onStartCommand(intent, flags, startId);
    }

    private void startTimer() {
        timer = new Timer();
        timer.schedule( new TimerTask()
        {

            @Override
            public void run()
            {
                if(Utils.isLauncherForeground(ControlCenterService.this)){

                    Intent intent = new Intent();
                    intent.setAction("islauncherforeground");
                    intent.putExtra("foreground", true);
                    sendBroadcast(intent);
                    Log.e(TAG, "run true");
                } else {
                    Intent intent = new Intent();
                    intent.setAction("islauncherforeground");
                    intent.putExtra("foreground", false);
                    sendBroadcast(intent);
                    Log.e(TAG, "run false");
                }
            }
        }, 0, 300 );
    }

    private void addOverlayView() {
        setParams();

        setOverlayView();

        setDataRelate();

        setShowPositionReceiver();

        setMusicRelate();

        setLockDisturbTimeout();

        setBottomParams();

        setOverlayBottom();

        windowManagerAddView();

        setOverlayListerner();

        setBottomButtonAction();

        setProgressbar();
    }

    private void setOverlayBottom() {
        overlayBottom = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.floating_view2, null);
        overlayView.setBottomView(overlayBottom);
    }

    private void windowManagerAddView() {
        windowManager.addView(overlayBottom, bottomParams);
        windowManager.addView(overlayView, params);
    }

    private void setOverlayListerner() {
        overlayView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private float startY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        startY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float endY = motionEvent.getY();
                        if (endY > startY && endY - startY > 150) {
                            //Move down
                            overlayView.setVisibility(View.GONE);
                            overlayBottom.setVisibility(View.VISIBLE);
                        }

                        params.y = 20;
                        windowManager.updateViewLayout(overlayView, params);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX - (int) (motionEvent.getRawX() - initialTouchX);
                        params.y = initialY - (int) (motionEvent.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(overlayView, params);
                        break;
                }
                params.x = 0;
                windowManager.updateViewLayout(overlayView, params);
                return false;
            }
        });

        overlayBottom.setOnTouchListener(new View.OnTouchListener() {

            private float starty;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                overlayView.setVisibility(View.VISIBLE);
                overlayBottom.setVisibility(View.GONE);
                flightMode.setImage();
                wifiAction.setImage();
                dataAction.setImage();
                bluetoothAction.setImage();
                initLightProgressbar();
                initVolumProgressbar();

                return false;
            }
        });
    }

    private void setBottomParams() {
        bottomParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                PixelFormat.TRANSLUCENT);
        bottomParams.gravity = Gravity.BOTTOM;
        //bottomParams.alpha=0.1f;
    }

    private void setLockDisturbTimeout() {
        lock = (RotateLockActionView) overlayView.findViewById(R.id.lock);
        lock.setImage();
        disturb = (DisturbActionView) overlayView.findViewById(R.id.disturb);
        disturb.setImage();
        screen_timeout = (ScreenTimeout) overlayView.findViewById(R.id.timeout);
        screen_timeout.setBottomView(overlayBottom);
    }

    private void setBottomButtonAction() {
        first_first = (BottomButtonActionView)overlayView.findViewById(R.id.first_first);
        first_first.setShortcut("com.android.fmradio");
        first_first.setImage();
        first_first.setBottomView(overlayBottom);

        first_second = (BottomButtonActionView)overlayView.findViewById(R.id.first_second);
        first_second.setShortcut("com.android.deskclock");
        first_second.setImage();
        first_second.setBottomView(overlayBottom);

        first_third = (BottomButtonActionView)overlayView.findViewById(R.id.first_third);
        first_third.setShortcut("com.android.calculator2");
        first_third.setImage();
        first_third.setBottomView(overlayBottom);

        first_fourth = (BottomButtonActionView)overlayView.findViewById(R.id.first_fourth);
        first_fourth.setShortcut("com.mediatek.camera");
        first_fourth.setImage();
        first_fourth.setBottomView(overlayBottom);

        second_first = (BottomButtonActionView)overlayView.findViewById(R.id.second_first);
        second_first.setImage();
        second_first.setBottomView(overlayBottom);

        second_second = (BottomButtonActionView)overlayView.findViewById(R.id.second_second);
        second_second.setImage();
        second_second.setBottomView(overlayBottom);

        second_third = (BottomButtonActionView)overlayView.findViewById(R.id.second_thrid);
        second_third.setImage();
        second_third.setBottomView(overlayBottom);

        second_fourth = (BottomButtonActionView)overlayView.findViewById(R.id.second_fourth);
        second_fourth.setImage();
        second_fourth.setBottomView(overlayBottom);
    }

    private void setProgressbar() {
        light_progressbar = (RectProgress)overlayView.findViewById(R.id.light_progressbar);

        light_progressbar.setChangedListener(new RectProgress.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int currentValue, int percent) {

                ContentResolver contentResolver = getContentResolver();
                try {
                    int mode = Settings.System.getInt(contentResolver,
                            Settings.System.SCREEN_BRIGHTNESS_MODE);
                    if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    }
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }

                Settings.System.putInt(contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS, (int)(percent*255.0/100));
            }
        });

        volum_progressbar = (RectProgress)overlayView.findViewById(R.id.volum_progressbar);

        volum_progressbar.setChangedListener(new RectProgress.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int currentValue, int percent) {
                Log.e(TAG, "currentValue = " + currentValue);
                AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(percent*max/100.0), 0);
            }
        });
    }

    private void setMusicRelate() {
        RelativeLayout music = (RelativeLayout)overlayView.findViewById(R.id.music);
        title = (TextView) overlayView.findViewById(R.id.title);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.music.musicservicecommand");
        intentFilter.addAction("com.android.music.metachanged");
        intentFilter.addAction("com.android.music.playstatechanged");
        intentFilter.addAction("com.android.music.updateprogress");
        registerReceiver(musicReceiver, intentFilter);


        play = (AppCompatImageView) overlayView.findViewById(R.id.play);

        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        play.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long eventtime = SystemClock.uptimeMillis();
                KeyEvent downEvent;
                switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            Log.e(TAG, "play ACTION_DOWN");
                            v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150L);
                            downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                            mAudioManager.dispatchMediaKeyEvent(downEvent);
                            return true;
                        case MotionEvent.ACTION_UP:
                            Log.e(TAG, "play ACTION_UP");
                            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150L);

                            Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                            downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                            mAudioManager.dispatchMediaKeyEvent(downEvent);

                            return true;
                    }
                return false;
            }
        });

        AppCompatImageView privious = (AppCompatImageView) overlayView.findViewById(R.id.privious);

        privious.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long eventtime = SystemClock.uptimeMillis();
                KeyEvent downEvent;
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.e(TAG, "privious ACTION_DOWN");
                        v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150L);
                        downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
                        mAudioManager.dispatchMediaKeyEvent(downEvent);
                        return true;
                    case MotionEvent.ACTION_UP:
                        Log.e(TAG, "privious ACTION_UP");
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150L);

                        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                        downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
                        mAudioManager.dispatchMediaKeyEvent(downEvent);

                        return true;
                }
                return false;
            }
        });


        AppCompatImageView next = (AppCompatImageView) overlayView.findViewById(R.id.next);

        next.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long eventtime = SystemClock.uptimeMillis();
                KeyEvent downEvent;

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.e(TAG, "next ACTION_DOWN");
                        v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150L);
                        downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT, 0);
                        mAudioManager.dispatchMediaKeyEvent(downEvent);
                        return true;
                    case MotionEvent.ACTION_UP:
                        Log.e(TAG, "next ACTION_UP");
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150L);

                        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                        downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT, 0);
                        mAudioManager.dispatchMediaKeyEvent(downEvent);

                        return true;
                }
                return false;
            }
        });

        music.setOnTouchListener(new View.OnTouchListener() {
            long eventtime = SystemClock.uptimeMillis();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event != null) {
                    Log.e(TAG, "v.getId() = " + v.getId());


                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            Log.e(TAG, "ACTION_DOWN");
                            v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150L);
                            return true;
                        case MotionEvent.ACTION_UP:
                            Log.e(TAG, "ACTION_UP");
                            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150L);
                            return true;
                    }
                }
                return false;
            }
        });
    }

    private void setShowPositionReceiver() {
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("islauncherforeground");

        registerReceiver(uiReceiver, intentFilter1);
    }

    private void setDataRelate() {
        RelativeLayout dataRelate = (RelativeLayout)overlayView.findViewById(R.id.data_relate);
        flightMode = (FlightModeActionView)overlayView.findViewById(R.id.flightmode);
        wifiAction = (WifiActionView) overlayView.findViewById(R.id.wifi);
        bluetoothAction = (BluetoothActionView) overlayView.findViewById(R.id.bluetooth);
        dataAction = (DataActionView) overlayView.findViewById(R.id.data);
        dataRelate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event != null) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            Log.e(TAG, "ACTION_DOWN");
                            v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150L);
                            return true;
                        case MotionEvent.ACTION_UP:
                            Log.e(TAG, "ACTION_UP");
                            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150L);
                            return true;
                    }
                }
                return false;
            }
        });
    }

    private void setOverlayView() {
        overlayView = (FloatLayout) ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.floating_view, null);
        overlayView.setVisibility(View.GONE);
    }

    private void setParams() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels);

        params = new WindowManager.LayoutParams(
                width,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        |WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        |WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                PixelFormat.OPAQUE);

        params.systemUiVisibility = params.systemUiVisibility
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.INVISIBLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

        params.gravity = Gravity.CENTER | Gravity.BOTTOM;
        params.x = 0;
        params.y = 20;
        params.windowAnimations = R.style.DropDownUp;
    }


    private void initVolumProgressbar() {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
        int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        volum_progressbar.setProgress((int)(current/(float)max*100));
    }

    private void initLightProgressbar() {
        ContentResolver contentResolver = getContentResolver();
        int brightness = 0;
        try{
            brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
            Log.e(TAG, "progress1 = " + brightness);
        }catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "progress = " + (int)(brightness/255.0*100));
        light_progressbar.setProgress((int)(brightness/255.0*100));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayView != null) {
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.removeView(overlayView);
            wm.removeView(overlayBottom);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
