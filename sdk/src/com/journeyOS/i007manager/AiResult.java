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

    public static final Creator<AiResult> CREATOR = new Creator<AiResult>() {
        @Override
        public AiResult createFromParcel(Parcel in) {
            return new AiResult(in);
        }

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
     * 得分(分词情况下没有得分)
     */
    private Float confidence;

    public AiResult(String label, Float confidence) {
        this.label = label;
        this.confidence = confidence;
    }

    protected AiResult(Parcel in) {
        label = in.readString();
        if (in.readByte() == 0) {
            confidence = null;
        } else {
            confidence = in.readFloat();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
        if (confidence == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(confidence);
        }
    }

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
     * 获取得分
     *
     * @return 得分
     */
    public Float getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return "AiTextResult{" +
                "label='" + label + '\'' +
                ", confidence=" + confidence +
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
         * 得分(分词情况下没有得分)
         */
        private Float confidence;

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
         * 设置得分
         *
         * @param confidence 得分
         * @return Builder
         */
        public Builder setConfidence(Float confidence) {
            this.confidence = confidence;
            return this;
        }

        /**
         * build
         *
         * @return AiTextResult
         */
        public AiResult build() {
            return new AiResult(label, confidence);
        }
    }

}
