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

package com.journeyOS.i007Service;

import android.app.Application;

import com.journeyOS.i007manager.I007Core;

/**
 * @author solo
 */
public class I007Application extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        I007Core.getCore().startup(this);
        AppConfig.getInstance().initialize(this);
    }
}
