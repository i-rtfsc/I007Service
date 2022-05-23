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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import com.journeyOS.database.source.local.base.DBConfigs

/**
 * app entity
 *
 * @author solo
 */
@Entity(tableName = DBConfigs.App.TABLE_NAME, primaryKeys = [DBConfigs.App.PACKAGE_NAME])
class App {
    /**
     * primary key
     */
    @ColumnInfo(name = DBConfigs.App.PACKAGE_NAME)
    var packageName = ""

    /**
     * type
     */
    @ColumnInfo(name = DBConfigs.App.TYPE)
    var type: String? = null

    /**
     * sub type
     */
    @ColumnInfo(name = DBConfigs.App.SUB_TYPE)
    var subType: String? = null

    @Ignore
    override fun toString(): String {
        return "App{" +
                "packageName='" + packageName + '\'' +
                ", type='" + type + '\'' +
                ", subType='" + subType + '\'' +
                '}'
    }
}