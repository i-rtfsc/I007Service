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

package com.journeyOS.machinelearning.tasks;

/**
 * TaskResult
 *
 * @param <T> T class
 */
public class TaskResult<T> {
    private int channel;
    private T result;

    /**
     * TaskResult
     *
     * @param result T
     */
    public TaskResult(T result) {
        this.channel = -1;
        this.result = result;
    }

    /**
     * TaskResult
     *
     * @param channel channel
     * @param result  result
     */
    public TaskResult(int channel, T result) {
        this.channel = channel;
        this.result = result;
    }

    /**
     * channel
     *
     * @return channel
     */
    public int getChannel() {
        return channel;
    }

    /**
     * result
     *
     * @return result
     */
    public T getResult() {
        return result;
    }

}
