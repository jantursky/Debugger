package com.jantursky.debugger.components.dbviewer.annotations;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef(value = {
        DbViewerRowType.HEADER, DbViewerRowType.ROW, DbViewerRowType.HEADER_EMPTY, DbViewerRowType.ROW_POSITION})
@Retention(RetentionPolicy.SOURCE)
public @interface DbViewerRowType {

    int HEADER = 0;
    int HEADER_EMPTY = 1;
    int ROW = 2;
    int ROW_POSITION = 3;

}
