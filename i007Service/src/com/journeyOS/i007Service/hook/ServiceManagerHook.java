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

import com.journeyOS.i007Service.base.utils.ReflectUtils;
import com.journeyOS.i007Service.hook.compat.ServiceManagerCompat;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class ServiceManagerHook extends Hook implements InvocationHandler {
    private static final String TAG = ServiceManagerHook.class.getSimpleName();
    private static final boolean DEBUG = false;
    private String mServiceName;
    private Object mProxyIInterface;

    public ServiceManagerHook(Context context, String serviceName) {
        super(context);
        this.mServiceName = serviceName;
    }

    public void setProxyIInterface(Object proxyIInterface) {
        this.mProxyIInterface = proxyIInterface;
    }

    @Override
    public void onHook(ClassLoader classLoader) throws Throwable {
        Map sCache = ServiceManagerCompat.sCache();
        Object cachedObj = sCache.get(mServiceName);
        sCache.remove(mServiceName);
        mOriginObj = ServiceManagerCompat.getService(mServiceName);
        if (mOriginObj == null) {
            if (cachedObj != null && cachedObj instanceof IBinder && !Proxy.isProxyClass(cachedObj.getClass())) {
                mOriginObj = cachedObj;
            }
        }
        if (mOriginObj instanceof IBinder) {
            Object proxyBinder = ReflectUtils.makeProxy(classLoader, mOriginObj.getClass(), this);
            sCache.put(mServiceName, proxyBinder);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals(ServiceManagerCompat.METHOD_QUERY_LOCAL_INTERFACE)) {
            if (mProxyIInterface != null) {
                return mProxyIInterface;
            }
        }
        return method.invoke(mOriginObj, args);
    }
}
