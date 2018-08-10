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

package com.journeyOS.i007.config;

import android.content.Context;

import com.journeyOS.i007.base.util.SpUtils;
import com.journeyOS.i007.database.DatabaseManager;


public class AppConfig {
    public static void initialize(Context context) {
        initSharedPreference(context);
        initDatabase();
    }

    private static void initDatabase() {
        DatabaseManager.getDefault().init();
    }

    private static void initSharedPreference(Context context) {
        SpUtils.init(context);
    }
}
