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
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

import com.journeyOS.i007Service.base.utils.DebugUtils;
import com.journeyOS.i007Service.base.utils.ReflectUtils;
import com.journeyOS.i007Service.hook.compat.IInputMethodManagerCompat;
import com.journeyOS.i007Service.hook.compat.SystemServiceRegistryCompat;
import com.journeyOS.i007Service.hook.listeners.MethodInvokeListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InputMethodManagerHook extends Hook implements InvocationHandler {
    private static final String TAG = InputMethodManagerHook.class.getSimpleName();

    private static List<MethodInvokeListener> mListeners = new CopyOnWriteArrayList<MethodInvokeListener>();

    public InputMethodManagerHook(Context context) {
        super(context);
    }

    public void setOnInputMethodListener(MethodInvokeListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    @Override
    public void onHook(ClassLoader classLoader) throws Throwable {
        ServiceManagerHook serviceManagerHook = new ServiceManagerHook(mContext, Context.INPUT_METHOD_SERVICE);
        serviceManagerHook.onHook(classLoader);
        Object originBinder = serviceManagerHook.getOriginObj();
        if (originBinder instanceof IBinder) {
            mOriginObj = IInputMethodManagerCompat.asInterface((IBinder) originBinder);
            Object proxyInputMethodInterface = ReflectUtils.makeProxy(classLoader, mOriginObj.getClass(), this);
            serviceManagerHook.setProxyIInterface(proxyInputMethodInterface);
//            clearCachedService();
//            mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
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

    private void clearCachedService() throws Throwable {
        Object sInstance = ReflectUtils.getStaticFiled(InputMethodManager.class, "sInstance");
        if (sInstance != null) {
            ReflectUtils.setStaticFiled(InputMethodManager.class, "sInstance", null);
            Object systemFetcher = SystemServiceRegistryCompat.getSystemFetcher(Context.INPUT_METHOD_SERVICE);
            if (systemFetcher != null) {
                ReflectUtils.setFiled(systemFetcher.getClass().getSuperclass(), "mCachedInstance", systemFetcher, null);
            }
        }
    }
}
