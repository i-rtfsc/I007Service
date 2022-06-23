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

package com.journeyOS.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.journeyOS.database.source.api.HttpDataSource;
import com.journeyOS.database.source.api.LocalDataSource;
import com.journeyOS.database.source.http.HttpDataSourceImpl;
import com.journeyOS.database.source.local.LocalDataSourceImpl;
import com.journeyOS.database.source.local.app.App;
import com.journeyOS.i007manager.SmartLog;

import java.util.List;

/**
 * @author solo
 */
public final class DataRepository implements HttpDataSource, LocalDataSource {
    private static final String TAG = DataRepository.class.getSimpleName();
    private static volatile DataRepository sInstance = null;
    private final HttpDataSource mHttpDataSource;
    private final LocalDataSource mLocalDataSource;

    private DataRepository(Context context) {
        SmartLog.d(TAG, "init data impl...");
        this.mHttpDataSource = HttpDataSourceImpl.getInstance(context);
        this.mLocalDataSource = LocalDataSourceImpl.getInstance(context);
    }

    /**
     * 获取DataRepository实例
     *
     * @param context 上下文
     * @return DataRepository
     */
    public static DataRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(context);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void put(@NonNull String key, @NonNull Object defaultValue) {
        mLocalDataSource.put(key, defaultValue);
    }

    @NonNull
    @Override
    public String getString(@NonNull String key) {
        return mLocalDataSource.getString(key);
    }

    @NonNull
    @Override
    public String getString(@NonNull String key, @NonNull String defaultValue) {
        return mLocalDataSource.getString(key, defaultValue);
    }

    @Override
    public int getInt(@NonNull String key) {
        return mLocalDataSource.getInt(key);
    }

    @Override
    public int getInt(@NonNull String key, int defaultValue) {
        return mLocalDataSource.getInt(key, defaultValue);
    }

    @Override
    public boolean getBoolean(@NonNull String key) {
        return mLocalDataSource.getBoolean(key);
    }

    @Override
    public boolean getBoolean(@NonNull String key, boolean defaultValue) {
        return mLocalDataSource.getBoolean(key, defaultValue);
    }

    @Override
    public float getFloat(@NonNull String key) {
        return mLocalDataSource.getFloat(key);
    }

    @Override
    public float getFloat(@NonNull String key, float defaultValue) {
        return mLocalDataSource.getFloat(key, defaultValue);
    }

    @Nullable
    @Override
    public App getApp(@NonNull String packageName) {
        return mLocalDataSource.getApp(packageName);
    }

    @Nullable
    @Override
    public List<App> getAllApps() {
        return mLocalDataSource.getAllApps();
    }

    @Override
    public void saveApp(@NonNull App app) {
        mLocalDataSource.saveApp(app);
    }
}
