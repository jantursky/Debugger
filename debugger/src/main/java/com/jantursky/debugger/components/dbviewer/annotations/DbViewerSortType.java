package com.jantursky.debugger.components.dbviewer.annotations;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef(value = {
        DbViewerSortType.A_Z, DbViewerSortType.Z_A, DbViewerSortType.DISABLED})
@Retention(RetentionPolicy.SOURCE)
public @interface DbViewerSortType {

    int DISABLED = -1;
    int A_Z = 0;
    int Z_A = 1;

}
