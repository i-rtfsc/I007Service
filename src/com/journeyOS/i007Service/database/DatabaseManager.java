/*
 * Copyright (c) 2018 anqi.huang@outlook.com
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

package com.journeyOS.i007Service.database;

import android.util.LruCache;

import com.journeyOS.i007Service.base.constants.Constant;
import com.journeyOS.i007Service.base.utils.DebugUtils;
import com.journeyOS.i007Service.base.utils.Singleton;
import com.journeyOS.i007Service.base.utils.SpUtils;
import com.journeyOS.i007Service.task.TaskManager;

import java.util.List;


public class DatabaseManager {
    private static final String TAG = DatabaseManager.class.getSimpleName();
    private static final Singleton<DatabaseManager> gDefault = new Singleton<DatabaseManager>() {
        @Override
        protected DatabaseManager create() {
            return new DatabaseManager();
        }
    };
    private Object mLock = new Object();
    private LruCache<String, App> mCacheApps = new LruCache<String, App>(1500);

    private DatabaseManager() {
    }

    public static DatabaseManager getDefault() {
        return gDefault.get();
    }

    public void init() {
        if (SpUtils.getInstant().getInt(Constant.DB_BL_INIT, 0) != 0) {
            return;
        }

        TaskManager.getDefault().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                DBOperate.getDefault().initTableBlApp();
            }
        });

        if (SpUtils.getInstant().getInt(Constant.DB_INIT, 0) != 0) {
            return;
        }

        TaskManager.getDefault().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                DBOperate.getDefault().initTableApp();
            }
        });
    }

    public boolean addApp(String packageName, String type) {
        synchronized (mLock) {
            try {
                App app = new App();
                app.packageName = packageName;
                app.type = type;
                DBOperate.getDefault().saveOrUpdate(DBConfig.APPS_URL, app);
                mCacheApps.put(packageName, app);
                return true;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public App queryApp(String packageName) {
        if (SpUtils.getInstant().getInt(Constant.DB_INIT, 0) == 0) {
            DebugUtils.d(TAG, "database has't inited!");
            return null;
        }

        App app = null;
        synchronized (mLock) {
            app = mCacheApps.get(packageName);
        }
        if (app != null) {
            return app;
        } else {
            app = DBOperate.getDefault().queryApp(DBConfig.APPS_URL, packageName);
            mCacheApps.put(packageName, app);
            return app;
        }
    }

    public boolean removeApp(String packageName) {
        synchronized (mLock) {
            try {
                DBOperate.getDefault().deleteApp(DBConfig.APPS_URL, packageName);
                mCacheApps.remove(packageName);
                return true;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public List<App> queryApps(String type) {
        if (type == null) return null;
        return DBOperate.getDefault().queryApps(DBConfig.APPS_URL);
    }

    public boolean isBLApp(String packageName) {
        if (SpUtils.getInstant().getInt(Constant.DB_INIT, 0) == 0) {
            DebugUtils.d(TAG, "database has't inited!");
            return false;
        }

        App app = DBOperate.getDefault().queryApp(DBConfig.BL_APPS_URL, packageName);
        return packageName.equals(app.packageName);
    }
}