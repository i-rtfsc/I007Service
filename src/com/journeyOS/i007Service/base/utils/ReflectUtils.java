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

package com.journeyOS.i007Service.base.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class ReflectUtils {
    public static void setStaticFiled(Class<?> type, String filedName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        setFiled(type, filedName, null, value);
    }

    public static void setFiled(Class<?> type, String filedName, Object object, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = type.getDeclaredField(filedName);
        declaredField.setAccessible(true);
        declaredField.set(object, value);
    }

    public static Object getStaticFiled(Class<?> type, String filedName)
            throws NoSuchFieldException, IllegalAccessException {
        return getFiled(type, filedName, null);
    }

    public static Object getFiled(Class<?> type, String filedName, Object object)
            throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = type.getDeclaredField(filedName);
        declaredField.setAccessible(true);
        return declaredField.get(object);
    }

    public static Object invokeStaticMethod(Class<?> type, String method, Class<?>[] paramsTypes, Object[] args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(type, method, paramsTypes, null, args);
    }

    public static Object invokeMethod(Class<?> type, String method, Class<?>[] paramsTypes, Object receiver, Object[] args)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method declaredMethod = type.getDeclaredMethod(method, paramsTypes);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(receiver, args);
    }

    public static Object makeProxy(ClassLoader loader, Class<?> base, InvocationHandler handler) {
        List<Class<?>> interfaces = getAllInterfaces(base);
        Class[] ifs = interfaces != null && interfaces.size() > 0 ? interfaces.toArray(new Class[interfaces.size()]) : new Class[0];
        return Proxy.newProxyInstance(loader, ifs, handler);
    }

    public static Object makeProxy(ClassLoader loader, Class<?>[] ifs, InvocationHandler handler) {
        return Proxy.newProxyInstance(loader, ifs, handler);
    }

    public static List<Class<?>> getAllInterfaces(final Class<?> cls) {
        if (cls == null) {
            return null;
        }
        final LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<>();
        getAllInterfaces(cls, interfacesFound);
        return new ArrayList<>(interfacesFound);
    }

    private static void getAllInterfaces(Class<?> cls, final HashSet<Class<?>> interfacesFound) {
        while (cls != null) {
            final Class<?>[] interfaces = cls.getInterfaces();

            for (final Class<?> i : interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }

            cls = cls.getSuperclass();
        }
    }
}
