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

import android.app.Application;
import android.util.Pair;

import com.journeyOS.common.task.TaskManager;
import com.journeyOS.i007manager.AiModel;
import com.journeyOS.i007manager.SmartLog;
import com.journeyOS.machinelearning.tasks.TaskResult;

/**
 * Generic interface for interacting with different recognition engines.
 *
 * @author solo
 */
public abstract class Classifier<T> {
    private static final String TAG = Classifier.class.getSimpleName();
    protected boolean mStart = false;
    private long mLastBegin;

    /**
     * 加载模型
     *
     * @param application 上下文
     * @param aiModel     模型信息
     * @return boolean 是否成功
     */
    public final synchronized boolean loadModel(Application application, AiModel aiModel) {
        if (mStart) {
            SmartLog.d(TAG, this.TAG + " is already started");
        } else {
            TaskManager.getDefault().submit(new Runnable() {
                @Override
                public void run() {
                    mStart = onStart(application, aiModel);
                    if (mStart) {
                        /**
                         * 如果模型加载成功，再加载有需要的额外配置
                         */
                        onExtraLoad(application, aiModel);
                    }
                }
            });
        }
        //TODO
        //上面是线程里执行，所以return是不准确的
        return mStart;
    }

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
     * 释放模型
     *
     * @return boolean 是否成功
     */
    public final synchronized boolean unloadModel() {
        boolean ret = false;
        if (!mStart) {
            SmartLog.d(TAG, this.TAG + " is already stopped");
        } else {
            ret = onStop();
            mStart = false;
        }
        return ret;
    }

    /**
     * 加载模型
     *
     * @param application 上下文
     * @param aiModel     模型配置
     * @return 是否成功
     */
    protected abstract boolean onStart(Application application, AiModel aiModel);

    /**
     * 加载模型后再 init 一些需要的配置，如初始化labels等
     *
     * @param application 上下文
     * @param aiModel     模型配置
     */
    protected abstract void onExtraLoad(Application application, AiModel aiModel);

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
    protected abstract boolean onStop();

    /**
     * 是否加载模型
     *
     * @return 是否加载模型
     */
    protected final synchronized boolean isStarted() {
        return mStart;
    }

    /**
     * 获取前k个数据
     *
     * @param k      需要获取前多少个数据
     * @param tensor 源数据
     * @return 前k个数据
     */
    protected Pair<Integer, Float>[] topK(int k, final float[] tensor) {
        final boolean[] selected = new boolean[tensor.length];
        final Pair<Integer, Float> topK[] = new Pair[k];
        int count = 0;
        while (count < k) {
            final int index = top(tensor, selected);
            selected[index] = true;
            topK[count] = new Pair<>(index, tensor[index]);
            count++;
        }
        return topK;
    }

    private int top(final float[] array, boolean[] selected) {
        int index = 0;
        float max = -1.f;
        for (int i = 0; i < array.length; i++) {
            if (selected[i]) {
                continue;
            }
            if (array[i] > max) {
                max = array[i];
                index = i;
            }
        }
        return index;
    }

    /**
     * 开始时间
     */
    protected void startInterval() {
        mLastBegin = System.currentTimeMillis();
    }

    /**
     * 结束时间
     *
     * @param message 需要打印的信息（一般是模型名称）
     * @return 耗时
     */
    protected long stopInterval(String message) {
        final long duration = System.currentTimeMillis() - mLastBegin;
        SmartLog.d(TAG, message + ", time = [" + duration + "ms]");
        return duration;
    }
}