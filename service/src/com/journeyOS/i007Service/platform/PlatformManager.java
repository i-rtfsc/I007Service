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

package com.journeyOS.i007Service.platform;

/**
 * 平台或者 feature 相关管理
 *
 * @author solo
 */
public final class PlatformManager extends BasePlatformApi {
    private static volatile PlatformManager sInstance = null;

    private PlatformManager() {
    }

    /**
     * 获取 PlatformManager 单例
     *
     * @return PlatformManager 实例
     */
    public static PlatformManager getInstance() {
        if (sInstance == null) {
            synchronized (PlatformManager.class) {
                if (sInstance == null) {
                    sInstance = new PlatformManager();
                }
            }
        }
        return sInstance;
    }

    public boolean supportMachineLearning() {
        return PRODUCT_ML.equals(getProduct());
    }
}