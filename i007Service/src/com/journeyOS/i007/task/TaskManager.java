/*
 * Copyright (c) 2018 anqi.huang@outlook.com
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

package com.journeyOS.i007.task;

import com.journeyOS.i007.base.util.Singleton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskManager {
    public static final String HANDLER_CLIENT_SESSION = "ClientSession";
    public static final String IO_THREAD = "FixedThread";

//    private static final int MAX_THREADS = 3;
//    private static final Singleton<TaskManager> gDefault = new Singleton<TaskManager>() {
//        @Override
//        protected TaskManager create() {
//            return new TaskManager();
//        }
//    };
//    private ExecutorService mThreadPool = Executors.newFixedThreadPool(MAX_THREADS);
//
//    private TaskManager() {
//    }
//
//    public static TaskManager getDefault() {
//        return gDefault.get();
//    }
//
//    public void submit(Runnable r) {
//        if (mThreadPool != null) {
//            mThreadPool.submit(r);
//        }
//    }
}
