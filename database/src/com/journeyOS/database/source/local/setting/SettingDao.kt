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

package com.journeyOS.database.source.local.app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.journeyOS.database.source.local.base.DBConfigs

@Dao
interface SettingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSetting(setting: Setting)

    @Query("SELECT * FROM " + DBConfigs.Setting.TABLE_NAME + " WHERE " + DBConfigs.Setting.KEY + " LIKE :key")
    fun getSetting(key: String): Setting

    @Query("DELETE FROM " + DBConfigs.Setting.TABLE_NAME.toString() + " WHERE " + DBConfigs.Setting.KEY + " LIKE :key")
    fun deleteSetting(key: String)
}