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

package com.journeyOS.i007Service.hook;

import android.content.Context;

import com.journeyOS.i007Service.base.utils.ReflectUtils;
import com.journeyOS.i007Service.hook.compat.IIActivityManagerCompat;
import com.journeyOS.i007Service.hook.listeners.MethodInvokeListener;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ActivityManagerHook extends Hook implements InvocationHandler {
    private static final String TAG = ActivityManagerHook.class.getSimpleName();

    private static List<MethodInvokeListener> mListeners = new CopyOnWriteArrayList<MethodInvokeListener>();

    public ActivityManagerHook(Context context) {
        super(context);
    }

    public void setOnActivityMethodListener(MethodInvokeListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    @Override
    public void onHook(ClassLoader classLoader) throws Throwable {
        // 第一步：
        // API 26 以后，hook android.app.ActivityManager.IActivityManagerSingleton
        // API 25 以前，hook android.app.ActivityManagerNative.gDefault
        // see IIActivityManagerCompat.getObject()
        Object value = IIActivityManagerCompat.getObject();
        Field instanceField = IIActivityManagerCompat.getInstance();
        mOriginObj = instanceField.get(value);

        // 第二步：获取我们的代理对象，这里因为 IActivityManager 是接口，我们使用动态代理的方式
        Object proxy = ReflectUtils.makeProxy(classLoader, mOriginObj.getClass(), this);

        // 第三步：偷梁换柱，将我们的 proxy 替换原来的对象
        instanceField.set(value, proxy);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object invoke = null;
        Object[] newArgs = null;
        try {
            for (MethodInvokeListener listener : mListeners) {
                newArgs = listener.invoke(mOriginObj, method, args);
            }
            invoke = method.invoke(mOriginObj, newArgs == null ? args : newArgs);
            for (MethodInvokeListener listener : mListeners) {
                listener.onMethod(mOriginObj, method, invoke);
            }
        } catch (Throwable e) {
        }
        return invoke;
    }

}
