package com.jantursky.debugger.components.sharedpreferencesviewer.annotations;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef(value = {
        SharedPreferencesViewerScreenType.List, SharedPreferencesViewerScreenType.Data})
@Retention(RetentionPolicy.SOURCE)
public @interface SharedPreferencesViewerScreenType {

    int List = 0;
    int Data = 1;

}
