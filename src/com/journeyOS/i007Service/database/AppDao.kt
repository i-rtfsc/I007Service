/*
 * Copyright (c) 2019 anqi.huang@outlook.com
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

package com.journeyOS.i007Service.database

import android.arch.persistence.room.*

@Dao
interface AppDao {

    @Query("SELECT * FROM " + DBConfigs.APP_TABLE)
    fun getApps(): List<App>

    @Query("SELECT * FROM " + DBConfigs.APP_TABLE + " WHERE " + DBConfigs.APP_PACKAGE_NAME + " = :packageName LIMIT 1")
    fun searchApp(packageName: String): App

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(app: App)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(apps: List<App>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(app: App)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(apps: List<App>)

    @Delete
    fun delete(app: App)

    @Delete
    fun delete(apps: List<App>)
}
