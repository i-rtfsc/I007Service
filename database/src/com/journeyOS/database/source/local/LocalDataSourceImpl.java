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

package com.journeyOS.database.source.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.journeyOS.common.FileConfigConstant;
import com.journeyOS.common.SmartLog;
import com.journeyOS.common.task.TaskManager;
import com.journeyOS.common.utils.AESUtils;
import com.journeyOS.common.utils.FileUtils;
import com.journeyOS.common.utils.JsonHelper;
import com.journeyOS.database.source.api.LocalDataSource;
import com.journeyOS.database.source.local.app.App;
import com.journeyOS.database.source.local.app.AppDao;
import com.journeyOS.database.source.local.base.DBConfigs;
import com.journeyOS.database.source.local.base.DBConstant;
import com.journeyOS.database.source.local.base.DBHelper;
import com.journeyOS.database.source.local.base.I007Database;
import com.journeyOS.database.source.local.setting.Setting;
import com.journeyOS.database.source.local.setting.SettingDao;

import java.util.List;

/**
 * data from database
 *
 * @author solo
 */
public class LocalDataSourceImpl implements LocalDataSource {
    private static final String TAG = LocalDataSourceImpl.class.getSimpleName();
    private volatile static LocalDataSourceImpl INSTANCE = null;

    private Context mContext;
    private AppDao mAppDao;
    private SettingDao mSettingDao;

    private LocalDataSourceImpl(Context context) {
        mContext = context;
        I007Database database = DBHelper.getInstance().getRoomDatabaseBuilder(context,
                I007Database.class, DBConfigs.DB_NAME);
        mAppDao = database.appDao();
        mSettingDao = database.settingDao();

        TaskManager.getDefault().submit(new Runnable() {
            @Override
            public void run() {
                initApp();
            }
        });
    }

