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
public class I007Battery implements Parcelable {
    public static final Creator<I007Battery> CREATOR = new Creator<I007Battery>() {
        @Override
        public I007Battery createFromParcel(Parcel in) {
            return new I007Battery(in);
        }

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

    public I007Battery(int level, int status, int temperature, int health, int pluggedIn) {
        this.level = level;
        this.status = status;
        this.temperature = temperature;
        this.health = health;
        this.pluggedIn = pluggedIn;
    }

    protected I007Battery(Parcel in) {
        level = in.readInt();
        status = in.readInt();
        temperature = in.readInt();
        health = in.readInt();
        pluggedIn = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(level);
        dest.writeInt(status);
        dest.writeInt(temperature);
        dest.writeInt(health);
        dest.writeInt(pluggedIn);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    public int getLevel() {
        return level;
    }

    public int getStatus() {
        return status;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getHealth() {
        return health;
    }

    public int getPluggedIn() {
        return pluggedIn;
    }

    public static class Builder {
        private int level;
        private int status;
        private int temperature;
        private int health;
        private int pluggedIn;

        public Builder setLevel(int level) {
            this.level = level;
            return this;
        }

        public Builder setStatus(int status) {
            this.status = status;
            return this;
        }

        public Builder setTemperature(int temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder setHealth(int health) {
            this.health = health;
            return this;
        }

        public Builder setPluggedIn(int pluggedIn) {
            this.pluggedIn = pluggedIn;
            return this;
        }

        public I007Battery build() {
            return new I007Battery(level, status, temperature, health, pluggedIn);
        }
    }
}
