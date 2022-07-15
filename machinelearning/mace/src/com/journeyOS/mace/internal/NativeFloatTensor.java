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

package com.journeyOS.mace.internal;

import com.journeyOS.i007manager.SmartLog;
import com.journeyOS.mace.core.FloatTensor;

import java.nio.FloatBuffer;

/**
 * @author solo
 */
public class NativeFloatTensor extends FloatTensor {
    private final String TAG = NativeFloatTensor.class.getSimpleName();

    private FloatBuffer floatBuffer;
    private int capacity;

    /**
     * 构造函数
     *
     * @param shape 输入、输出 shape
     */
    public NativeFloatTensor(int[] shape) {
        super(shape);
        capacity = 1;
        for (int i : shape) {
            capacity *= i;
        }
        floatBuffer = FloatBuffer.allocate(capacity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(float[] floats, int startPos, int length) {
        if (length > capacity || (startPos + length) > capacity || startPos < 0) {
            SmartLog.e(TAG, "illegal argument with write, startPos = [" + startPos + "], length = [" + startPos + "], capacity = [" + capacity + "]");
            return;
        }
        floatBuffer.rewind();
        floatBuffer.put(floats, startPos, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(float[] floats, int startPos, int length) {
        if (length > capacity || startPos < 0) {
            SmartLog.e(TAG, "illegal argument with read, startPos = [" + startPos + "], length = [" + startPos + "], capacity = [" + capacity + "]");
            return -1;
        }
        floatBuffer.rewind();
        floatBuffer.get(floats, startPos, length);
        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release() {
        floatBuffer.clear();
        floatBuffer = null;
    }
}
