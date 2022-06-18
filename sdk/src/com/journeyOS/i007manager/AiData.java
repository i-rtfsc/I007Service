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
 * 模型的信息
 *
 * @author solo
 */
public class AiData implements Parcelable {

    public static final Creator<AiData> CREATOR = new Creator<AiData>() {
        @Override
        public AiData createFromParcel(Parcel in) {
            return new AiData(in);
        }

        @Override
        public AiData[] newArray(int size) {
            return new AiData[size];
        }
    };

    /**
     * 用channel id来区分是哪一条信息
     */
    private int channel;

    /**
     * 需要识别的文字（对应的类型必须text）
     */
    private String text;

    /**
     * 需要识别的图像（对应的类型必须image）
     */
    private AiImage image;

    /**
     * 构造函数
     *
     * @param channel 区分是哪一条信息
     * @param text    需要识别的文字（对应的类型必须text）
     * @param image   需要识别的图像（对应的类型必须image）
     */
    public AiData(int channel, String text, AiImage image) {
        this.channel = channel;
        this.text = text;
        this.image = image;
    }

    protected AiData(Parcel in) {
        channel = in.readInt();
        text = in.readString();
        image = in.readParcelable(AiImage.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(channel);
        dest.writeString(text);
        dest.writeParcelable(image, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 用channel id来区分是哪一条信息
     *
     * @return id
     */
    public int getChannel() {
        return channel;
    }

    /**
     * 需要识别的文字
     *
     * @return 文字
     */
    public String getText() {
        return text;
    }

    /**
     * 需要识别的图像
     *
     * @return 图像
     */
    public AiImage getImage() {
        return image;
    }


    /**
     * builder
     */
    public static class Builder {
        /**
         * 用channel id来区分是哪一条信息
         */
        private int channel;

        /**
         * 需要识别的文字
         */
        private String text;

        /**
         * 需要识别的图像
         */
        private AiImage image;

        /**
         * 设置 channel id
         *
         * @param channel channel id
         * @return Builder
         */
        public Builder setChannel(int channel) {
            this.channel = channel;
            return this;
        }

        /**
         * 设置需要识别的文字
         *
         * @param text 需要识别的文字
         * @return Builder
         */
        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        /**
         * 设置需要识别的图像
         *
         * @param image 需要识别的图像
         * @return Builder
         */
        public Builder setImage(AiImage image) {
            this.image = image;
            return this;
        }

        /**
         * build
         *
         * @return AiData
         */
        public AiData build() {
            return new AiData(channel, text, image);
        }
    }

}
