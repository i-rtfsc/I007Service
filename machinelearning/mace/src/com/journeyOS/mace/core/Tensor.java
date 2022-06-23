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

package com.journeyOS.mace.core;

/**
 * @author solo
 */
public abstract class Tensor {
    private final int[] mShape;

    /**
     * shape转成int数组
     *
     * @param shape 输入、输出shape
     */
    protected Tensor(int... shape) {
        mShape = new int[shape.length];
        for (int i = 0; i < shape.length; i++) {
            mShape[i] = shape[i];
        }
    }

    /**
     * 获取shape
     *
     * @return int数组
     */
    public int[] getShape() {
        return mShape;
    }

    /**
     * 获取shape size
     *
     * @return
     */
    public int getSize() {
        int size = 1;
        for (int dim : mShape) {
            size *= dim;
        }
        return size;
    }

    /**
     * 写
     *
     * @param floats   float数组
     * @param startPos 起始位置
     * @param length   长度
     */
    public abstract void write(float[] floats, int startPos, int length);

    /**
     * 读
     *
     * @param floats   float数组
     * @param startPos 起始位置
     * @param length   长度
     */
    public abstract int read(float[] floats, int startPos, int length);

    /**
     * 释放资源
     */
    public abstract void release();

}
