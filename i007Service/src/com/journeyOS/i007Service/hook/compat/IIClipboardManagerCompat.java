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

import android.os.IBinder;

import com.journeyOS.i007Service.base.utils.ReflectUtils;

import java.lang.reflect.InvocationTargetException;

public class IIClipboardManagerCompat {
    //http://androidxref.com/9.0.0_r3/xref/frameworks/base/core/java/android/content/IClipboard.aidl
    public static final String METHOD_SET_PRIMARY_CLIP = "setPrimaryClip";
    public static final String METHOD_CLEAR_PRIMARY_CLIP = "clearPrimaryClip";
    public static final String METHOD_GET_PRIMARY_CLIP = "getPrimaryClip";
    public static final String METHOD_GET_PRIMARY_CLIP_DESCRIPTION = "getPrimaryClipDescription";
    public static final String METHOD_HAS_PRIMARY_CLIP = "hasPrimaryClip";
    public static final String METHOD_HAS_CLIPBOARD_TEXT = "hasClipboardText";

    private static Class sClass;

    public static Class Class() throws ClassNotFoundException {
        if (sClass == null) {
            sClass = Class.forName("android.content.IClipboard");
        }
        return sClass;
    }

    public static Object asInterface(IBinder binder) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class clazz = Class.forName("android.content.IClipboard$Stub");
        return ReflectUtils.invokeStaticMethod(clazz, "asInterface", new Class[]{IBinder.class}, new Object[]{binder});
    }
}
