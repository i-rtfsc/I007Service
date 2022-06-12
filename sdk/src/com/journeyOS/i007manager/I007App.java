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

    /**
     * Creator
     */
    public static final Creator<I007App> CREATOR = new Creator<I007App>() {
        /**
         * {@inheritDoc}
         */
        @Override
        public I007App createFromParcel(Parcel in) {
            return new I007App(in);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public I007App[] newArray(int size) {
            return new I007App[size];
        }
    };
    private int type;
    private String packageName;
    private String activity;

    /**
     * 构造函数
     *
     * @param type        app类型
     * @param packageName app的包名
     * @param activity    app的activity名
     */
    public I007App(int type, String packageName, String activity) {
        this.type = type;
        this.packageName = packageName;
        this.activity = activity;
    }

    /**
     * Parcel
     *
     * @param in Parcel
     */
    protected I007App(Parcel in) {
        type = in.readInt();
        packageName = in.readString();
        activity = in.readString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(packageName);
        dest.writeString(activity);
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
        return "I007App{" +
                "type=" + type +
                ", packageName='" + packageName + '\'' +
                ", activity='" + activity + '\'' +
                '}';
    }

    /**
     * 获取app类型
     *
     * @return app类型
     */
    public int getType() {
        return type;
    }

    /**
     * 获取app包名
     *
     * @return app包名
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * 获取app activity名
     *
     * @return app activity名
     */
    public String getActivity() {
        return activity;
    }

    /**
     * app 类型
     */
    public static class Type {
        /**
         * 默认类型
         */
        public static final int DEFAULT = 0;

        /**
         * 相册
         */
        public static final int ALBUM = 1;

        /**
         * 浏览器
         */
        public static final int BROWSER = 2;

        /**
         * 游戏
         */
        public static final int GAME = 3;

        /**
         * 聊天
         */
        public static final int IM = 4;

        /**
         * 音乐
         */
        public static final int MUSIC = 5;

        /**
         * 新闻
         */
        public static final int NEWS = 6;

        /**
         * 阅读/读书
         */
        public static final int READER = 7;

        /**
         * 视频
         */
        public static final int VIDEO = 8;
    }

    /**
     * builder
     */
    public static class Builder {
        private int type = Type.DEFAULT;
        private String packageName;
        private String activity;

        /**
         * 设置app类型
         *
         * @param type app类型
         * @return Builder
         */
        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        /**
         * 设置app包名
         *
         * @param packageName app包名
         * @return Builder
         */
        public Builder setPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        /**
         * 设置app activity名
         *
         * @param activity app activity名
         * @return Builder
         */
        public Builder setActivity(String activity) {
            this.activity = activity;
            return this;
        }

        /**
         * build
         *
         * @return I007App
         */
        public I007App build() {
            return new I007App(type, packageName, activity);
        }
    }
}
