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

object DBConfigs {
    //database appName
    const val DB_NAME = "i007.db"
    //database version
    const val DB_VERSION = 1

    //table
    const val APP_TABLE = "dockConfigs"
    //Column
    const val APP_PACKAGE_NAME = "packageName"
    //Column
    const val APP_TYPE = "type"
    //Column
    const val APP_SUB_TYPE = "subType"
}
