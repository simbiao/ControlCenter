package com.vsun.controlcenter.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ubuntu on 17-12-4.
 */

public class Utils {
    private static String TAG = "Utils";
    public static ActivityManager getActivityManager(Context context){
        return (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

    }

    public static List<String> getLaunchers(Context context){
        List<String> packageNames = new ArrayList<String>();

        List<String> filterList = new ArrayList<>();
        filterList.add("com.android.settings");

        List<String> whiteList = new ArrayList<>();
        whiteList.add("com.mobiistar.launcher");
        whiteList.add("com.vsun.launcher");
        whiteList.add("com.android.launcher3");


        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for(ResolveInfo resolveInfo:resolveInfos){
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if(activityInfo != null) {
                if(whiteList.contains(resolveInfo.activityInfo.packageName)){
                    Log.e(TAG, resolveInfo.activityInfo.packageName);
                    packageNames.add(resolveInfo.activityInfo.processName);
                    packageNames.add(resolveInfo.activityInfo.packageName);
                }
            }
        }
        return packageNames;
    }

    public static boolean isLauncherForeground(Context context){
        boolean isLauncherForeground = false;
        ActivityManager activityManager = getActivityManager(context);
        List<String> lanuchers = getLaunchers(context);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos =  activityManager.getRunningTasks(1);

        if(lanuchers.contains(runningTaskInfos.get(0).baseActivity.getPackageName())) {
            isLauncherForeground = true;
        }

        return isLauncherForeground;
    }
}
