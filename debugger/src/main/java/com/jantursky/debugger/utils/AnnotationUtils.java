package com.jantursky.debugger.utils;

import com.jantursky.debugger.R;
import com.jantursky.debugger.annotations.DebuggerComponentType;

public class AnnotationUtils {

    public static int getTextIdForComponentType(@DebuggerComponentType int componentType) {
        if (componentType == DebuggerComponentType.DB_VIEWER) {
            return R.string.component_type_db_viewer;
        } else if (componentType == DebuggerComponentType.SHARED_PREFERENCES_VIEWER) {
            return R.string.component_type_shared_preferences_viewer;
        } else if (componentType == DebuggerComponentType.REST_CLIENT) {
            return R.string.component_type_rest_client;
        }
        return 0;
    }

    public static int getTextColorFromResponseCode(int code) {
        if (code >= 100 && code < 200) {
            return R.color.yellow;
        } else if (code >= 200 && code < 300) {
            return R.color.green;
        } else if (code >= 300) {
            return R.color.red;
        }
        return R.color.red;
    }

}

// eof
