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

package com.journeyOS.database.source.local.setting;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.journeyOS.database.source.local.base.DBConfigs;

/**
 * setting entity
 *
 * @author solo
 */
@Entity(tableName = DBConfigs.Setting.TABLE_NAME, primaryKeys = {DBConfigs.Setting.KEY})
public class Setting {

    /**
     * primary key
     */
    @NonNull
    @ColumnInfo(name = DBConfigs.Setting.KEY)
    private String key = "";

    @ColumnInfo(name = DBConfigs.Setting.VALUE)
    private String value;

    @ColumnInfo(name = DBConfigs.Setting.OBJECT)
    private String object;

    /**
     * 获取key值
     *
     * @return key值
     */
    @NonNull
    public String getKey() {
        return key;
    }

    /**
     * 设置key
     *
     * @param key key值
     */
    public void setKey(@NonNull String key) {
        this.key = key;
    }

    /**
     * 获取value值
     *
     * @return value值
     */
    public String getValue() {
        return value;
    }

    /**
     * 设置value值
     *
     * @param value value值
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 获取object值
     *
     * @return object值
     */
    public String getObject() {
        return object;
    }

    /**
     * 获取object值
     *
     * @param object object值
     */
    public void setObject(String object) {
        this.object = object;
    }

    /**
     * 获取类型为Boolean的value值
     *
     * @return value值
     */
    @Ignore
    public boolean getBoolean() {
        if (Boolean.class.getName().equals(object)) {
            return Boolean.parseBoolean(value);
        } else {
            throw new IllegalStateException("(getBoolean)setting is " + object);
        }
    }

    /**
     * 获取类型为Integer的value值
     *
     * @return value值
     */
    @Ignore
    public int getInt() {
        if (Integer.class.getName().equals(object)) {
            return (Integer) Integer.parseInt(value);
        } else {
            throw new IllegalStateException("(getInt)setting is " + object);
        }
    }

    /**
     * 获取类型为String的value值
     *
     * @return value值
     */
    @Ignore
    public String getString() {
        if (String.class.getName().equals(object)) {
            return (String) value;
        } else {
            throw new IllegalStateException("(getString)setting is " + object);
        }
    }

    /**
     * 获取类型为Float的value值
     *
     * @return value值
     */
    @Ignore
    public float getFloat() {
        if (Float.class.getName().equals(object)) {
            return Float.parseFloat(value);
        } else {
            throw new IllegalStateException("(getFloat)setting is " + object);
        }
    }
}
