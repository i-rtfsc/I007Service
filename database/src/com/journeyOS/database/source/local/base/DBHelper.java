/*
 * Copyright (c) 2022 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.journeyOS.database.source.local.base;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * database helper
 *
 * @author solo
 */
public final class DBHelper {
    private static final String TAG = DBHelper.class.getSimpleName();

    private static volatile DBHelper sInstance = null;
    private final Map<String, Object> mDatabaseMap = new HashMap<>();
    private final Migration migration1To2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //upgrade
        }
    };

    private DBHelper() {
    }

    /**
     * 获取DBHelper单例
     *
     * @return DBHelper实例
     */
    public static DBHelper getsInstance() {
        if (sInstance == null) {
            synchronized (DBHelper.class) {
                if (sInstance == null) {
                    sInstance = new DBHelper();

                }
            }
        }
        return sInstance;
    }

    /**
     * 获取RoomDatabase
     *
     * @param context 上下文
     * @param dbCls   RoomDatabase
     * @param dbName  数据库名字
     * @param <T>     RoomDatabase
     * @return RoomDatabase
     */
    public synchronized <T extends RoomDatabase> T getRoomDatabaseBuilder(Context context, Class<T> dbCls, String dbName) {
        String dbClsName = dbCls.getName();
        if (mDatabaseMap.get(dbClsName) == null) {
            T database = provider(context, dbCls, dbName);
            mDatabaseMap.put(dbClsName, database);
            return database;
        } else {
            Object database = mDatabaseMap.get(dbClsName);
            return (T) database;
        }
    }

    private <T extends RoomDatabase> T provider(Context context, Class<T> dbCls, String dbName) {
        return Room.databaseBuilder(context, dbCls, dbName)
//                .addMigrations(migration1To2)
//                .allowMainThreadQueries()
                .setJournalMode(RoomDatabase.JournalMode.AUTOMATIC)
                .fallbackToDestructiveMigration().build();
    }
}
