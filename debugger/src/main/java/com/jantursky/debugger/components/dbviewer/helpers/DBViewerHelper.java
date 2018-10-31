package com.jantursky.debugger.components.dbviewer.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.jantursky.debugger.components.dbviewer.annotations.DbViewerRowType;
import com.jantursky.debugger.components.dbviewer.db.CursorWrapper;
import com.jantursky.debugger.components.dbviewer.models.DbViewerColumnModel;
import com.jantursky.debugger.components.dbviewer.models.DbViewerDataModel;
import com.jantursky.debugger.components.dbviewer.models.DbViewerRowModel;
import com.jantursky.debugger.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DBViewerHelper {

    private static final String TAG = DBViewerHelper.class.getSimpleName();
    private static DBViewerHelper instance;
    private DatabaseHelper databaseHelper;

    /*public static final String DATABASE_NAME = "kitchen-app.db";*/
    private SQLiteDatabase db;

    private DBViewerHelper(Context context, File dbFile) {
        initDatabaseHelper(context, dbFile);
        initSQLiteDatabase();
    }

    public static void resetInstance() {
        instance = null;
    }

    public static DBViewerHelper getInstance(Context context, File file) {
        if (instance == null) {
            synchronized (DBViewerHelper.class) {
                if (instance == null) {
                    instance = new DBViewerHelper(context, file);
                }
            }
        }
        return instance;
    }

    public static String[] getArgs(Object... args) {
        String[] resultArgs = null;
        if (args != null && args.length > 0) {
            List<String> argsList = new ArrayList<>();

            for (Object argument : args) {
                if (argument != null) {
                    String argumentString = argument.toString();
                    if (!argumentString.equals("")) {
                        argsList.add(argumentString);
                    }
                }
            }

            resultArgs = argsList.toArray(new String[argsList.size()]);
        }

        return resultArgs;
    }

    private void initDatabaseHelper(Context context, File file) {
        Log.w(TAG, "##### INIT " + file.getPath());
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        int version = db.getVersion();
        db.close();

        this.databaseHelper = new DatabaseHelper(context, file.getPath(), version);
    }

    private void initSQLiteDatabase() {
        try {
            this.db = this.databaseHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.e(TAG, "### Failed to init DB " + e.getMessage());
        }
    }

    public ArrayList<String> getAllTables() {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        ArrayList<String> tables = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                tables.add(cursor.getString(0));
            }
            cursor.close();
        }
        Collections.sort(tables);
        return tables;
    }

    public String[] getAllColumns(String table) {
        Cursor cursor = db.query(table, null, null, null, null, null, null);

        String[] names = null;
        if (cursor != null) {
            names = cursor.getColumnNames();
            cursor.close();
        }
        return names;
    }

    public String[] getCustomColumns(String query) {
        Cursor cursor = db.rawQuery(query, null);

        String[] names = null;
        if (cursor != null) {
            names = cursor.getColumnNames();
            cursor.close();
        }
        return names;
    }

    public ArrayList<DbViewerDataModel> getColumnsType(String table) {
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null);

        ArrayList<DbViewerDataModel> headerRow = new ArrayList<>();
        if (cursor != null) {
            int columnPos = 0;
            headerRow.add(new DbViewerColumnModel(columnPos++, DbViewerRowType.HEADER_EMPTY));
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                boolean isNotNull = cursor.getInt(cursor.getColumnIndex("notnull")) == 1;
                boolean isPrimaryKey = cursor.getInt(cursor.getColumnIndex("pk")) == 1;
                String defaultValue = cursor.getString(cursor.getColumnIndex("dflt_value"));
                headerRow.add(new DbViewerColumnModel(columnPos++, name, name, type, isNotNull, isPrimaryKey, defaultValue, DbViewerRowType.HEADER));
            }
            cursor.close();
        }
        return headerRow;
    }

    public ArrayList<ArrayList<DbViewerDataModel>> getDataForTable(String table, String[] columns, String primaryColumnName) {
        return getData("SELECT * FROM " + table, columns, primaryColumnName);
    }

    public ArrayList<ArrayList<DbViewerDataModel>> getDataForQuery(String query, String[] columns, String primaryColumnName) {
        return getData(query, columns, primaryColumnName);
    }

    public ArrayList<ArrayList<DbViewerDataModel>> getData(String query, String[] columns, String primaryColumnName) {
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<ArrayList<DbViewerDataModel>> arrayList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ArrayList<DbViewerDataModel> arrayRow = new ArrayList<>();

                int columnPos = 0;
                arrayRow.add(new DbViewerRowModel(columnPos++, DbViewerRowType.ROW_POSITION));
                String primaryColumnValue = null;
                if (!StringUtils.isEmpty(primaryColumnName)) {
                    for (String column : columns) {
                        if (column.equals(primaryColumnName)) {
                            int index = cursor.getColumnIndex(column);
                            int type = cursor.getType(index);
                            if (type == Cursor.FIELD_TYPE_NULL) {

                            } else if (type == Cursor.FIELD_TYPE_STRING) {
                                primaryColumnValue = cursor.getString(index);
                            } else if (type == Cursor.FIELD_TYPE_FLOAT) {
                                primaryColumnValue = String.valueOf(cursor.getFloat(index));
                            } else if (type == Cursor.FIELD_TYPE_INTEGER) {
                                primaryColumnValue = String.valueOf(cursor.getInt(index));
                            } else if (type == Cursor.FIELD_TYPE_BLOB) {

                            }
                        }
                    }
                }

                for (String column : columns) {
                    int index = cursor.getColumnIndex(column);
                    int type = cursor.getType(index);
                    if (type == Cursor.FIELD_TYPE_NULL) {
                        arrayRow.add(new DbViewerRowModel(columnPos++, column, type, DbViewerRowType.ROW, "NULL", primaryColumnName, primaryColumnValue));
                    } else if (type == Cursor.FIELD_TYPE_STRING) {
                        arrayRow.add(new DbViewerRowModel(columnPos++, column, type, DbViewerRowType.ROW, cursor.getString(index), primaryColumnName, primaryColumnValue));
                    } else if (type == Cursor.FIELD_TYPE_FLOAT) {
                        arrayRow.add(new DbViewerRowModel(columnPos++, column, type, DbViewerRowType.ROW, cursor.getFloat(index), primaryColumnName, primaryColumnValue));
                    } else if (type == Cursor.FIELD_TYPE_INTEGER) {
                        arrayRow.add(new DbViewerRowModel(columnPos++, column, type, DbViewerRowType.ROW, cursor.getInt(index), primaryColumnName, primaryColumnValue));
                    } else if (type == Cursor.FIELD_TYPE_BLOB) {
                        arrayRow.add(new DbViewerRowModel(columnPos++, column, type, DbViewerRowType.ROW, "BLOB", primaryColumnName, primaryColumnValue));
                    }
                }
                arrayList.add(arrayRow);
            }
            cursor.close();
        }
        return arrayList;
    }

    public int getCountForTable(String tableName) {
        return getCount("SELECT COUNT(*) AS cnt FROM " + tableName);
    }

    public int getCountForQuery(String query) {
        return getCount(query);
    }

    public int getCount(String query) {
        int count = -1;
        CursorWrapper cursor = this.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    /**
     * close the db
     * return type: void
     */
    public void close() {
        if (this.db != null) {
            this.db.close();
            this.db = null;
        }

        if (this.databaseHelper != null) {
            this.databaseHelper.close();
            this.databaseHelper = null;
        }
    }

    public void execSql(String sql) {
        this.db.execSQL(sql);
    }

    public CursorWrapper rawQuery(String sql, String[] sqlArgs) {
        CursorWrapper cursorWrapper = null;

        try {
            Cursor cursor = this.db.rawQuery(sql, sqlArgs);
            cursorWrapper = new CursorWrapper(cursor);
        } catch (Exception ex) {
            Log.w("Query error", ex);
        }

        return cursorWrapper;
    }

    public String queryValueAsString(String sql, String[] sqlArgs) {
        String val = "";

        try {
            Cursor cursor = this.db.rawQuery(sql, sqlArgs);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    val = cursor.getString(0);
                }

                cursor.close();
            }
        } catch (Exception ex) {
            Log.w("Query error", ex);
        }

        return val;
    }

    public int queryValueAsInt(String sql, String[] sqlArgs) {
        int val = -1;

        try {
            Cursor cursor = this.db.rawQuery(sql, sqlArgs);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    val = cursor.getInt(0);
                }

                cursor.close();
            }
        } catch (Exception ex) {
            Log.w("Query error", ex);
        }

        return val;
    }

    public float queryValueAsFloat(String sql, String[] sqlArgs) {
        float val = -1;

        try {
            Cursor cursor = this.db.rawQuery(sql, sqlArgs);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    val = cursor.getFloat(0);
                }

                cursor.close();
            }
        } catch (Exception ex) {
            Log.w("Query error", ex);
        }

        return val;
    }

    public List<String> queryStringList(String sql, String[] sqlArgs) {
        List<String> list = new ArrayList<>();

        try {
            Cursor cursor = this.db.rawQuery(sql, sqlArgs);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    list.add(cursor.getString(0));
                }

                cursor.close();
            }
        } catch (Exception ex) {
            Log.w("Query error", ex);
        }

        return list;
    }

    public CursorWrapper query(String table, String[] columns) {
        return query(false, table, columns, null, null, null, null, null, null);
    }

    public CursorWrapper query(String table, String[] columns, String selection, String[] selectionArgs) {
        return query(false, table, columns, selection, selectionArgs, null, null, null, null);
    }

    public CursorWrapper query(boolean distinct, String table, String[] columns, String selection,
                               String[] selectionArgs, String groupBy, String having, String orderBy,
                               String limit) {
        CursorWrapper cursorWrapper = null;

        try {
            Cursor cursor = this.db.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            cursorWrapper = new CursorWrapper(cursor);
        } catch (Exception ex) {
            Log.w("Query error " + table, ex);
        }

        return cursorWrapper;
    }

    public int insert(String table, ContentValues values) {
        int rowId = -1;

        try {
            rowId = (int) this.db.insert(table, null, values);
        } catch (Exception ex) {
            Log.w("Insert error " + table, ex);
        }

        return rowId;
    }

    public int update(String table, ContentValues values, String query, String[] queryArgs) {
        int affectedRows = 0;

        try {
            Log.w(TAG, "##### Update table: " + table + ", affected: " + affectedRows + ", values: " + ((values != null) ? values.size() : "IS NULL") + ", query: " + query + ", args " + ((queryArgs != null) ? Arrays.toString(queryArgs) : "IS NULL"));
            affectedRows = this.db.update(table, values, query, queryArgs);
        } catch (Exception ex) {
            Log.w("Update error " + table, ex);
        }

        return affectedRows;
    }

    public int delete(String table, String query) {
        try {
            return this.db.delete(table, query, null);
        } catch (Exception ex) {
            Log.w("Delete error " + table, ex);
        }

        return 0;
    }

    public int delete(String table, String query, String[] queryArgs) {
        try {
            return this.db.delete(table, query, queryArgs);
        } catch (Exception ex) {
            Log.w("Delete error " + table, ex);
        }

        return 0;
    }

    public void beginTransaction() {
        this.db.beginTransactionNonExclusive();
    }

    public void setTransactionSuccessful() {
        this.db.setTransactionSuccessful();
    }

    public void endTransaction() {
        this.db.endTransaction();
    }

    public void endTransactionSuccessful() {
        setTransactionSuccessful();
        endTransaction();
    }

    public String getSchema(String table) {
        Cursor cursor = db.rawQuery("SELECT sql FROM sqlite_master WHERE name='" + table + "';", null);

        String schema = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                schema = cursor.getString(0);
            }
            cursor.close();
        }
        return schema;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context, String dbFileName, int dbVersion) {
            super(context, dbFileName, null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {

        }
    }
}
