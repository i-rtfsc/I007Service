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

package com.journeyOS.machinelearning.helpers;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * @author solo
 */
public class BitmapHelper {
    private static final String TAG = BitmapHelper.class.getSimpleName();

    /**
     * 图片转数组
     *
     * @param image 图片
     * @return float数组
     */
    public float[] loadRgbBitmapAsFloat(Bitmap image) {
        final int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getPixels(pixels, 0, image.getWidth(), 0, 0,
                image.getWidth(), image.getHeight());

        final float[] pixelsBatched = new float[pixels.length * 3];
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                final int idx = y * image.getWidth() + x;
                final int batchIdx = idx * 3;

                final float[] rgb = extractColorChannels(pixels[idx]);
                pixelsBatched[batchIdx] = rgb[0];
                pixelsBatched[batchIdx + 1] = rgb[1];
                pixelsBatched[batchIdx + 2] = rgb[2];
            }
        }
        return pixelsBatched;
    }

    /**
     * color bitmap to grayscale bitmap
     *
     * @param image 图片
     * @return float数组
     */
    public float[] loadGrayScaleBitmapAsFloat(Bitmap image) {
        final int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getPixels(pixels, 0, image.getWidth(), 0, 0,
                image.getWidth(), image.getHeight());

        final float[] pixelsBatched = new float[pixels.length];
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                final int idx = y * image.getWidth() + x;

                final int rgb = pixels[idx];
                final float b = ((rgb) & 0xFF);
                final float g = ((rgb >> 8) & 0xFF);
                final float r = ((rgb >> 16) & 0xFF);
                float grayscale = (float) (r * 0.3 + g * 0.59 + b * 0.11);

                pixelsBatched[idx] = preProcess(grayscale);
            }
        }
        return pixelsBatched;
    }

    /**
     * 缩放图片
     *
     * @param image     图片
     * @param newWidth  图片新的宽
     * @param newHeight 图片新的高
     * @return 缩放后的图片
     */
    public Bitmap getResizedBitmap(Bitmap image, int newWidth, int newHeight) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height, matrix, false);
        image.recycle();
        return resizedBitmap;
    }

    /**
     * 缩放图片
     *
     * @param source 图片
     * @param width  图片新的宽
     * @param height 图片新的高
     * @return 缩放后的图片
     */
    public Bitmap resizeBitmap(Bitmap source, int width, int height) {
        if (source.getHeight() == height && source.getWidth() == width) {
            return source;
        }
        int maxLength = Math.min(width, height);
        try {
            source = source.copy(source.getConfig(), true);
            if (source.getHeight() <= source.getWidth()) {
                /**
                 * if image already smaller than the required height
                 */
                if (source.getHeight() <= maxLength) {
                    return source;
                }

                double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                int targetWidth = (int) (maxLength * aspectRatio);

                return Bitmap.createScaledBitmap(source, targetWidth, maxLength, false);
            } else {
                /**
                 * if image already smaller than the required height
                 */
                if (source.getWidth() <= maxLength) {
                    return source;
                }

                double aspectRatio = ((double) source.getHeight()) / ((double) source.getWidth());
                int targetHeight = (int) (maxLength * aspectRatio);

                return Bitmap.createScaledBitmap(source, maxLength, targetHeight, false);
            }
        } catch (Exception e) {
            return source;
        }
    }

    private float[] extractColorChannels(int pixel) {

        float b = ((pixel) & 0xFF);
        float g = ((pixel >> 8) & 0xFF);
        float r = ((pixel >> 16) & 0xFF);

        return new float[]{preProcess(r), preProcess(g), preProcess(b)};
    }

    private float preProcess(float original) {
        return (original - 128) / 128;
    }

}
