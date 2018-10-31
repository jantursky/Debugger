package com.jantursky.debugger.annotations;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef(value = {
        DebuggerComponentType.DB_VIEWER, DebuggerComponentType.SHARED_PREFERENCES_VIEWER, DebuggerComponentType.REST_CLIENT})
@Retention(RetentionPolicy.SOURCE)
public @interface DebuggerComponentType {

    int DB_VIEWER = 0;
    int SHARED_PREFERENCES_VIEWER = 1;
    int REST_CLIENT = 2;

}
