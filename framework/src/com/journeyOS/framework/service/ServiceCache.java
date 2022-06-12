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

package com.journeyOS.framework.service;

import android.os.IBinder;

import com.journeyOS.common.SmartLog;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务缓存
 *
 * @author solo
 */
public final class ServiceCache {
    private static final String TAG = ServiceCache.class.getSimpleName();
    private static final int MAX_SERVICE = 10;
    private static Map<String, IBinder> sCache = new HashMap<>(MAX_SERVICE);

    /**
     * 构造方法
     */
    private ServiceCache() {
    }

    /**
     * 添加服务
     *
     * @param name    服务名称
     * @param service binder对象
     */
    public static void addService(String name, IBinder service) {
        sCache.put(name, service);
    }

    /**
     * 获取服务
     *
     * @param name 服务名称
     * @return binder对象
     */
    public static IBinder getService(String name) {
        return sCache.get(name);
    }

    /**
     * 删除服务
     *
     * @param name 服务名称
     * @return binder对象
     */
    public static IBinder removeService(String name) {
        return sCache.remove(name);
    }

    /**
     * 打印所有服务信息
     */
    public static void dumpService() {
        SmartLog.d(TAG, "service size = [" + sCache.size() + "]");
        for (String key : sCache.keySet()) {
            SmartLog.d(TAG, "service name = [" + key + "], service = [" + sCache.get(key) + "]");
        }
    }
}
