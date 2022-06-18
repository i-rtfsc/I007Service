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

package com.journeyOS.machinelearning;

import android.content.Context;

import com.journeyOS.common.SmartLog;
import com.journeyOS.common.task.TaskManager;
import com.journeyOS.i007manager.AiModel;
import com.journeyOS.machinelearning.tasks.TaskResult;

/**
 * Generic interface for interacting with different recognition engines.
 *
 * @author solo
 */
public abstract class Classifier<T> {
    private static final String TAG = Classifier.class.getSimpleName();
    protected boolean mStart = false;
    protected boolean enableLog = false;

    /**
     * 识别
     *
     * @param data 模板类
     * @return TaskResult 任务结果
     */
    public final synchronized TaskResult recognize(T data) {
        return doRecognize(data);
    }

    /**
     * log开关
     *
     * @param debug 是否打开log
     */
    public void enableLog(boolean debug) {
        enableLog = debug;
    }

    /**
     * 是否打开log
     *
     * @return 是否打开log
     */
    protected boolean isEnableLog() {
        return enableLog;
    }

    /**
     * 加载模型
     *
     * @param context 上下文
     * @param aiModel 模型信息
     * @return boolean 是否成功
     */
    public final synchronized boolean applyModelInfo(Context context, AiModel aiModel) {
        if (mStart) {
            SmartLog.d(TAG, this.TAG + " is already started");
        } else {
            TaskManager.getDefault().submit(new Runnable() {
                @Override
                public void run() {
                    mStart = onApplyModelInfo(context, aiModel);
                }
            });
        }
        //TODO
        //上面是线程里执行，所以return是不准确的
        return mStart;
    }

    /**
     * 释放模型
     *
     * @return boolean 是否成功
     */
    public final synchronized boolean releaseModelInfo() {
        boolean ret = false;
        if (!mStart) {
            SmartLog.d(TAG, this.TAG + " is already stopped");
        } else {
            ret = onReleaseModelInfo();
            mStart = false;
        }
        return ret;
    }

    /**
     * 加载模型
     *
     * @param context 上下文
     * @param aiModel 模型配置
     * @return 是否成功
     */
    protected abstract boolean onApplyModelInfo(Context context, AiModel aiModel);

    /**
     * 检测
     *
     * @param data 模版类
     * @return 识别结果
     */
    protected abstract TaskResult doRecognize(T data);

    /**
     * 释放模型
     *
     * @return 是否成功
     */
    protected abstract boolean onReleaseModelInfo();
}