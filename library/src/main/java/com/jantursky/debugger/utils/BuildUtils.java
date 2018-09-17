package com.jantursky.debugger.utils;

import android.os.Build;

public class BuildUtils {

    public static boolean equalOrHigher(int minVersion) {
        return Build.VERSION.SDK_INT >= minVersion;
    }

}

// eof
