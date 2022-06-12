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
 * 电池相关
 *
 * @author solo
 */
public class I007Battery implements Parcelable {
    /**
     * Creator
     */
    public static final Creator<I007Battery> CREATOR = new Creator<I007Battery>() {
        /**
         * {@inheritDoc}
         */
        @Override
        public I007Battery createFromParcel(Parcel in) {
            return new I007Battery(in);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public I007Battery[] newArray(int size) {
            return new I007Battery[size];
        }
    };
    private int level;
    private int status;
    private int temperature;
    private int health;
    private int pluggedIn;

    /**
     * 构造函数
     *
     * @param level       电池等级（0-100）
     * @param status      电池状态
     * @param temperature 电池温度
     * @param health      电池状态状况
     * @param pluggedIn   电池充电状态
     */
    public I007Battery(int level, int status, int temperature, int health, int pluggedIn) {
        this.level = level;
        this.status = status;
        this.temperature = temperature;
        this.health = health;
        this.pluggedIn = pluggedIn;
    }

    /**
     * Parcel
     *
     * @param in Parcel
     */
    protected I007Battery(Parcel in) {
        level = in.readInt();
        status = in.readInt();
        temperature = in.readInt();
        health = in.readInt();
        pluggedIn = in.readInt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(level);
        dest.writeInt(status);
        dest.writeInt(temperature);
        dest.writeInt(health);
        dest.writeInt(pluggedIn);
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
        return "I007Battery{" +
                "level=" + level +
                ", status=" + status +
                ", temperature=" + temperature +
                ", health=" + health +
                ", pluggedIn=" + pluggedIn +
                '}';
    }

    /**
     * 获取电池等级
     *
     * @return 电池等级
     */
    public int getLevel() {
        return level;
    }

    /**
     * 获取电池状态
     *
     * @return 电池状态
     */
    public int getStatus() {
        return status;
    }

    /**
     * 获取电池温度
     *
     * @return 电池温度
     */
    public int getTemperature() {
        return temperature;
    }

    /**
     * 获取电池健康状况
     *
     * @return 电池健康状况
     */
    public int getHealth() {
        return health;
    }

    /**
     * 获取电池充电状态
     *
     * @return 电池充电状态
     */
    public int getPluggedIn() {
        return pluggedIn;
    }

    /**
     * Builder
     */
    public static class Builder {
        private int level;
        private int status;
        private int temperature;
        private int health;
        private int pluggedIn;

        /**
         * 设置电池等级
         *
         * @param level 电池等级
         * @return Builder
         */
        public Builder setLevel(int level) {
            this.level = level;
            return this;
        }

        /**
         * 设置电池状态
         *
         * @param status 电池状态
         * @return Builder
         */
        public Builder setStatus(int status) {
            this.status = status;
            return this;
        }

        /**
         * 设置电池温度
         *
         * @param temperature 电池温度
         * @return Builder
         */
        public Builder setTemperature(int temperature) {
            this.temperature = temperature;
            return this;
        }

        /**
         * 设置电池健康状况
         *
         * @param health 电池健康状况
         * @return Builder
         */
        public Builder setHealth(int health) {
            this.health = health;
            return this;
        }

        /**
         * 设置电池充电状态
         *
         * @param pluggedIn 电池充电状态
         * @return Builder
         */
        public Builder setPluggedIn(int pluggedIn) {
            this.pluggedIn = pluggedIn;
            return this;
        }

        /**
         * build
         *
         * @return I007Battery
         */
        public I007Battery build() {
            return new I007Battery(level, status, temperature, health, pluggedIn);
        }
    }
}
