package com.jantursky.debugger.dbviewer.models;

import android.database.Cursor;

import com.jantursky.debugger.utils.JsonUtils;

public class DbViewerDataModel {

    public String dbKey;
    public int type;
    public String typeText;
    public boolean isHidden;
    public boolean isHeader;

    public int valueInt;
    public float valueFloat;
    public String valueString;

    public DbViewerDataModel(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public DbViewerDataModel(String dbKey, String name, String type, boolean isHeader) {
        this.dbKey = dbKey;
        this.valueString = name;
        this.typeText = type;
        this.isHeader = isHeader;
    }

    public DbViewerDataModel(String dbKey, int type, int valueInt) {
        this.dbKey = dbKey;
        this.type = type;
        this.valueInt = valueInt;
    }

    public DbViewerDataModel(String dbKey, int type, float valueFloat) {
        this.dbKey = dbKey;
        this.type = type;
        this.valueFloat = valueFloat;
    }

    public DbViewerDataModel(String dbKey, int type, String valueString) {
        this.dbKey = dbKey;
        this.type = type;
        this.valueString = valueString;
    }

    @Override
    public String toString() {
        return "DbViewerDataModel{" +
                "type=" + type +
                ", typeText='" + typeText + '\'' +
                ", isHeader=" + isHeader +
                ", valueInt=" + valueInt +
                ", valueFloat=" + valueFloat +
                ", valueString='" + valueString + '\'' +
                '}';
    }

    public String getText() {
        if (isHeader) {
            return valueString;
        } else {
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
    }

    public String getCleanedText() {
        return JsonUtils.getIndentedText(getText());
    }

    public String geType() {
        /*if (type == Cursor.FIELD_TYPE_STRING) {
            return "STRING";
        } else if (type == Cursor.FIELD_TYPE_INTEGER) {
            return "INTEGER";
        } else if (type == Cursor.FIELD_TYPE_FLOAT) {
            return "FLOAT";
        } else if (type == Cursor.FIELD_TYPE_BLOB) {
            return "BLOB";
        }
        return "UNKNOWN";*/
        return typeText;
    }
}
