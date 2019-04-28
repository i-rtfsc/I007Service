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

package com.journeyOS.i007Service.database;

import android.os.Parcel;
import android.os.Parcelable;

public class App implements Parcelable {
    public static final Parcelable.Creator<App> CREATOR = new Parcelable.Creator<App>() {
        @Override
        public App createFromParcel(Parcel source) {
            return new App(source);
        }

        @Override
        public App[] newArray(int size) {
            return new App[size];
        }
    };
    public String packageName;
    public String type;
    public String subType;

    @Override
    public String toString() {
        return "App{" +
                "packageName='" + packageName + '\'' +
                ", type='" + type + '\'' +
//                ", subType='" + subType + '\'' +
                '}';
    }

    public App() {
    }

    protected App(Parcel in) {
        this.packageName = in.readString();
        this.type = in.readString();
        this.subType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.packageName);
        dest.writeString(this.type);
        dest.writeString(this.subType);
    }
}
