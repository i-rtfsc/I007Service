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
import com.journeyOS.i007manager.AiModel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * worker管理者
 *
 * @author solo
 */
public class WorkerManager {
    private static final String TAG = WorkerManager.class.getSimpleName();

    private static WorkerManager mInstance = null;
    private ConcurrentHashMap<String, Worker> mWorkers = new ConcurrentHashMap<>();
    private Application mApplication;

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
     * @param application 上下文
     * @return WorkerFactory
     */
    public WorkerManager init(Application application) {
        mApplication = application;
        return this;
    }

    /**
     * initWorker
     *
     * @param aiModel ModelInfo
     * @return 是否成功
     */
    public boolean initWorker(AiModel aiModel) {
        Classifier classifier = getMatchedClassifier(aiModel);
        if (classifier == null) {
            return false;
        }

        Worker newWorker = new Worker.Builder()
                .setClassifier(classifier)
                .setModelInfo(aiModel)
                .build();

        mWorkers.put(newWorker.getName(), newWorker);
        return true;
    }

    /**
     * 获取工作者
     *
     * @param modelName 模型名字
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
     * 加载模型
     *
     * @param aiModel 模型信息
     * @return boolean 是否成功
     */
    public boolean applyModel(AiModel aiModel) {
        Worker worker = getWorker(aiModel.getWorkerName());
        if (worker != null) {
            return worker.applyModel(mApplication);
        }

        return false;
    }

    /**
     * 释放模型
     *
     * @param aiModel 模型信息
     * @return boolean 是否成功
     */
    public boolean releaseModel(AiModel aiModel) {
        Worker worker = getWorker(aiModel.getWorkerName());
        if (worker != null) {
            return worker.releaseModel();
        }

        return false;
    }

    /**
     * 通过模型信息找到Classifier
     *
     * @param aiModel 模型信息
     * @return Classifier
     */
    private Classifier getMatchedClassifier(final AiModel aiModel) {
        String workerName = aiModel.getWorkerName();
        String graph = aiModel.getGraph();
        Classifier classifier = null;
        SmartLog.d(TAG, "find Classifier = " + aiModel.getName() + ", workerName = " + workerName + ", graph = " + graph);
        switch (graph) {
            case AiModel.Graph.TF_LITE:
                classifier = selectTfLiteClassifier(workerName);
                break;
            case AiModel.Graph.PY_TORCH:
                classifier = selectPyTorchClassifier(workerName);
                break;
            case AiModel.Graph.SNPE:
                classifier = selectSnpeClassifier(workerName);
                break;
            case AiModel.Graph.MACE:
                classifier = selectMaceClassifier(workerName);
                break;
            default:
                break;
        }
        return classifier;
    }

    /**
     * 根据 workerName 找到 tflite classifier
     *
     * @param workerName worker 名称
     * @return tflite classifier
     */
    private Classifier selectTfLiteClassifier(final String workerName) {
        switch (workerName) {
            case AiModel.Model.TEXT_CLASSIFICATION:
                return reflectClassifier("com.journeyOS.tflite.TextDetector");
            case AiModel.Model.IMAGE_CLASSIFICATION:
                return reflectClassifier("com.journeyOS.tflite.ImageClassifier");
            default:
                break;
        }
        SmartLog.e(TAG, "un-support workerName = " + workerName);
        return null;
    }

    /**
     * 根据 workerName 找到 pytorch classifier
     *
     * @param workerName worker 名称
     * @return pytorch classifier
     */
    private Classifier selectPyTorchClassifier(final String workerName) {
        switch (workerName) {
            case AiModel.Model.TEXT_CLASSIFICATION:
                return reflectClassifier("com.journeyOS.pytorch.TextDetector");
            case AiModel.Model.IMAGE_CLASSIFICATION:
                return reflectClassifier("com.journeyOS.pytorch.ImageClassifier");
            default:
                break;
        }
        SmartLog.e(TAG, "un-support workerName = " + workerName);
        return null;
    }

    /**
     * 根据 workerName 找到 snpe classifier
     *
     * @param workerName worker 名称
     * @return snpe classifier
     */
    private Classifier selectSnpeClassifier(final String workerName) {
        switch (workerName) {
            case AiModel.Model.IMAGE_CLASSIFICATION:
                return reflectClassifier("com.journeyOS.snpe.ImageClassifier");
            default:
                break;
        }
        SmartLog.e(TAG, "un-support workerName = " + workerName);
        return null;
    }

    /**
     * 根据 workerName 找到 mace classifier
     *
     * @param workerName worker 名称
     * @return mace classifier
     */
    private Classifier selectMaceClassifier(final String workerName) {
        switch (workerName) {
            case AiModel.Model.IMAGE_CLASSIFICATION:
                return reflectClassifier("com.journeyOS.mace.ImageClassifier");
            default:
                break;
        }
        SmartLog.e(TAG, "un-support workerName = " + workerName);
        return null;
    }

    /**
     * 反射Classifier
     *
     * @param className 类名
     * @return Classifier
     */
    private Classifier reflectClassifier(String className) {
        try {
            return (Classifier) Class.forName(className).newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
