package com.patr.radix.dal;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import org.xutils.common.util.LogUtil;

import com.patr.radix.utils.Utils;

/**
 * 缓存记录数据操作对象
 * 
 * @author huangzhongwen
 * 
 */
public class CacheDAOImpl implements CacheDAO {

    private static final int LIMIT = 20;
    DatabaseHelper helper;

    /**
     * 相关Key
     * 
     * @author huangzhongwen
     * 
     */
    public interface Key {

        /**
         * 表名称
         */
        String TB = "CacheRec";

        /**
         * 请求URL
         */
        String URL = "textUrl";

        /**
         * 缓存更新时间
         */
        String M_TIME = "mTime";

        /**
         * 响应内容
         */
        String CONTENT = "textContent";

    }

    public CacheDAOImpl(Context context) {
        helper = DatabaseHelper.getInstance(context,
                DatabaseHelper.DATABASE_NAME);
    }

    /**
     * 获取缓存日期
     * 
     * @param url
     * @return
     * @throws Exception
     */
    public synchronized long getDate(String url) throws Exception {
        long result = 0;
        if (TextUtils.isEmpty(url)) {
            return result;
        }
        // 开始数据库操作
        SQLiteDatabase db = null;
        Cursor cur = null;
        try {
            String selection = Key.URL + "=?";
            String[] selectionArgs = { url };
            db = helper.getReadableDatabase();
            while (db.isDbLockedByOtherThreads()
                    || db.isDbLockedByCurrentThread()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            cur = db.query(Key.TB, null, selection, selectionArgs, null, null,
                    null);
            if (cur != null) {
                if (cur.moveToNext()) {
                    result = cur.getLong(cur.getColumnIndex(Key.M_TIME));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e);
        } finally {
            helper.close(cur, db);
        }
        return result;
    }

    /**
     * 删除缓存记录
     * 
     * @param url
     * @return
     * @throws Exception
     */
    public synchronized boolean delete(String url) throws Exception {
        boolean result = false;
        if (TextUtils.isEmpty(url)) {
            return result;
        }
        SQLiteDatabase db = null;
        try {
            String whereClause = Key.URL + "=?";
            String[] whereArgs = { url };
            db = helper.getWritableDatabase();
            while (db.isDbLockedByOtherThreads()
                    || db.isDbLockedByCurrentThread()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            result = db.delete(Key.TB, whereClause, whereArgs) != 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e);
        } finally {
            helper.close(null, db);
        }
        return result;
    }

    /**
     * 清除缓存记录
     * 
     * @return
     * @throws Exception
     */
    public synchronized boolean clear() throws Exception {
        boolean result = false;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            while (db.isDbLockedByOtherThreads()
                    || db.isDbLockedByCurrentThread()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            result = db.delete(Key.TB, null, null) != 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e);
        } finally {
            helper.close(null, db);
        }
        return result;
    }

    @Override
    public synchronized boolean saveCache(String url, String content)
            throws Exception {
        boolean result = false;
        if (TextUtils.isEmpty(url)) {
            return result;
        }
        LogUtil.i(url);
        String[] keys = { Key.URL, Key.M_TIME };
        String[] values = { url, String.valueOf(System.currentTimeMillis()) };
        if (!TextUtils.isEmpty(content)) {
            LogUtil.i(content);
            keys = new String[] { Key.URL, Key.CONTENT, Key.M_TIME };
            values = new String[] { url, content,
                    String.valueOf(System.currentTimeMillis()) };
        }
        ContentValues cv = createContentValues(keys, values);
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            while (db.isDbLockedByOtherThreads()
                    || db.isDbLockedByCurrentThread()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            result = db.replace(Key.TB, null, cv) != -1;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e);
        } finally {
            helper.close(null, db);
        }
        return result;
    }

