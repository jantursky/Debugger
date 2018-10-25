package com.jantursky.debugger.dbviewer.models;

import com.jantursky.debugger.dbviewer.annotations.DbViewerRowType;
import com.jantursky.debugger.utils.JsonUtils;

public class DbViewerColumnModel extends DbViewerDataModel {

    public String columnName;
    public String typeText;
    public boolean isNotNull;
    public boolean isPrimaryKey;
    public String defaultValue;

    public DbViewerColumnModel(int columnPos, @DbViewerRowType int rowType) {
        super(rowType);
        this.columnPos = columnPos;
    }

    public DbViewerColumnModel(int columnPos, String dbKey, String columnName, String typeText,
                               boolean isNotNull, boolean isPrimaryKey, String defaultValue,
                               @DbViewerRowType int rowType) {
        super(rowType);
        this.columnPos = columnPos;
        this.dbKey = dbKey;
        this.columnName = columnName;
        this.typeText = typeText;
        this.isNotNull = isNotNull;
        this.isPrimaryKey = isPrimaryKey;
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "DbViewerColumnModel{" +
                "columnPos=" + columnPos +
                ", dbKey='" + dbKey + '\'' +
                ", columnName='" + columnName + '\'' +
                ", typeText='" + typeText + '\'' +
                ", isNotNull=" + isNotNull +
                ", isPrimaryKey=" + isPrimaryKey +
                ", defaultValue='" + defaultValue + '\'' +
                ", rowType=" + rowType +
                '}';
    }

    public String getText() {
        if (rowType == DbViewerRowType.HEADER) {
            return columnName;
        }
        return "";
    }

    public String getCleanedText() {
        return JsonUtils.getIndentedText(getText());
    }
}
