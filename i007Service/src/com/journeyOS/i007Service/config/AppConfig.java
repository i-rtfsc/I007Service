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

package com.journeyOS.i007Service.config;

import android.app.Application;
import android.content.Context;

import com.journeyOS.i007Service.base.utils.SpUtils;
import com.journeyOS.i007Service.core.I007Core;
import com.journeyOS.i007Service.database.DatabaseManager;


public class AppConfig {
    private static Application sContext = null;

    public static void initialize() {
        if (!I007Core.getCore().isRunning()) {
            throw new IllegalStateException("I007 Core has not been run!");
        }
        sContext = I007Core.getCore().getContext();

        initSharedPreference(sContext);
        initDatabase();
    }

    private static void initDatabase() {
        DatabaseManager.getDefault().init();
    }

    private static void initSharedPreference(Context context) {
        SpUtils.init(context);
    }
}
