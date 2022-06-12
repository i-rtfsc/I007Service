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

package com.journeyOS.common.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Json JsonHelper/Deserializer.
 *
 * @author solo
 */
public class JsonHelper {

    private static final Gson GSON = new Gson();

    JsonHelper() {
    }

    /**
     * 对象转json字符串
     *
     * @param object 对象
     * @return json字符串
     */
    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    /**
     * 对象转json字符串
     *
     * @param object 对象
     * @param type   对象的类型
     * @return json字符串
     */
    public static String toJson(Object object, Type type) {
        return GSON.toJson(object, type);
    }

    /**
     * json字符串转对象
     *
     * @param string json字符串
     * @param clazz  需要转成的对象
     * @param <T>    T
     * @return 对象
     */
    public static <T> T fromJson(String string, Class<T> clazz) {
        return GSON.fromJson(string, clazz);
    }

    /**
     * json字符串转对象
     *
     * @param string json字符串
     * @param type   需要转成的对象类型
     * @param <T>    T
     * @return 对象类型
     */
    public static <T> T fromJson(String string, Type type) {
        return GSON.fromJson(string, type);
    }
}