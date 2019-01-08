package com.vsun.controlcenter.views;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vsun.controlcenter.R;
import com.vsun.controlcenter.utils.AppInfos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ubuntu on 17-10-20.
 */

public class BottomButtonActionView extends RelativeLayout{
    public String TAG = "BottomButtonActionView";
    private Context context;
    private SharedPreferences settings;
    private String name;
    private boolean longPressed = false;
    private View bottomView;
    private List<String> filterList = new ArrayList<>();

    public BottomButtonActionView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public BottomButtonActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public BottomButtonActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        settings = context.getSharedPreferences("bottom_button_action", 0);
        filterList.add("com.vsun.controlcenter");
        filterList.add("com.vsun.launcher");
    }
    public void setBottomView(View v) {
        Log.e(TAG, "bottomView = " + v);
        this.bottomView = v;
    }

    public void setShortcut(String name) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(BottomButtonActionView.this.getId()+"", name);
        editor.commit();
    }

    public void setImage() {
        PackageManager mPackageManager = context.getPackageManager();
        Log.e(TAG, "id = " + BottomButtonActionView.this.getId());

        name = settings.getString(BottomButtonActionView.this.getId()+"", "");
        Log.e(TAG, "name = " + name);
        if (name == null || name == "") {
            Log.e(TAG, name);
            BottomButtonActionView.this.getChildAt(0).setBackgroundResource(R.mipmap.control_add);
        }else {
            try{
                if ("com.android.deskclock".equals(name)) {
                    BottomButtonActionView.this.getChildAt(0).setBackgroundResource(R.mipmap.control_alarm_clock_normal);
                } else if ("com.android.calculator2".equals(name)) {
                    BottomButtonActionView.this.getChildAt(0).setBackgroundResource(R.mipmap.control_calculator_normal);
                } else if ("com.mediatek.camera".equals(name)) {
                    BottomButtonActionView.this.getChildAt(0).setBackgroundResource(R.mipmap.control_camera_normal);
                } else if ("com.android.fmradio".equals(name)){
                    BottomButtonActionView.this.getChildAt(0).setBackgroundResource(R.mipmap.control_fm_normal);
                } else {
                    BottomButtonActionView.this.getChildAt(0).setBackground(mPackageManager.getApplicationIcon(name));
                }
            } catch (Exception e ) {
                Log.e(TAG, e+"");
            }
        }
    }


    Handler mHandler = new Handler();
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent");
        int startX = 0, startY = 0;
        long startTime;
        int PRESSREANGE = 500;
        int CLICKRANGE = 100;

        ImageView child = (ImageView)BottomButtonActionView.this.getChildAt(0);


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                startX = (int) event.getX();
                startY = (int) event.getY();
                startTime = event.getDownTime();

                    /* 长按操作 */
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        longPressedAlertDialog();
                    }
                }, PRESSREANGE);
                child.animate().scaleX(1.2f).scaleY(1.2f).setDuration(150L);
                break;

            case MotionEvent.ACTION_MOVE:
                int lastX = (int) event.getX();
                int lastY = (int) event.getY();

                if (Math.abs(lastX - startX) > CLICKRANGE || Math.abs(lastY - startY) > CLICKRANGE) {
                    Log.e(TAG, "remove");
                    mHandler.removeCallbacksAndMessages(null);
                }
                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacksAndMessages(null);
                Log.e(TAG, "Action_up remove");
                //Toast.makeText(context, "Action up remove", Toast.LENGTH_LONG).show();
                child.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150L);
                if(name != null && name != "" && !longPressed) {
                    Log.e(TAG, "name = " + name + " ACTION_UP");

                    PackageManager packageManager = context.getPackageManager();
                    Intent intent =packageManager.getLaunchIntentForPackage(name);
                    if(intent==null){
                        Log.e(TAG, "App not found");
                    }
                    context.startActivity(intent);
                    BottomButtonActionView.this.getRootView().setVisibility(GONE);
                    Log.e(TAG, "ACTION UP bottomView = " + bottomView);
                    bottomView.setVisibility(VISIBLE);
                }

                if (name == null || name == "") {
                    longPressedAlertDialog();
                }


                longPressed = false;

                break;
        }
        return true;

    }

    class AppInfoListAdapter extends BaseAdapter{
        private List<AppInfos> appInfoses;
        private LayoutInflater inflater;

        public AppInfoListAdapter(List<AppInfos> data) {
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.appInfoses = data;
        }

        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return appInfoses.size();
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public Object getItem(int position) {
            return position;
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either
         * create a View manually or inflate it from an XML layout file. When the View is inflated, the
         * parent View (GridView, ListView...) will apply default layout parameters unless you use
         * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
         * to specify a root view and to prevent attachment to the root.
         *
         * @param position    The position of the item within the adapter's data set of the item whose view
         *                    we want.
         * @param convertView The old view to reuse, if possible. Note: You should check that this view
         *                    is non-null and of an appropriate type before using. If it is not possible to convert
         *                    this view to display the correct data, this method can create a new view.
         *                    Heterogeneous lists can specify their number of view types, so that this View is
         *                    always of the right type (see {@link #getViewTypeCount()} and
         *                    {@link #getItemViewType(int)}).
         * @param parent      The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View appView = this.inflater.inflate(R.layout.select_dialog_siglechoice, null);
            AppInfos appInfos = appInfoses.get(position);

            ImageView icon = (ImageView) appView.findViewById(R.id.icon);
            CheckedTextView name = (CheckedTextView)appView.findViewById(R.id.text1);

            icon.setImageDrawable(appInfos.getAppIcon());
            name.setText(appInfos.getAppName());
            return appView;
        }
    }

    public void longPressedAlertDialog() {
        Log.e(TAG, "longPressedAlertDialog");

        longPressed = true;
        List<AppInfos> appInfosesList = new ArrayList<>();

        PackageManager mPackageManager = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN", null);
        intent.addCategory("android.intent.category.LAUNCHER");
        final List<ResolveInfo> pInfos = mPackageManager.queryIntentActivities(intent, 0);

        for (ResolveInfo i : pInfos) {
            AppInfos appInfos = new AppInfos();

            String lable = i.loadLabel(mPackageManager).toString();
            Drawable icon = i.loadIcon(mPackageManager);
            name = i.activityInfo.packageName;
            if(filterList.contains(name)) {
                continue;
            }

            appInfos.setAppIcon(icon);
            appInfos.setAppName(lable);
            appInfos.setPackageName(name);

            appInfosesList.add(appInfos);
        }

        final AppInfoListAdapter arrayAdapter = new AppInfoListAdapter(appInfosesList);


        AlertDialog builderSingle = new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.btn_plus)
                .setTitle("Select One App")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(context, BottomButtonActionView.this.getId()+"", Toast.LENGTH_LONG).show();
                        BottomButtonActionView.this.getChildAt(0).setBackground(appInfosesList.get(which).getAppIcon());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(BottomButtonActionView.this.getId()+"", appInfosesList.get(which).getPackageName());
                        name = appInfosesList.get(which).getPackageName();
                        editor.commit();
                        setImage();
                    }
                })
                .create();

        setImage();
        builderSingle.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        builderSingle.show();
    }
}
