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

import com.journeyOS.common.SmartLog;
import com.journeyOS.machinelearning.datas.ModelInfo;
import com.journeyOS.machinelearning.pytorch.PyTorchTextDetector;
import com.journeyOS.machinelearning.tflite.TFLiteTextDetector;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author solo
 */
public class WorkerManager {
    private static final String TAG = WorkerManager.class.getSimpleName();

    private static WorkerManager mInstance = null;
    private ConcurrentHashMap<String, Worker> mWorkers = new ConcurrentHashMap<>();
    private Application mContext;

    /**
     * getInstance
     *
     * @return WorkerFactory
     */
    public static WorkerManager getInstance() {
        if (mInstance == null) {
            synchronized (WorkerManager.class) {
                if (mInstance == null) {
                    mInstance = new WorkerManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * init
     *
     * @param application Application
     * @return WorkerFactory
     */
    public WorkerManager init(Application application) {
        mContext = application;
        return this;
    }

    /**
     * initWorker
     *
     * @param modelInfo ModelInfo
     */
    public void initWorker(ModelInfo modelInfo) {
        Worker newWorker = new Worker
                .Builder()
                .setClassifier(getMatchedClassifier(modelInfo))
                .setModelInfo(modelInfo)
                .build();

//        boolean ret = newWorker.applyModel(mContext);
//        if (ret) {
//            mWorkers.put(newWorker.getName(), newWorker);
//        } else {
//            SmartLog.w(TAG, "Model(" + modelInfo.getName() + ") apply failed");
//        }
        mWorkers.put(newWorker.getName(), newWorker);
    }

    /**
     * getWorker
     *
     * @param modelName modelName
     * @return Worker
     */
    public Worker getWorker(String modelName) {
        for (String workerName : mWorkers.keySet()) {
            Worker worker = mWorkers.get(workerName);
            if (modelName.equals(worker.getModelName())) {
                return worker;
            }
        }
        return null;
    }

    /**
     * applyModel
     *
     * @param modelInfo ModelInfo
     * @return boolean
     */
    public boolean applyModel(ModelInfo modelInfo) {
        Worker worker = getWorker(modelInfo.getWorkerName());
        if (worker != null) {
            worker.applyModel(mContext);
            return true;
        }

        return false;
    }

    /**
     * releaseModel
     *
     * @param modelInfo ModelInfo
     * @return boolean
     */
    public boolean releaseModel(ModelInfo modelInfo) {
        Worker worker = getWorker(modelInfo.getWorkerName());
        if (worker != null) {
            worker.releaseModel();
            return true;
        }

        return false;
    }

    private AbstractClassifier getMatchedClassifier(final ModelInfo modelInfo) {
        String workerName = modelInfo.getWorkerName();
        String graph = modelInfo.getGraph();
        AbstractClassifier classifier = null;
        SmartLog.d(TAG, "find Classifier = " + modelInfo.getName() + ", workerName = " + workerName + ", graph = " + graph);
        switch (graph) {
            case ModelInfo.Graph.TF_LITE:
                classifier = selectTfLiteClassifier(workerName);
                break;
            case ModelInfo.Graph.PY_TORCH:
                classifier = selectPyTorchClassifier(workerName);
                break;
            default:
                break;
        }
        return classifier;
    }

    private AbstractClassifier selectTfLiteClassifier(final String workerName) {
        switch (workerName) {
            case ModelInfo.Model.TEXT_CLASSIFICATION:
                return new TFLiteTextDetector();
            default:
                break;
        }
        SmartLog.e(TAG, "unsupport workerName = " + workerName);
        return null;
    }

    private AbstractClassifier selectPyTorchClassifier(final String workerName) {
        switch (workerName) {
            case ModelInfo.Model.TEXT_CLASSIFICATION:
                return new PyTorchTextDetector();
            default:
                break;
        }
        SmartLog.e(TAG, "unsupport workerName = " + workerName);
        return null;
    }

}
