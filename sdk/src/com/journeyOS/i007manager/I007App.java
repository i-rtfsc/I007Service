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
public class I007App implements Parcelable {

    public static final Creator<I007App> CREATOR = new Creator<I007App>() {
        @Override
        public I007App createFromParcel(Parcel in) {
            return new I007App(in);
        }

        @Override
        public I007App[] newArray(int size) {
            return new I007App[size];
        }
    };
    private int type;
    private String packageName;
    private String activity;

    public I007App(int type, String packageName, String activity) {
        this.type = type;
        this.packageName = packageName;
        this.activity = activity;
    }

    protected I007App(Parcel in) {
        type = in.readInt();
        packageName = in.readString();
        activity = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(packageName);
        dest.writeString(activity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "I007App{" +
                "type=" + type +
                ", packageName='" + packageName + '\'' +
                ", activity='" + activity + '\'' +
                '}';
    }

    public int getType() {
        return type;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getActivity() {
        return activity;
    }

    public static class Type {
        public static final int DEFAULT = 0;
        public static final int ALBUM = 1;
        public static final int BROWSER = 2;
        public static final int GAME = 3;
        public static final int IM = 4;
        public static final int MUSIC = 5;
        public static final int NEWS = 6;
        public static final int READER = 7;
        public static final int VIDEO = 8;
    }

    public static class Builder {
        private int type = Type.DEFAULT;
        private String packageName;
        private String activity;

        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        public Builder setPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder setActivity(String activity) {
            this.activity = activity;
            return this;
        }

        public I007App build() {
            return new I007App(type, packageName, activity);
        }
    }
}
