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
 * 耳机相关
 *
 * @author solo
 */
public class I007HeadSet implements Parcelable {
    public static final int HEADSET_ON = 1;
    public static final int HEADSET_OFF = 0;
    /**
     * Creator
     */
    public static final Creator<I007HeadSet> CREATOR = new Creator<I007HeadSet>() {
        /**
         * {@inheritDoc}
         */
        @Override
        public I007HeadSet createFromParcel(Parcel in) {
            return new I007HeadSet(in);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public I007HeadSet[] newArray(int size) {
            return new I007HeadSet[size];
        }
    };
    private int status;

    /**
     * 构造函数
     *
     * @param status 耳机状态
     */
    public I007HeadSet(int status) {
        this.status = status;
    }

    /**
     * Parcel
     *
     * @param in Parcel
     */
    protected I007HeadSet(Parcel in) {
        status = in.readInt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "I007HeadSet{" +
                "status=" + status +
                '}';
    }

    /**
     * 获取耳机状态
     *
     * @return 耳机状态
     */
    public int getStatus() {
        return status;
    }

    /**
     * Builder
     */
    public static class Builder {
        private int status;

        /**
         * 设置耳机状态
         *
         * @param status 耳机状态
         * @return Builder
         */
        public Builder setStatus(int status) {
            this.status = status;
            return this;
        }

        /**
         * build
         *
         * @return I007Battery
         */
        public I007HeadSet build() {
            return new I007HeadSet(status);
        }
    }
}
