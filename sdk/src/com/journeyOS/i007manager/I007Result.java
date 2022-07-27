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
public class I007Result implements Parcelable {

    /**
     * Creator
     */
    public static final Creator<I007Result> CREATOR = new Creator<I007Result>() {
        /**
         * {@inheritDoc}
         */
        @Override
        public I007Result createFromParcel(Parcel in) {
            return new I007Result(in);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public I007Result[] newArray(int size) {
            return new I007Result[size];
        }
    };
    private long factoryId;
    private I007App app;
    private I007Net net;
    private I007Battery battery;
    private I007Screen screen;
    private I007HeadSet headSet;

    /**
     * 构造函数
     *
     * @param factoryId 场景因子
     * @param app       app
     * @param net       网络
     * @param battery   电池
     * @param screen    屏幕
     * @param headSet   耳机
     */
    public I007Result(long factoryId, I007App app, I007Net net, I007Battery battery, I007Screen screen, I007HeadSet headSet) {
        this.factoryId = factoryId;
        this.app = app;
        this.net = net;
        this.battery = battery;
        this.screen = screen;
        this.headSet = headSet;
    }

    /**
     * Parcel
     *
     * @param in Parcel
     */
    protected I007Result(Parcel in) {
        factoryId = in.readLong();
        app = in.readParcelable(I007App.class.getClassLoader());
        net = in.readParcelable(I007Net.class.getClassLoader());
        battery = in.readParcelable(I007Battery.class.getClassLoader());
        screen = in.readParcelable(I007Screen.class.getClassLoader());
        headSet = in.readParcelable(I007HeadSet.class.getClassLoader());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(factoryId);
        dest.writeParcelable(app, flags);
        dest.writeParcelable(net, flags);
        dest.writeParcelable(battery, flags);
        dest.writeParcelable(screen, flags);
        dest.writeParcelable(headSet, flags);
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
        return "I007Result{" +
                "factoryId=" + factoryId +
                ", app=" + app +
                ", net=" + net +
                ", battery=" + battery +
                ", screen=" + screen +
                ", headSet=" + headSet +
                '}';
    }

    /**
     * 获取场景因子
     *
     * @return 场景因子
     */
    public long getFactoryId() {
        return factoryId;
    }

    /**
     * 获取app
     *
     * @return app
     */
    public I007App getApp() {
        return app;
    }

    /**
     * 获取网络
     *
     * @return 网络
     */
    public I007Net getNet() {
        return net;
    }

    /**
     * 获取电池
     *
     * @return 电池
     */
    public I007Battery getBattery() {
        return battery;
    }

    /**
     * 获取屏幕
     *
     * @return 屏幕
     */
    public I007Screen getScreen() {
        return screen;
    }

    /**
     * 获取耳机
     *
     * @return 耳机
     */
    public I007HeadSet getHeadSet() {
        return headSet;
    }

    /**
     * Builder
     */
    public static class Builder {
        private long factoryId;
        private I007App app;
        private I007Net net;
        private I007Battery battery;
        private I007Screen screen;
        private I007HeadSet headSet;

        /**
         * 设置场景因子
         *
         * @param factoryId 场景因子
         * @return Builder
         */
        public Builder setFactoryId(long factoryId) {
            this.factoryId = factoryId;
            return this;
        }

        /**
         * 设置app
         *
         * @param app app
         * @return Builder
         */
        public Builder setApp(I007App app) {
            this.app = app;
            return this;
        }

        /**
         * 设置网络
         *
         * @param net 网络
         * @return Builder
         */
        public Builder setNet(I007Net net) {
            this.net = net;
            return this;
        }

        /**
         * 设置电池
         *
         * @param battery 电池
         * @return Builder
         */
        public Builder setBattery(I007Battery battery) {
            this.battery = battery;
            return this;
        }

        /**
         * 设置屏幕
         *
         * @param screen 屏幕
         * @return Builder
         */
        public Builder setScreen(I007Screen screen) {
            this.screen = screen;
            return this;
        }

        /**
         * 设置耳机
         *
         * @param headSet 耳机
         * @return Builder
         */
        public Builder setHeadSet(I007HeadSet headSet) {
            this.headSet = headSet;
            return this;
        }

        /**
         * build
         *
         * @return I007Result
         */
        public I007Result build() {
            return new I007Result(factoryId, app, net, battery, screen, headSet);
        }
    }

}
