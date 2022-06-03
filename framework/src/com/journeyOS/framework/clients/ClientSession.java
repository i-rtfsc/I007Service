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

package com.journeyOS.framework.clients;

import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.journeyOS.i007manager.I007Result;
import com.journeyOS.i007manager.II007Observer;

/**
 * @author solo
 */
public final class ClientSession {
    private static volatile ClientSession sInstance = null;
    private final HandlerThread mHandlerThread;
    private final Handler mHandler;
    private final Clients mClisnts;

    private ClientSession() {
        mHandlerThread = new HandlerThread("ClientSession");
        mHandlerThread.start();
        mHandler = new H(mHandlerThread.getLooper());
        mClisnts = new Clients(mHandler);
    }

    public static ClientSession getInstance() {
        if (sInstance == null) {
            synchronized (ClientSession.class) {
                if (sInstance == null) {
                    sInstance = new ClientSession();
                }
            }
        }
        return sInstance;
    }

    /**
     * 插入监听回调方
     *
     * @param listener 监听回调方
     * @return 是否成功
     */
    public boolean insertToCategory(II007Observer listener) {
        int callingPid = Binder.getCallingPid();
        return mClisnts.addRemoteListener(callingPid, listener);
    }

    /**
     * 删除监听回调方
     *
     * @param listener 监听回调方
     * @return 是否成功
     */
    public boolean removeFromCategory(II007Observer listener) {
        return mClisnts.removeRemoteListener(listener);
    }

    /**
     * 设置场景因子
     *
     * @param factors 场景因子
     * @return 是否成功
     */
    public boolean setFactorToCategory(long factors) {
        int callingPid = Binder.getCallingPid();
        return mClisnts.setRemoteFactor(callingPid, factors);
    }

    /**
     * 新增场景因子
     *
     * @param factors 场景因子
     * @return 是否成功
     */
    public boolean updateFactorToCategory(long factors) {
        int callingPid = Binder.getCallingPid();
        return mClisnts.updateRemoteFactor(callingPid, factors);
    }

    /**
     * 清除场景因子
     *
     * @param factors 场景因子
     * @return 是否成功
     */
    public boolean removeFactorFromCategory(long factors) {
        int callingPid = Binder.getCallingPid();
        return mClisnts.removeRemoteFactor(callingPid, factors);
    }

    /**
     * 判断所有的客户端里是否存在输入的场景因子
     *
     * @param factors 场景因子
     * @return 存在场景因子
     */
    public boolean checkFactorFromCategory(long factors) {
        return mClisnts.checkFactor(factors);
    }

    /**
     * 派发结果给回调方
     *
     * @param result 结果数据
     */
    public synchronized void dispatchFactorEvent(final I007Result result) {
        Message message = Message.obtain();
        message.what = H.MSG_OBJ;
        message.obj = result;
        mHandler.sendMessageDelayed(message, H.DELAYED_MILLIS);
    }

    private class H extends Handler {
        public static final long DELAYED_MILLIS = 15;
        public static final int MSG_OBJ = 1;

        public H(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OBJ:
                    mClisnts.dispatchFactorEvent((I007Result) msg.obj);
                    break;
                default:
                    break;
            }
        }
    }
}