    @Override
    public synchronized String getContent(String url) throws Exception {
        String result = null;
        if (TextUtils.isEmpty(url)) {
            return result;
        }
        // 开始数据库操作
        SQLiteDatabase db = null;
        Cursor cur = null;
        try {
            db = helper.getReadableDatabase();
            while (db.isDbLockedByOtherThreads()
                    || db.isDbLockedByCurrentThread()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String selection = Key.URL + "=?";
            String[] selectionArgs = { url };
            cur = db.query(Key.TB, null, selection, selectionArgs, null, null,
                    null);
            if (cur != null) {
                if (cur.moveToNext()) {
                    result = cur.getString(cur.getColumnIndex(Key.CONTENT));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e);
        } finally {
            helper.close(cur, db);
        }
        return result;
    }

    @Override
    public synchronized boolean clear(long milliseconds) throws Exception {
        return clear(Key.TB, milliseconds);
    }

    @Override
    public synchronized boolean clear(String tableName, long milliseconds)
            throws Exception {
        boolean result = false;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            while (db.isDbLockedByOtherThreads()
                    || db.isDbLockedByCurrentThread()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            result = db.delete(tableName, Key.M_TIME + "<=?",
                    new String[] { String.valueOf(milliseconds) }) != 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e);
        } finally {
            helper.close(null, db);
        }
        return result;
    }

    @Override
    public synchronized boolean saveCache(String tbName,
            List<ContentValues> values, String keyName) throws Exception {
        boolean result = false;
        if (values == null || values.isEmpty()) {
            return true;
        }
        SQLiteDatabase db = null;
        Cursor cur = null;
        try {
            db = helper.getWritableDatabase();
            while (db.isDbLockedByOtherThreads()
                    || db.isDbLockedByCurrentThread()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 开始事务
            db.beginTransaction();
            int size = values.size();
            for (int i = 0; i < size; i++) {
                ContentValues value = values.get(i);
                cur = db.query(tbName, null, keyName + "=?",
                        new String[] { value.getAsString(keyName) }, null,
                        null, null);
                long l = -1;
                if (cur != null && cur.moveToNext()) {
                    l = db.update(tbName, value, keyName + "=?",
                            new String[] { value.getAsString(keyName) });
                } else {
                    l = db.insert(tbName, null, value);
                }
                LogUtil.i("long = " + l);
                cur.close();
            }
            // 事务成功
            db.setTransactionSuccessful();
            LogUtil.i("values.count=" + values.size());
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e);
        } finally {
            // 结束并提交事务
            db.endTransaction();
            helper.close(cur, db);
        }
        return result;
    }

    @Override
    public synchronized List<ContentValues> listCache(String tbName,
            int curPage, String selection, String[] whereValues, String orderBy)
            throws Exception {
        List<ContentValues> list = null;
        // 准备数据
        String limitStr = null;
        if (curPage > 0) {
            // 页码大于0，表示分页查询，否则查询所有
            limitStr = (curPage - 1) * LIMIT + "," + LIMIT;
        }
        // 数据库操作
        SQLiteDatabase db = null;
        Cursor cur = null;
        try {
            db = helper.getReadableDatabase();
            while (db.isDbLockedByOtherThreads()
                    || db.isDbLockedByCurrentThread()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            cur = db.query(tbName, null, selection, whereValues, null, null,
                    orderBy, limitStr);
            if (cur != null) {
                LogUtil.i("cur=" + cur.getCount());
                list = new ArrayList<ContentValues>();
                ContentValues cv = null;
                while (cur.moveToNext()) {
                    cv = new ContentValues();
                    for (int i = 0; i < cur.getColumnCount(); i++) {
                        cv.put(cur.getColumnName(i), cur.getString(i));
                    }
                    LogUtil.i(cv.toString());
                    list.add(cv);
                }
            } else {
                LogUtil.i("cur=" + null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            helper.close(cur, db);
        }
        return list;
    }

    @Override
    public synchronized boolean deleteCache(String tbName, String whereClause,
            List<String[]> whereArgsList) throws Exception {
        boolean result = false;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            while (db.isDbLockedByOtherThreads()
                    || db.isDbLockedByCurrentThread()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 开始事务
            db.beginTransaction();
            int size = whereArgsList.size();
            for (int i = 0; i < size; i++) {
                String[] whereArgs = null;
                if (whereArgsList != null) {
                    whereArgs = whereArgsList.get(i);
                }
                db.delete(tbName, whereClause, whereArgs);
            }
            // 事务成功
            db.setTransactionSuccessful();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e);
        } finally {
            // 结束并提交事务
            db.endTransaction();
            helper.close(null, db);
        }
        return result;
    }

    @Override
    public boolean updateCache(String tbName, List<ContentValues> values,
            String whereClause, List<String[]> whereArgsList) throws Exception {
        boolean result = false;
        if (values == null || values.isEmpty()) {
            return true;
        }
        SQLiteDatabase db = null;
        Cursor cur = null;
        try {
            db = helper.getWritableDatabase();
            while (db.isDbLockedByOtherThreads()
                    || db.isDbLockedByCurrentThread()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 开始事务
            db.beginTransaction();
            int size = values.size();
            for (int i = 0; i < size; i++) {
                ContentValues value = values.get(i);
                db.update(tbName, value, whereClause, whereArgsList.get(i));
            }
            // 事务成功
            db.setTransactionSuccessful();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e);
        } finally {
            // 结束并提交事务
            db.endTransaction();
            helper.close(cur, db);
        }
        return result;
    }

    /**
     * 创建ContentValues
     * 
     * @param keys
     * @param values
     * @return
     */
    public static ContentValues createContentValues(String[] keys,
            String[] values) {
        ContentValues contentValues = new ContentValues();
        int len = keys.length;
        for (int i = 0; i < len; i++) {
            contentValues.put(keys[i], values[i]);
        }
        contentValues.put(CacheDAOImpl.Key.M_TIME,
                String.valueOf(System.currentTimeMillis()));
        return contentValues;
    }
}
