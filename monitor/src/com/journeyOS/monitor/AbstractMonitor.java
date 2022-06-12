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

package com.journeyOS.monitor;

import com.journeyOS.common.SmartLog;

/**
 * @author solo
 */
public abstract class AbstractMonitor {
    private static final String TAG = AbstractMonitor.class.getSimpleName();

    protected long mFactoryId;
    protected boolean mInit = false;
    protected boolean mStart = false;

    /**
     * 同步初始化
     *
     * @param factoryId 场景因子
     * @return 初始化是否成功
     */
    public final synchronized boolean init(long factoryId) {
        mFactoryId = factoryId;
        if (mInit) {
            SmartLog.d(TAG, this.getClass().getSimpleName() + " is already inited");
        } else {
            SmartLog.d(TAG, this.getClass().getSimpleName() + " init , factoryId = [" + mFactoryId + "]");
            onInit(factoryId);
            mInit = true;
        }
        return mInit;
    }

    /**
     * 同步启动
     *
     * @return 启动是否成功
     */
    public final synchronized boolean start() {
        if (mStart) {
            SmartLog.d(TAG, this.getClass().getSimpleName() + " is already started");
        } else {
            SmartLog.d(TAG, this.getClass().getSimpleName() + " start , factoryId = [" + mFactoryId + "]");
            onStart();
            mStart = true;
        }
        return mStart;
    }

    /**
     * 同步关闭
     *
     * @return 关闭是否成功
     */
    public final synchronized boolean stop() {
        boolean ret = false;
        if (!mStart) {
            SmartLog.d(TAG, this.getClass().getSimpleName() + " is already stopped");
        } else {
            SmartLog.d(TAG, this.getClass().getSimpleName() + " stop , factoryId = [" + mFactoryId + "]");
            onStop();
            mInit = false;
            mStart = false;
        }
        return ret;
    }

    /**
     * 是否已经开始
     *
     * @return 是否已经开始
     */
    public final synchronized boolean isStarted() {
        return mStart;
    }

    /**
     * init monitor
     *
     * @param factoryId 场景因子
     */
    protected abstract void onInit(long factoryId);

    /**
     * start monitor
     */
    protected abstract void onStart();

    /**
     * stop monitor
     */
    protected abstract void onStop();
}
