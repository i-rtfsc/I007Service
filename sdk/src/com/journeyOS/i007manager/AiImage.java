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

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 图片
 *
 * @author solo
 */
public class AiImage implements Parcelable {

    public static final Creator<AiImage> CREATOR
            = new Creator<AiImage>() {
        /**
         * Rebuilds a bitmap previously stored with writeToParcel().
         *
         * @param p    Parcel object to read the bitmap from
         * @return a new bitmap created from the data in the parcel
         */
        @Override
        public AiImage createFromParcel(Parcel p) {
            AiImage image = new AiImage(p);
            if (image == null) {
                throw new RuntimeException("Failed to unparcel Image");
            }
            return image;
        }

        public AiImage[] newArray(int size) {
            return new AiImage[size];
        }
    };
    protected int[] pixels;
    protected int height;
    protected int width;

    private AiImage() {
    }

    /**
     * 构造函数
     *
     * @param pixels 图片的像素值
     * @param height 图片的高
     * @param width  图片的宽
     */
    public AiImage(int[] pixels, int height, int width) {
        this.pixels = pixels;
        this.height = height;
        this.width = width;
    }

    /**
     * 构造函数
     *
     * @param bitmap 图片
     */
    public AiImage(Bitmap bitmap) {
        this.pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        this.height = bitmap.getHeight();
        this.width = bitmap.getWidth();
    }

    protected AiImage(Parcel in) {
        pixels = in.createIntArray();
        height = in.readInt();
        width = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(pixels);
        dest.writeInt(height);
        dest.writeInt(width);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public AiImage resizeTo(int width, int height) {
        Bitmap bm = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565);
        Bitmap bmScaled = Bitmap.createScaledBitmap(bm, width, height, false);
        int intValues[] = new int[width * height];
        bmScaled.getPixels(intValues, 0, width, 0, 0, width, height);
        AiImage ret = new AiImage(intValues, height, width);
        bm.recycle();
        bmScaled.recycle();
        return ret;
    }

    public Bitmap dumpToBitmap() {
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    /**
     * 获取图像的像素
     *
     * @return 像素
     */
    public int[] getPixels() {
        return pixels;
    }

    /**
     * 获取图像的高
     *
     * @return 高
     */
    public int getHeight() {
        return height;
    }

    /**
     * 获取图像的宽
     *
     * @return 宽
     */
    public int getWidth() {
        return width;
    }

    /**
     * Builder
     */
    public static class Builder {
        private Bitmap bitmap;

        /**
         * 设置图像
         *
         * @param bitmap 图像
         * @return Builder
         */
        public Builder setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            return this;
        }


        /**
         * 构建
         *
         * @return AiImage
         */
        public AiImage build() {
            return new AiImage(bitmap);
        }
    }
}
