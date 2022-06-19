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

package com.journeyOS.i007Service;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.journeyOS.common.SmartLog;
import com.journeyOS.database.DataRepository;

import java.lang.ref.WeakReference;

/**
 * @author solo
 */
public final class AppConfig {
    private static final String TAG = AppConfig.class.getSimpleName();
    private static volatile AppConfig sInstance = null;

    private WeakReference<Application> mReference;

    private AppConfig() {
    }

    /**
     * 获取 AppConfig 单例
     *
     * @return AppConfig 实例
     */
    public static AppConfig getInstance() {
        if (sInstance == null) {
            synchronized (AppConfig.class) {
                if (sInstance == null) {
                    sInstance = new AppConfig();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     *
     * @param application 上下文
     */
    public void initialize(Application application) {
        mReference = new WeakReference<>(application);
        //initStrictMode();
        initDatabase(application);
    }


    /**
     * 获取上下文
     *
     * @return Application
     */
    public Application getApplication() {
        return mReference.get();
    }

    private void initStrictMode() {
        if (!"user".equals(Build.TYPE)) {
            SmartLog.w(TAG, "enable StrictMode on userdebug or eng build");
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder()
                            .detectDiskReads()
                            .detectDiskWrites()
                            .detectAll()   // or .detectAll() for all detectable problems
                            .penaltyLog()
                            .build());
            StrictMode.setVmPolicy(
                    new StrictMode.VmPolicy.Builder()
                            .detectLeakedSqlLiteObjects()
                            .detectLeakedClosableObjects()
                            .penaltyLog()
                            .penaltyDeath()
                            .build());
        }
    }

    private void initDatabase(Context context) {
        /**
         * DataRepository的构造函数里会拿到LocalDataSourceImpl实例
         * LocalDataSourceImpl的构造函数里又会判断是否需要初始化数据库
         */
        DataRepository.getInstance(context);
    }

}
