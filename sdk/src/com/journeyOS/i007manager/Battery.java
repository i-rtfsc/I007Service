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
public class Battery implements Parcelable {
    public static final Creator<Battery> CREATOR = new Creator<Battery>() {
        @Override
        public Battery createFromParcel(Parcel in) {
            return new Battery(in);
        }

        @Override
        public Battery[] newArray(int size) {
            return new Battery[size];
        }
    };
    private int status;
    private int level;
    private int temperature;
    private int pluggedIn;

    public Battery(int status, int level, int temperature, int pluggedIn) {
        this.status = status;
        this.level = level;
        this.temperature = temperature;
        this.pluggedIn = pluggedIn;
    }

    protected Battery(Parcel in) {
        status = in.readInt();
        level = in.readInt();
        temperature = in.readInt();
        pluggedIn = in.readInt();
    }

    public int getStatus() {
        return status;
    }

    public int getLevel() {
        return level;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getPluggedIn() {
        return pluggedIn;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeInt(level);
        dest.writeInt(temperature);
        dest.writeInt(pluggedIn);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static class Builder {
        private int status;
        private int level;
        private int temperature;
        private int pluggedIn;

        public Builder setStatus(int status) {
            this.status = status;
            return this;
        }

        public Builder setLevel(int level) {
            this.level = level;
            return this;
        }

        public Builder setTemperature(int temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder setPluggedIn(int pluggedIn) {
            this.pluggedIn = pluggedIn;
            return this;
        }

        public Battery build() {
            return new Battery(status, level, temperature, pluggedIn);
        }
    }
}
