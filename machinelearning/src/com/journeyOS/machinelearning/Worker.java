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

/**
 * Worker
 */
public class Worker {
    private static final String TAG = Worker.class.getSimpleName();
    protected String name;
    protected AbstractClassifier classifier;
    protected ModelInfo modelInfo;

    protected Worker(Builder workerBuilder) {
        this.classifier = workerBuilder.classifier;
        this.modelInfo = workerBuilder.modelInfo;
        this.name = modelInfo.getName();
    }

    /**
     * getName
     *
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * getModelName
     *
     * @return modelInfo name
     */
    public String getModelName() {
        return modelInfo.getName();
    }

    /**
     * getClassifier
     *
     * @return Classifier
     */
    public AbstractClassifier getClassifier() {
        return classifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        int hashCode = this.classifier == null ? 0 : this.classifier.hashCode();
        return "Worker(" + this.name + "#+" + Integer.toHexString(hashCode) + ")";
    }

    /**
     * applyModel
     *
     * @param application Application
     * @return boolean
     */
    public boolean applyModel(Application application) {
        try {
            this.classifier.applyModelInfo(application, this.modelInfo);
            return true;
        } catch (Exception e) {
            SmartLog.w(TAG, this.toString() + " applyModel failed.");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * releaseModel
     *
     * @return boolean
     */
    public boolean releaseModel() {
        try {
            this.classifier.releaseModelInfo();
            return true;
        } catch (Exception e) {
            SmartLog.w(TAG, this.toString() + " applyModel failed.");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Builder
     *
     * @param <T> T class
     */
    public static class Builder<T extends Builder<T>> {
        protected AbstractClassifier classifier;
        protected ModelInfo modelInfo;

        /**
         * setClassifier
         *
         * @param classifier Classifier
         * @return T
         */
        public T setClassifier(AbstractClassifier classifier) {
            this.classifier = classifier;
            return (T) this;
        }

        /**
         * setModelInfo
         *
         * @param modelInfo ModelInfo
         * @return T
         */
        public T setModelInfo(ModelInfo modelInfo) {
            this.modelInfo = modelInfo;
            return (T) this;
        }

        /**
         * build
         *
         * @return Worker
         */
        public Worker build() {
            return new Worker(this);
        }
    }
}
