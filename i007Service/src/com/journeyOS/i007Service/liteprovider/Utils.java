/*
 * Copyright (c) 2018 anqi.huang@outlook.com
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

package com.journeyOS.i007Service.liteprovider;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.Locale;

final class Utils {

    private Utils() {
        /* Utility classes must not have a public constructor */
    }

    static String getTableName(Class<?> clazz, Table table) {
        String value = table.value();
        if (TextUtils.isEmpty(value)) {
            return pluralize(clazz.getSimpleName());
        } else {
            return value;
        }
    }

    static String pluralize(String string) {
        string = string.toLowerCase(Locale.US);

//        if (string.endsWith("s")) {
//            return string;
//        } else if (string.endsWith("ay")) {
//            return string.replaceAll("ay$", "ays");
//        } else if (string.endsWith("ey")) {
//            return string.replaceAll("ey$", "eys");
//        } else if (string.endsWith("oy")) {
//            return string.replaceAll("oy$", "oys");
//        } else if (string.endsWith("uy")) {
//            return string.replaceAll("uy$", "uys");
//        } else if (string.endsWith("y")) {
//            return string.replaceAll("y$", "ies");
//        } else {
//            return string + "s";
//        }
        return string;
    }

    static String getColumnConstraint(Field field, Column column) throws IllegalAccessException {
        return field.get(null) + " " + column.value()
                + (column.primaryKey() ? " PRIMARY KEY" : "")
                + (column.notNull() ? " NOT NULL" : "")
                + (column.unique() ? " UNIQUE" : "");
    }

}
