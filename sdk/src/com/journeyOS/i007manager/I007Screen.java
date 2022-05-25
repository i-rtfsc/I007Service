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
public class I007Screen implements Parcelable {
    public static final Creator<I007Screen> CREATOR = new Creator<I007Screen>() {
        @Override
        public I007Screen createFromParcel(Parcel in) {
            return new I007Screen(in);
        }

        @Override
        public I007Screen[] newArray(int size) {
            return new I007Screen[size];
        }
    };

    private int status;

    public I007Screen(int status) {
        this.status = status;
    }

    protected I007Screen(Parcel in) {
        status = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "I007Screen{" +
                "status=" + status +
                '}';
    }

    public int getStatus() {
        return status;
    }

    public static class Status {
        public static final int UNKNOWN = -1;
        public static final int ON = 1;
        public static final int OFF = 0;
    }

    public static class Builder {
        private int status = Status.UNKNOWN;

        public Builder setStatus(int status) {
            this.status = status;
            return this;
        }

        public I007Screen build() {
            return new I007Screen(status);
        }
    }
}
