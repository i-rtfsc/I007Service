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

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = DBConfigs.APP_TABLE, primaryKeys = [DBConfigs.APP_PACKAGE_NAME])
class App {
    @ColumnInfo(name = DBConfigs.APP_PACKAGE_NAME)
    var packageName = ""


    @ColumnInfo(name = DBConfigs.APP_TYPE)
    var type: String? = null


    @ColumnInfo(name = DBConfigs.APP_SUB_TYPE)
    var subType: String? = null
}