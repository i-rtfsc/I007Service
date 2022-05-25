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

package com.journeyOS.i007Service.hook.compat;

import com.journeyOS.i007Service.base.utils.ReflectUtils;

import java.util.Map;

public class SystemServiceRegistryCompat {
    private static Class sClass = null;
    private static boolean foundClassSystemServiceRegistry = false;

    public static Class Class() throws ClassNotFoundException {
        if (sClass == null) {
            try {
                sClass = Class.forName("android.app.SystemServiceRegistry");
                foundClassSystemServiceRegistry = true;
            } catch (Exception e) {
                sClass = Class.forName("android.app.ContextImpl");
                foundClassSystemServiceRegistry = false;
            }
        }
        return sClass;
    }

    public static Object getSystemFetcher(String serviceName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Object serviceFetchers;
        Class aClass = Class();
        if (foundClassSystemServiceRegistry) {
            serviceFetchers = ReflectUtils.getStaticFiled(aClass, "SYSTEM_SERVICE_FETCHERS");
        } else {
            serviceFetchers = ReflectUtils.getStaticFiled(aClass, "SYSTEM_SERVICE_MAP");
        }
        if (serviceFetchers instanceof Map) {
            Map fetcherMap = (Map) serviceFetchers;
            return fetcherMap.get(serviceName);
        }
        return null;
    }
}
