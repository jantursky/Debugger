package com.jantursky.debugger.dbviewer.db;

import android.database.Cursor;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CursorWrapper {

    public static final String SIMPLE_DATETIME_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";

    private final Cursor cursor;

    public Cursor getCursor() {
        return cursor;
    }

    public CursorWrapper(Cursor cursor) {
        this.cursor = cursor;
    }

    public int getInt(String columnName) {
        int columnIndex = this.cursor.getColumnIndexOrThrow(columnName);
        return this.cursor.getInt(columnIndex);
    }

    public Integer getIntNullable(String columnName) {
        int columnIndex = this.cursor.getColumnIndexOrThrow(columnName);
        if (this.cursor.isNull(columnIndex)) {
            return null;
        } else {
            return this.cursor.getInt(columnIndex);
        }
    }

    public int getInt(String columnName, int defaultValue) {
        int value = defaultValue;

        try {
            int columnIndex = this.cursor.getColumnIndex(columnName);
            if (columnIndex != -1 && !this.cursor.isNull(columnIndex)) {
                value = this.cursor.getInt(columnIndex);
            }
        } catch (Exception ignored) {
        }

        return value;
    }

    public int getInt(int columnIndex) {
        return this.cursor.getInt(columnIndex);
    }

    public Integer getIntNullable(int columnIndex) {
        if (this.cursor.isNull(columnIndex)) {
            return null;
        } else {
            return this.cursor.getInt(columnIndex);
        }
    }

    public short getShort(String columnName) {
        int columnIndex = this.cursor.getColumnIndexOrThrow(columnName);
        return this.cursor.getShort(columnIndex);
    }

    public Short getShortNullable(String columnName) {
        int columnIndex = this.cursor.getColumnIndexOrThrow(columnName);
        if (this.cursor.isNull(columnIndex)) {
            return null;
        } else {
            return this.cursor.getShort(columnIndex);
        }
    }

    public long getLong(String columnName) {
        int columnIndex = this.cursor.getColumnIndexOrThrow(columnName);
        return this.cursor.getLong(columnIndex);
    }

    public Long getLongNullable(String columnName) {
        int columnIndex = this.cursor.getColumnIndexOrThrow(columnName);
        if (this.cursor.isNull(columnIndex)) {
            return null;
        } else {
            return this.cursor.getLong(columnIndex);
        }
    }

    public long getLong(String columnName, long defaultValue) {
        long value = defaultValue;

        try {
            int columnIndex = this.cursor.getColumnIndex(columnName);
            if (columnIndex != -1 && !this.cursor.isNull(columnIndex)) {
                value = this.cursor.getLong(columnIndex);
            }
        } catch (Exception ignored) {
        }

        return value;
    }


    public String getString(int index) {
        return this.cursor.getString(index);
    }

    public String getString(String columnName) {
        int columnIndex = this.cursor.getColumnIndexOrThrow(columnName);
        return this.cursor.getString(columnIndex);
    }

    public String getString(String columnName, String defaultValue) {
        String value = defaultValue;

        try {
            int columnIndex = this.cursor.getColumnIndex(columnName);
            if (columnIndex != -1 && !this.cursor.isNull(columnIndex)) {
                value = this.cursor.getString(columnIndex);
            }
        } catch (Exception ignored) {
        }

        return value;
    }

    public float getFloat(String columnName) {
        int columnIndex = this.cursor.getColumnIndexOrThrow(columnName);
        return this.cursor.getFloat(columnIndex);
    }

    public Float getFloatNullable(String columnName) {
        int columnIndex = this.cursor.getColumnIndexOrThrow(columnName);
        if (this.cursor.isNull(columnIndex)) {
            return null;
        } else {
            return this.cursor.getFloat(columnIndex);
        }
    }

    public double getDouble(String columnName) {
        int columnIndex = this.cursor.getColumnIndexOrThrow(columnName);
        return this.cursor.getDouble(columnIndex);
    }

    public Double getDoubleNullable(String columnName) {
        int columnIndex = this.cursor.getColumnIndexOrThrow(columnName);
        if (this.cursor.isNull(columnIndex)) {
            return null;
        } else {
            return this.cursor.getDouble(columnIndex);
        }
    }

    public double getDouble(String columnName, double defaultValue) {
        double value = defaultValue;

        try {
            int columnIndex = this.cursor.getColumnIndex(columnName);
            if (columnIndex != -1 && !this.cursor.isNull(columnIndex)) {
                value = this.cursor.getDouble(columnIndex);
            }
        } catch (Exception ignored) {
        }

        return value;
    }

    public boolean getBoolean(String columnName) {
        int columnIndex = this.cursor.getColumnIndexOrThrow(columnName);
        return (this.cursor.getInt(columnIndex) != 0);
    }

    public Date getDate(String columnName) {
        return parseDbDateTime(getString(columnName));
    }

    public boolean moveToNext() {
        return this.cursor.moveToNext();
    }

    public boolean moveToFirst() {
        return this.cursor.moveToFirst();
    }

    public void close() {
        this.cursor.close();
    }

    public int getCount() {
        return this.cursor.getCount();
    }

    public static Date parseDbDateTime(String value) {
        return parseDbDateTime(value, SIMPLE_DATETIME_FORMAT_STRING);
    }

    public static Date parseDbDateTime(String value, String format) {
        if (!TextUtils.isEmpty(value) && !value.toLowerCase().equals("null")) {
            try {
                SimpleDateFormat parser = new SimpleDateFormat(format, Locale.US);
                return parser.parse(value);
            } catch (ParseException e) {
//                Crashlytics.logException(e);
//				Log.e(TAG, "### Failed to parse " + value + " for format " + format);
            }
        }

        return null;
    }
}
