package com.patr.radix.dal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * 公共表名
     */
    public static final String DATABASE_NAME = "radix.db";

    /**
     * 数据表版本
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * 
     */
    private static volatile DatabaseHelper instance = null;

    private DatabaseHelper(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context,
            String databaseName) {
        if (instance == null) {
            instance = new DatabaseHelper(context, databaseName);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建CacheRec表
        db.execSQL("CREATE TABLE IF NOT EXISTS CacheRec ("
                + "textUrl TEXT PRIMARY KEY NOT NULL UNIQUE,"
                + "textContent TEXT,mTime TEXT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion != oldVersion) {
            dropTable(db, "CacheRec");
            onCreate(db);
        }
    }

    /**
     * 删除数据表
     * 
     * @param db
     * @param tableName
     */
    private void dropTable(SQLiteDatabase db, String tableName) {
        db.execSQL(new StringBuilder("DROP TABLE IF EXISTS ").append(tableName)
                .toString());
    }

    /**
     * 关闭数据库
     * 
     * @param cur
     * @param db
     */
    public void close(Cursor cur, SQLiteDatabase db) {
        try {
            if (cur != null) {
                cur.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (db != null) {
                    db.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
