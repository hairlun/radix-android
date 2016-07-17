package com.patr.radix.dal;

import java.util.List;

import android.content.ContentValues;

/**
 * 缓存数据操作对象接口
 * 
 * @author zhoushujie
 * 
 */
public interface CacheDAO {

    /**
     * 保存缓存
     * 
     * @param url
     * @param content
     * @return
     * @throws Exception
     */
    boolean saveCache(String url, String content) throws Exception;

    /**
     * 获取缓存日期
     * 
     * @param url
     * @return
     * @throws Exception
     */
    long getDate(String url) throws Exception;

    /**
     * 删除缓存
     * 
     * @param url
     * @return
     * @throws Exception
     */
    boolean delete(String url) throws Exception;

    /**
     * 清除缓存
     * 
     * @return
     * @throws Exception
     */
    boolean clear() throws Exception;

    /**
     * 清除指定日期之前的缓存
     * 
     * @param milliseconds
     *            日期时间毫秒数
     * @return
     * @throws Exception
     */
    boolean clear(long milliseconds) throws Exception;

    /**
     * 清除指定表名、指定日期之前的缓存
     * 
     * @param tableName
     *            表名
     * @param milliseconds
     *            时间毫秒数
     * @return
     * @throws Exception
     */
    boolean clear(String tableName, long milliseconds) throws Exception;

    /**
     * 获取缓存数据
     * 
     * @param url
     * @return
     * @throws Exception
     */
    String getContent(String url) throws Exception;

    /**
     * 保存缓存数据
     * 
     * @param tbName
     *            表名
     * @param values
     *            键值对
     * @param keyName
     *            主键列名（用于判断记录是否存在）
     * @return
     * @throws Exception
     */
    boolean saveCache(String tbName, List<ContentValues> values, String keyName)
            throws Exception;

    /**
     * 更新缓存
     * 
     * @param tbName
     * @param values
     * @param whereClause
     * @param whereArgsList
     * @return
     * @throws Exception
     */
    boolean updateCache(String tbName, List<ContentValues> values,
            String whereClause, List<String[]> whereArgsList) throws Exception;

    /**
     * 
     * @param tbName
     * @param curPage
     * @param selection
     * @param whereValues
     * @param orderBy
     * @return
     * @throws Exception
     */
    List<ContentValues> listCache(String tbName, int curPage, String selection,
            String[] whereValues, String orderBy) throws Exception;

    /**
     * 
     * @param tbName
     * @param whereClause
     * @param whereArgsList
     * @return
     * @throws Exception
     */
    boolean deleteCache(String tbName, String whereClause,
            List<String[]> whereArgsList) throws Exception;
}
