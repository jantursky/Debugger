package com.jantursky.debugger.dbviewer.models;

import android.text.TextUtils;

import java.io.File;

public class DbViewerModel {

    public boolean jumpToRoot;
    public File table;
    public String column;

    public DbViewerModel(boolean jumpToRoot) {
        this.jumpToRoot = jumpToRoot;
    }

    public DbViewerModel(File table) {
        this.table = table;
    }

    public DbViewerModel(String column) {
        this.column = column;
    }

    public boolean isJumpToRootType() {
        return jumpToRoot;
    }

    public boolean isTableType() {
        return table != null;
    }

    public boolean isColumnType() {
        return !TextUtils.isEmpty(column);
    }

    public String getTableName() {
        return table.getName();
    }

    public String getColumnName() {
        return column;
    }
}
