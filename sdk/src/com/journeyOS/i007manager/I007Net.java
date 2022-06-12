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
 * 网络类型
 *
 * @author solo
 */
public class I007Net implements Parcelable {

    /**
     * Creator
     */
    public static final Creator<I007Net> CREATOR = new Creator<I007Net>() {
        /**
         * {@inheritDoc}
         */
        @Override
        public I007Net createFromParcel(Parcel in) {
            return new I007Net(in);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public I007Net[] newArray(int size) {
            return new I007Net[size];
        }
    };
    private boolean available;
    private int type;

    /**
     * 构造函数
     *
     * @param available 网络是否可用
     * @param type      网络类型
     */
    public I007Net(boolean available, int type) {
        this.available = available;
        this.type = type;
    }

    /**
     * Parcel
     *
     * @param in Parcel
     */
    protected I007Net(Parcel in) {
        available = in.readByte() != 0;
        type = in.readInt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeInt(type);
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
        return "I007Net{" +
                "available=" + available +
                ", type=" + type +
                '}';
    }

    /**
     * 获取网络是否可用
     *
     * @return 网络是否可用
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * 获取网络类型
     *
     * @return 网络类型
     */
    public int getType() {
        return type;
    }

    /**
     * 网络类型
     */
    public static class Type {
        /**
         * 未知网络
         */
        public static final int NET_UNKNOWN = -1;

        /**
         * Wi-Fi网络
         */
        public static final int NET_WIFI = 0;

        /**
         * 5G网络
         */
        public static final int NET_5G = 1;

        /**
         * 4G网络
         */
        public static final int NET_4G = 2;

        /**
         * 3G网络
         */
        public static final int NET_3G = 3;

        /**
         * 2G网络
         */
        public static final int NET_2G = 4;
    }

    /**
     * Builder
     */
    public static class Builder {
        private boolean available;
        private int type = Type.NET_UNKNOWN;

        /**
         * 设置网络可用状态
         *
         * @param available 网络可用状态
         * @return Builder
         */
        public Builder setAvailable(boolean available) {
            this.available = available;
            return this;
        }

        /**
         * 设置网络类型
         *
         * @param type 网络类型
         * @return Builder
         */
        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        /**
         * build
         *
         * @return I007Net
         */
        public I007Net build() {
            return new I007Net(available, type);
        }
    }
}
