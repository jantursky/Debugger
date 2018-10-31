package com.jantursky.debugger.components.dbviewer.models;

import com.jantursky.debugger.components.dbviewer.annotations.DbViewerRowType;

public class DbViewerDataModel {

    @DbViewerRowType
    public final int rowType;
    public int columnPos;
    public boolean isHidden;
    public String dbKey;

    public DbViewerDataModel(@DbViewerRowType int rowType) {
        this.rowType = rowType;
    }

    public boolean isEmptyHeader() {
        return rowType == DbViewerRowType.HEADER_EMPTY;
    }

    public boolean isHeader() {
        return rowType == DbViewerRowType.HEADER;
    }

    public boolean isRow() {
        return rowType == DbViewerRowType.ROW;
    }

    public boolean isRowPosition() {
        return rowType == DbViewerRowType.ROW_POSITION;
    }

    public String getText() {
        return "";
    }

    public String geType() {
        return "";
    }
}
