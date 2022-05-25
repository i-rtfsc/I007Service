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

package com.journeyOS.common.task;

import com.journeyOS.common.utils.Singleton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TaskManager
 *
 * @author solo
 */
public final class TaskManager {
    private static final int MAX_THREADS = 3;
    private static final Singleton<TaskManager> SINGLETON = new Singleton<TaskManager>() {
        @Override
        protected TaskManager create() {
            return new TaskManager();
        }
    };
    private ExecutorService mThreadPool = Executors.newFixedThreadPool(MAX_THREADS);

    private TaskManager() {
    }

    /**
     * get TaskManager
     *
     * @return TaskManager
     */
    public static TaskManager getDefault() {
        return SINGLETON.get();
    }

    /**
     * submit
     *
     * @param r Runnable
     */
    public void submit(Runnable r) {
        if (mThreadPool != null) {
            mThreadPool.submit(r);
        }
    }
}
