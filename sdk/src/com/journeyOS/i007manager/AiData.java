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
    /**
     * 文字
     */
    public static final int TEXT = 0x1;

    /**
     * 图像
     */
    public static final int IMAGE = 0x2;
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
     * 识别的类型（文字、图像）
     */
    private int type;
    /**
     * 需要识别的文字（对应的类型必须text）
     */
    private String word;
    /**
     * 需要识别的图像（对应的类型必须image）
     */
    private AiImage image;

    /**
     * 构造函数
     *
     * @param channel 区分是哪一条信息
     * @param type    识别的类型
     * @param word    需要识别的文字（对应的类型必须text）
     * @param image   需要识别的图像（对应的类型必须image）
     */
    public AiData(int channel, int type, String word, AiImage image) {
        this.channel = channel;
        this.type = type;
        this.word = word;
        this.image = image;
    }

    protected AiData(Parcel in) {
        channel = in.readInt();
        type = in.readInt();
        word = in.readString();
        image = in.readParcelable(AiImage.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(channel);
        dest.writeInt(type);
        dest.writeString(word);
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
     * 识别的类型（文字、图像）
     *
     * @return 类型
     */
    public int getType() {
        return type;
    }

    /**
     * 需要识别的文字（对应的类型必须text）
     *
     * @return 文字
     */
    public String getWord() {
        return word;
    }

    /**
     * 需要识别的图像（对应的类型必须image）
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
         * 识别的类型（文字、图像）
         */
        private int type;

        /**
         * 需要识别的文字（对应的类型必须text）
         */
        private String word;

        /**
         * 需要识别的图像（对应的类型必须image）
         */
        private AiImage image;

        public Builder setChannel(int channel) {
            this.channel = channel;
            return this;
        }

        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        public Builder setWord(String word) {
            this.word = word;
            return this;
        }

        public Builder setImage(AiImage image) {
            this.image = image;
            return this;
        }

        /**
         * build
         *
         * @return I007App
         */
        public AiData build() {
            return new AiData(channel, type, word, image);
        }
    }

}
