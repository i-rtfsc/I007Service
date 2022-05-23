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
@Entity(tableName = DBConfigs.Setting.TABLE_NAME, primaryKeys = [DBConfigs.Setting.KEY])
class Setting {
    /**
     * primary key
     */
    @ColumnInfo(name = DBConfigs.Setting.KEY)
    var key: String = ""

    /**
     * type
     */
    @ColumnInfo(name = DBConfigs.Setting.VALUE)
    var value: String? = null

    /**
     * type
     */
    @ColumnInfo(name = DBConfigs.Setting.TYPE)
    var type: String? = null

    @Ignore
    fun getBoolean(): Boolean {
        return if (Boolean::class.java.name == type) {
            java.lang.Boolean.parseBoolean(value)
        } else {
            throw IllegalStateException("settings is $type")
        }
    }

    @Ignore
    fun getInt(): Int {
        return if (Int::class.java.name == type) {
            value!!.toInt()
        } else {
            throw java.lang.IllegalStateException("settings is $type")
        }
    }

    @Ignore
    fun getString(): String? {
        return if (String::class.java.name == type) {
            value
        } else {
            throw java.lang.IllegalStateException("settings is $type")
        }
    }

    @Ignore
    fun getFloat(): Float {
        return if (Float::class.java.name == type) {
            value!!.toFloat()
        } else {
            throw java.lang.IllegalStateException("settings is $type")
        }
    }

    @Ignore
    override fun toString(): String {
        return "Setting(key='$key', value=$value, type=$type)"
    }

}