    /**
     * 获取LocalDataSourceImpl单例
     *
     * @param context 上下文
     * @return LocalDataSourceImpl实例
     */
    public static LocalDataSourceImpl getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LocalDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalDataSourceImpl(context);

                }
            }
        }
        return INSTANCE;
    }

    /**
     * 注销LocalDataSourceImpl单例
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    private void initApp() {
        boolean init = getBoolean(DBConstant.Setting.APP_INIT, DBConstant.Setting.APP_INIT_DEFAULT);
        if (init) {
            SmartLog.d(TAG, "app table has't been init");
            return;
        }

        SmartLog.d(TAG, "init app table!");
        String game = AESUtils.decrypt(FileUtils.readFileFromAsset(mContext, FileConfigConstant.GAME));
        if (game != null) {
            Apps appInfo = JsonHelper.fromJson(game, Apps.class);
            if (appInfo != null && !appInfo.apps.isEmpty()) {
                mAppDao.insertApps(appInfo.apps);
                init = true;
            }
        }

        String album = AESUtils.decrypt(FileUtils.readFileFromAsset(mContext, FileConfigConstant.ALBUM));
        if (album != null) {
            Apps appInfo = JsonHelper.fromJson(album, Apps.class);
            if (appInfo != null && !appInfo.apps.isEmpty()) {
                mAppDao.insertApps(appInfo.apps);
                init = true;
            }
        }

        String browser = AESUtils.decrypt(FileUtils.readFileFromAsset(mContext, FileConfigConstant.BROWSER));
        if (browser != null) {
            Apps appInfo = JsonHelper.fromJson(browser, Apps.class);
            if (appInfo != null && !appInfo.apps.isEmpty()) {
                mAppDao.insertApps(appInfo.apps);
                init = true;
            }
        }

        String im = AESUtils.decrypt(FileUtils.readFileFromAsset(mContext, FileConfigConstant.IM));
        if (im != null) {
            Apps appInfo = JsonHelper.fromJson(im, Apps.class);
            if (appInfo != null && !appInfo.apps.isEmpty()) {
                mAppDao.insertApps(appInfo.apps);
                init = true;
            }
        }

        String music = AESUtils.decrypt(FileUtils.readFileFromAsset(mContext, FileConfigConstant.MUSIC));
        if (music != null) {
            Apps appInfo = JsonHelper.fromJson(music, Apps.class);
            if (appInfo != null && !appInfo.apps.isEmpty()) {
                mAppDao.insertApps(appInfo.apps);
                init = true;
            }
        }

        String news = AESUtils.decrypt(FileUtils.readFileFromAsset(mContext, FileConfigConstant.NEWS));
        if (news != null) {
            Apps appInfo = JsonHelper.fromJson(news, Apps.class);
            if (appInfo != null && !appInfo.apps.isEmpty()) {
                mAppDao.insertApps(appInfo.apps);
                init = true;
            }
        }

        String reader = AESUtils.decrypt(FileUtils.readFileFromAsset(mContext, FileConfigConstant.READER));
        if (reader != null) {
            Apps appInfo = JsonHelper.fromJson(reader, Apps.class);
            if (appInfo != null && !appInfo.apps.isEmpty()) {
                mAppDao.insertApps(appInfo.apps);
                init = true;
            }
        }

        String video = AESUtils.decrypt(FileUtils.readFileFromAsset(mContext, FileConfigConstant.VIDEO));
        if (video != null) {
            Apps appInfo = JsonHelper.fromJson(video, Apps.class);
            if (appInfo != null && !appInfo.apps.isEmpty()) {
                mAppDao.insertApps(appInfo.apps);
                init = true;
            }
        }

        put(DBConstant.Setting.APP_INIT, init);
    }

    @Nullable
    @Override
    public App getApp(@NonNull String packageName) {
        return mAppDao.searchApp(packageName);
    }

    @Nullable
    @Override
    public List<App> getAllApps() {
        return mAppDao.getAll();
    }

    @Override
    public void saveApp(@NonNull App app) {
        mAppDao.insetOrUpdateApp(app);
    }

    private Setting get(@NonNull String key, @NonNull Object defaultValue) {
        Setting setting = mSettingDao.getSetting(key);
        if (setting == null) {
            Class<?> clazz = defaultValue.getClass();
            final String object = clazz.getName();

            setting = new Setting();
            setting.key = key;
            setting.value = defaultValue.toString();
            setting.object = object;
        }

        return setting;
    }

    @Override
    public void put(@NonNull String key, @NonNull Object defaultValue) {
        Setting setting = mSettingDao.getSetting(key);
        if (setting == null) {
            setting = new Setting();
            setting.key = key;
        }
        Class<?> clazz = defaultValue.getClass();
        setting.object = clazz.getName();
        setting.value = defaultValue.toString();
        mSettingDao.saveSetting(setting);
    }

    @Nullable
    @Override
    public String getString(@NonNull String key) {
        Setting setting = get(key, "");
        return setting.getString();
    }

    @NonNull
    @Override
    public String getString(@NonNull String key, @NonNull String defaultValue) {
        Setting setting = get(key, defaultValue);
        return setting.getString();
    }

    @Override
    public int getInt(@NonNull String key) {
        Setting setting = get(key, 0);
        return setting.getInt();
    }

    @Override
    public int getInt(@NonNull String key, int defaultValue) {
        Setting setting = get(key, defaultValue);
        return setting.getInt();
    }

    @Override
    public boolean getBoolean(@NonNull String key) {
        Setting setting = get(key, false);
        return setting.getBoolean();
    }

    @Override
    public boolean getBoolean(@NonNull String key, boolean defaultValue) {
        Setting setting = get(key, defaultValue);
        return setting.getBoolean();
    }

    @Override
    public float getFloat(@NonNull String key) {
        Setting setting = get(key, 0);
        return setting.getFloat();
    }

    @Override
    public float getFloat(@NonNull String key, float defaultValue) {
        Setting setting = get(key, defaultValue);
        return setting.getFloat();
    }

    /**
     * 用来解析asset目录的json字符串
     */
    public class Apps {
        public String version;
        public List<App> apps;
    }
}
