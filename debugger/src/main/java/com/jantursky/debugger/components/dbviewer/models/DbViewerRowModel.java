package com.jantursky.debugger.components.dbviewer.models;

import android.database.Cursor;

import com.jantursky.debugger.components.dbviewer.annotations.DbViewerRowType;
import com.jantursky.debugger.utils.JsonUtils;

public class DbViewerRowModel extends DbViewerDataModel {

    public int type;
    public String typeText;

    public int valueInt;
    public float valueFloat;
    public String valueString;

    public String primaryColumnName;
    public String primaryColumnValue;

    public DbViewerRowModel(int columnPos, @DbViewerRowType int rowType) {
        super(rowType);
        this.columnPos = columnPos;
    }

    public DbViewerRowModel(int columnPos, boolean isHidden, @DbViewerRowType int rowType, String primaryColumnName, String primaryColumnValue) {
        super(rowType);
        this.primaryColumnName = primaryColumnName;
        this.primaryColumnValue = primaryColumnValue;
        this.columnPos = columnPos;
        this.isHidden = isHidden;
    }

    public DbViewerRowModel(int columnPos, String dbKey, String name, String type, @DbViewerRowType int rowType, String primaryColumnName, String primaryColumnValue) {
        super(rowType);
        this.primaryColumnName = primaryColumnName;
        this.primaryColumnValue = primaryColumnValue;
        this.columnPos = columnPos;
        this.dbKey = dbKey;
        this.valueString = name;
        this.typeText = type;
    }

    public DbViewerRowModel(int columnPos, String dbKey, int type, @DbViewerRowType int rowType, int valueInt, String primaryColumnName, String primaryColumnValue) {
        super(rowType);
        this.primaryColumnName = primaryColumnName;
        this.primaryColumnValue = primaryColumnValue;
        this.columnPos = columnPos;
        this.dbKey = dbKey;
        this.type = type;
        this.valueInt = valueInt;
    }

    public DbViewerRowModel(int columnPos, String dbKey, int type, @DbViewerRowType int rowType, float valueFloat, String primaryColumnName, String primaryColumnValue) {
        super(rowType);
        this.primaryColumnName = primaryColumnName;
        this.primaryColumnValue = primaryColumnValue;
        this.columnPos = columnPos;
        this.dbKey = dbKey;
        this.type = type;
        this.valueFloat = valueFloat;
    }

    public DbViewerRowModel(int columnPos, String dbKey, int type, @DbViewerRowType int rowType, String valueString, String primaryColumnName, String primaryColumnValue) {
        super(rowType);
        this.primaryColumnName = primaryColumnName;
        this.primaryColumnValue = primaryColumnValue;
        this.columnPos = columnPos;
        this.dbKey = dbKey;
        this.type = type;
        this.valueString = valueString;
    }

    @Override
    public String toString() {
        return "DbViewerRowModel{" +
                "type=" + type +
                ", typeText='" + typeText + '\'' +
                ", valueInt=" + valueInt +
                ", valueFloat=" + valueFloat +
                ", valueString='" + valueString + '\'' +
                ", primaryColumnName='" + primaryColumnName + '\'' +
                ", primaryColumnValue='" + primaryColumnValue + '\'' +
                ", rowType=" + rowType +
                ", columnPos=" + columnPos +
                ", isHidden=" + isHidden +
                ", dbKey='" + dbKey + '\'' +
                '}';
    }

    @Override
    public String getText() {
        if (rowType == DbViewerRowType.HEADER) {
            return valueString;
        } else if (rowType == DbViewerRowType.ROW) {
            if (type == Cursor.FIELD_TYPE_STRING) {
                return valueString;
            } else if (type == Cursor.FIELD_TYPE_INTEGER) {
                return String.valueOf(valueInt);
            } else if (type == Cursor.FIELD_TYPE_FLOAT) {
                return String.valueOf(valueFloat);
            } else if (type == Cursor.FIELD_TYPE_BLOB) {
                return valueString;
            }
            return "";
        }
        return "";
    }

    public String getCleanedText() {
        return JsonUtils.getIndentedText(getText());
    }

    @Override
    public String geType() {
        return typeText;
    }
}
