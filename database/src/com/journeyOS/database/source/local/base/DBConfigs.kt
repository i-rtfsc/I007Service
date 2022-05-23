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

package com.journeyOS.database.source.local.base

/**
 * database configs
 *
 * @author solo
 */
object DBConfigs {
    /**
     * database name
     */
    const val DB_NAME = "i007.db"

    /**
     * database version
     */
    const val DB_VERSION = 1

    object App {
        //table
        const val TABLE_NAME = "apps"

        //column
        const val PACKAGE_NAME = "packageName"

        //column
        const val TYPE = "type"

        //column
        const val SUB_TYPE = "subType"
    }


    object Setting {
        //table
        const val TABLE_NAME = "settings"

        //column
        const val KEY = "key"

        //column
        const val VALUE = "value"

        //column
        const val TYPE = "type"
    }
}