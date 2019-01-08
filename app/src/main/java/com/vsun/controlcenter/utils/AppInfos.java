package com.vsun.controlcenter.utils;

import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by ubuntu on 17-11-23.
 */

public class AppInfos {
    public String TAG = "AppInfos";
    public String appName="";
    public String packageName="";
    public String versionName="";
    public int versionCode=0;
    public Drawable appIcon;


    public void print()
    {
        Log.v(TAG, "Name:" + appName + " Package:" + packageName);
        Log.v(TAG, "Name:" + appName + " versionName:" + versionName);
        Log.v(TAG, "Name:" + appName + " versionCode:" + versionCode);
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }


    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
}
