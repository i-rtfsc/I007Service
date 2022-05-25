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

    public static final Creator<I007Result> CREATOR = new Creator<I007Result>() {
        @Override
        public I007Result createFromParcel(Parcel in) {
            return new I007Result(in);
        }

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

    public I007Result(long factoryId, I007App app, I007Net net, I007Battery battery, I007Screen screen) {
        this.factoryId = factoryId;
        this.app = app;
        this.net = net;
        this.battery = battery;
        this.screen = screen;
    }

    protected I007Result(Parcel in) {
        factoryId = in.readLong();
        app = in.readParcelable(I007App.class.getClassLoader());
        net = in.readParcelable(I007Net.class.getClassLoader());
        battery = in.readParcelable(I007Battery.class.getClassLoader());
        screen = in.readParcelable(I007Screen.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(factoryId);
        dest.writeParcelable(app, flags);
        dest.writeParcelable(net, flags);
        dest.writeParcelable(battery, flags);
        dest.writeParcelable(screen, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public long getFactoryId() {
        return factoryId;
    }

    public I007App getApp() {
        return app;
    }

    public I007Net getNet() {
        return net;
    }

    public I007Battery getBattery() {
        return battery;
    }

    public I007Screen getScreen() {
        return screen;
    }

    @Override
    public String toString() {
        return "I007Result{" +
                "factoryId=" + factoryId +
                ", app=" + app +
                ", net=" + net +
                ", battery=" + battery +
                ", screen=" + screen +
                '}';
    }

    public static class Builder {
        private long factoryId;
        private I007App app;
        private I007Net net;
        private I007Battery battery;
        private I007Screen screen;

        public Builder setFactoryId(long factoryId) {
            this.factoryId = factoryId;
            return this;
        }

        public Builder setApp(I007App app) {
            this.app = app;
            return this;
        }

        public Builder setNet(I007Net net) {
            this.net = net;
            return this;
        }

        public Builder setBattery(I007Battery battery) {
            this.battery = battery;
            return this;
        }

        public Builder setScreen(I007Screen screen) {
            this.screen = screen;
            return this;
        }

        public I007Result build() {
            return new I007Result(factoryId, app, net, battery, screen);
        }
    }
}
