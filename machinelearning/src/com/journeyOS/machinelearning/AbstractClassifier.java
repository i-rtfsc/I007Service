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

import com.journeyOS.machinelearning.datas.ModelInfo;
import com.journeyOS.machinelearning.tasks.TaskResult;

import java.util.List;

/**
 * Generic interface for interacting with different recognition engines.
 */
public abstract class AbstractClassifier<T> {
    private static final String TAG = AbstractClassifier.class.getSimpleName();
    private boolean mStart = false;

    /**
     * recognize
     *
     * @param datas T class
     * @return List<TaskResult>
     */
    public final synchronized List<TaskResult> recognize(T datas) {
        return doRecognize(datas);
    }

    /**
     * enable log
     *
     * @param debug
     */
    public abstract void enableLog(boolean debug);

    /**
     * apply model info
     *
     * @param application Application
     * @param modelInfo   modelInfo
     * @return boolean
     */
    public final synchronized boolean applyModelInfo(Application application, ModelInfo modelInfo) {
        return onApplyModelInfo(application, modelInfo);
    }

    /**
     * release model info
     *
     * @return boolean
     */
    public final synchronized boolean releaseModelInfo() {
        return onReleaseModelInfo();
    }

    protected abstract boolean onApplyModelInfo(Application application, ModelInfo modelInfo);

    protected abstract List<TaskResult> doRecognize(T datas);

    protected abstract boolean onReleaseModelInfo();
}