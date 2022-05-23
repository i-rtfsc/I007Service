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

package com.journeyOS.database.source.api

import com.journeyOS.database.source.local.app.App


interface LocalDataSource {
    fun put(key: String, defaultValue: Any)

    fun getString(key: String): String

    fun getString(key: String, defaultValue: String): String

    fun getInt(key: String): Int

    fun getInt(key: String, defaultValue: Int): Int

    fun getBoolean(key: String): Boolean

    fun getBoolean(key: String, defaultValue: Boolean): Boolean

    fun getFloat(key: String): Float

    fun getFloat(key: String, defaultValue: Float): Float

    fun getApp(packageName: String): App?

    fun getAllApps(): List<App?>?

    fun saveApp(app: App)
}