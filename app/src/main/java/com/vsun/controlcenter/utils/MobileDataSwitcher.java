package com.vsun.controlcenter.utils;

/**
 * Created by ubuntu on 17-11-10.
 */

import java.lang.reflect.Method;
import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MobileDataSwitcher {

    private static String TAG = "MobileDataSwitcher";

    public static void setMobileDataState(Context context, boolean mobileDataEnabled)
    {
        try
        {
            TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method setMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            if (null!= setMobileDataEnabledMethod)
            {
                setMobileDataEnabledMethod.invoke(telephonyService, mobileDataEnabled);
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG,"Error setting mobile data state", ex);
        }
    }
    public static boolean getMobileDataState(Context context)
    {
        try
        {
            TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method getMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("getDataEnabled");
            if (null!= getMobileDataEnabledMethod)
            {
                boolean mobileDataEnabled = (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);
                return mobileDataEnabled;
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG,"Error getting mobile data state", ex);
        }
        return false;
    }
}
