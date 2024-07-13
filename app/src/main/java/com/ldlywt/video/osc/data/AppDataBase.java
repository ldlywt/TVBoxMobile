package com.ldlywt.video.osc.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ldlywt.video.osc.cache.Cache;
import com.ldlywt.video.osc.cache.CacheDao;
import com.ldlywt.video.osc.cache.VodCollect;
import com.ldlywt.video.osc.cache.VodCollectDao;
import com.ldlywt.video.osc.cache.VodRecord;
import com.ldlywt.video.osc.cache.VodRecordDao;


/**
 * 类描述:
 *
 * @author pj567
 * @since 2020/5/15
 */
@Database(entities = {Cache.class, VodRecord.class, VodCollect.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract CacheDao getCacheDao();

    public abstract VodRecordDao getVodRecordDao();

    public abstract VodCollectDao getVodCollectDao();
}
