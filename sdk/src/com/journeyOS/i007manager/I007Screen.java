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
public class I007Screen implements Parcelable {
    /**
     * Creator
     */
    public static final Creator<I007Screen> CREATOR = new Creator<I007Screen>() {
        /**
         * {@inheritDoc}
         */
        @Override
        public I007Screen createFromParcel(Parcel in) {
            return new I007Screen(in);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public I007Screen[] newArray(int size) {
            return new I007Screen[size];
        }
    };

    private int status;

    /**
     * 构造函数
     *
     * @param status 屏幕状态
     */
    public I007Screen(int status) {
        this.status = status;
    }

    /**
     * Parcel
     *
     * @param in Parcel
     */
    protected I007Screen(Parcel in) {
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
        return "I007Screen{" +
                "status=" + status +
                '}';
    }

    /**
     * 获取屏幕状态
     *
     * @return 屏幕状态
     */
    public int getStatus() {
        return status;
    }

    /**
     * 屏幕状态
     */
    public static class Status {
        /**
         * 未知状态
         */
        public static final int UNKNOWN = -1;

        /**
         * 亮屏
         */
        public static final int ON = 1;

        /**
         * 灭屏
         */
        public static final int OFF = 0;
    }

    /**
     * Builder
     */
    public static class Builder {
        private int status = Status.UNKNOWN;

        /**
         * 设置屏幕状态
         *
         * @param status 屏幕状态
         * @return Builder
         */
        public Builder setStatus(int status) {
            this.status = status;
            return this;
        }

        /**
         * build
         *
         * @return I007Screen
         */
        public I007Screen build() {
            return new I007Screen(status);
        }
    }
}
