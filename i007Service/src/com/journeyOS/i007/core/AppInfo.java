/*
 * Copyright (c) 2018 anqi.huang@outlook.com
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

package com.journeyOS.i007.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.journeyOS.i007.I007Manager;


public class AppInfo implements Parcelable {
    public long factorId;
    public long state = I007Manager.SCENE_FACTOR_APP_STATE_DEFAULT;
    public String packageName = "";

    public AppInfo() {
    }

    public AppInfo(long factorId, long state, String packageName) {
        this.factorId = factorId;
        this.state = state;
        this.packageName = packageName;
    }

    public AppInfo(AppInfo appInfo) {
        this.factorId = appInfo.factorId;
        this.state = appInfo.state;
        this.packageName = appInfo.packageName;
    }

    protected AppInfo(Parcel in) {
        this.factorId = in.readLong();
        this.state = in.readLong();
        this.packageName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.factorId);
        dest.writeLong(this.state);
        dest.writeString(this.packageName);
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel source) {
            return new AppInfo(source);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };
}
