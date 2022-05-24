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
    private long factoryId;
    private String packageName;
    private Battery battery;

    public I007Result(long factoryId, String packageName, Battery battery) {
        this.factoryId = factoryId;
        this.packageName = packageName;
        this.battery = battery;
    }

    public long getFactoryId() {
        return factoryId;
    }

    public String getPackageName() {
        return packageName;
    }

    public Battery getBattery() {
        return battery;
    }

    protected I007Result(Parcel in) {
        factoryId = in.readLong();
        packageName = in.readString();
        battery = in.readParcelable(Battery.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(factoryId);
        dest.writeString(packageName);
        dest.writeParcelable(battery, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    @Override
    public String toString() {
        return "I007Result{" +
                "factoryId=" + factoryId +
                ", packageName='" + packageName + '\'' +
                ", battery=" + battery +
                '}';
    }

    public static class Builder {
        private long factoryId;
        private String packageName;
        private Battery battery;

        public Builder setFactoryId(long factoryId) {
            this.factoryId = factoryId;
            return this;
        }

        public Builder setPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder setBattery(Battery battery) {
            this.battery = battery;
            return this;
        }

        public I007Result build() {
            return new I007Result(factoryId, packageName, battery);
        }
    }
}
