package com.jantursky.debugger.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class ScreenUtils {

    public static int[] getScreenDimens(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int[] coords = new int[2];
        if (BuildUtils.equalOrHigher(Build.VERSION_CODES.HONEYCOMB_MR2)) {
            coords[0] = display.getWidth();
            coords[1] = display.getHeight();
        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);
            coords[0] = metrics.widthPixels;
            coords[1] = metrics.heightPixels;
        }
        return coords;
    }

    protected static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}