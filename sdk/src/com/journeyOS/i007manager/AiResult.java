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

package com.journeyOS.i007manager;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author solo
 */
public class AiResult implements Parcelable {

    /**
     * Creator
     */
    public static final Creator<AiResult> CREATOR = new Creator<AiResult>() {
        /**
         * {@inheritDoc}
         */
        @Override
        public AiResult createFromParcel(Parcel in) {
            return new AiResult(in);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public AiResult[] newArray(int size) {
            return new AiResult[size];
        }
    };
    /**
     * 标签
     */
    private String label;
    /**
     * 概率
     */
    private float probability;
    /**
     * 耗时（ms）
     */
    private long time;

    /**
     * 构造函数
     *
     * @param label       标签
     * @param probability 概率
     * @param time        耗时
     */
    public AiResult(String label, float probability, long time) {
        this.label = label;
        this.probability = probability;
        this.time = time;
    }

    /**
     * 构造函数
     *
     * @param in Parcel
     */
    protected AiResult(Parcel in) {
        label = in.readString();
        probability = in.readFloat();
        time = in.readLong();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
        dest.writeFloat(probability);
        dest.writeLong(time);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 获取标签
     *
     * @return 标签
     */
    public String getLabel() {
        return label;
    }

    /**
     * 获取概率
     *
     * @return 概率
     */
    public float getProbability() {
        return probability;
    }

    /**
     * 获取推荐模型花费的时间
     *
     * @return 时间
     */
    public long getTime() {
        return time;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "AiResult{" +
                "label='" + label + '\'' +
                ", probability=" + probability +
                ", time=" + time +
                '}';
    }

    /**
     * builder
     */
    public static class Builder {
        /**
         * 标签
         */
        private String label;

        /**
         * 概率
         */
        private float probability;

        /**
         * 耗时（ms）
         */
        private long time;

        /**
         * 设置标签
         *
         * @param label 标签
         * @return Builder
         */
        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        /**
         * 设置概率
         *
         * @param probability 概率
         * @return Builder
         */
        public Builder setProbability(float probability) {
            this.probability = probability;
            return this;
        }

        /**
         * 设置时间
         *
         * @param time 时间
         * @return Builder
         */
        public Builder setTime(long time) {
            this.time = time;
            return this;
        }

        /**
         * build
         *
         * @return AiTextResult
         */
        public AiResult build() {
            return new AiResult(label, probability, time);
        }
    }

}
