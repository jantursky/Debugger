package com.jantursky.debugger.dbviewer.annotations;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef(value = {
        DbViewerScreenType.Databases, DbViewerScreenType.Tables, DbViewerScreenType.Data})
@Retention(RetentionPolicy.SOURCE)
public @interface DbViewerScreenType {

    int Databases = 0;
    int Tables = 1;
    int Data = 2;

}
