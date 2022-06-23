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

import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import com.journeyOS.i007manager.SmartLog;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端Helper
 *
 * @author solo
 */
public class ClientsImpl<TListener extends IInterface> {
    private static final String TAG = ClientsImpl.class.getSimpleName();
    private final Handler mHandler;

    /**
     * binder对象跟II007Observer对应起来
     */
    private final Map<IBinder, LinkedListener> mListenerMap = new HashMap<>();

    /**
     * 构造函数
     *
     * @param handler Handler
     */
    public ClientsImpl(Handler handler) {
        mHandler = handler;
    }

    /**
     * 注册事件监听
     *
     * @param pid      客户端进程号
     * @param listener 监听回调方
     * @return 是否成功
     */
    public boolean addRemoteListener(int pid, TListener listener) {
        IBinder binder = listener.asBinder();
        LinkedListener deathListener = new LinkedListener(pid, listener);
        synchronized (mListenerMap) {
            if (mListenerMap.containsKey(binder)) {
                // listener already added
                return true;
            }
            try {
                binder.linkToDeath(deathListener, 0);
            } catch (RemoteException e) {
                // if the remote process registering the listener is already death, just swallow the
                // exception and return
                SmartLog.v(TAG, "Remote listener already died.");
                return false;
            }
            mListenerMap.put(binder, deathListener);
        }
        return true;
    }

    /**
     * 注销事件监听
     *
     * @param listener 监听回调方
     * @return 是否成功
     */
    public boolean removeRemoteListener(TListener listener) {
        IBinder binder = listener.asBinder();
        LinkedListener linkedListener;
        synchronized (mListenerMap) {
            linkedListener = mListenerMap.remove(binder);
        }
        if (linkedListener != null) {
            binder.unlinkToDeath(linkedListener, 0);
        }
        return true;
    }

    /**
     * 设置场景因子
     *
     * @param pid     客户端进程
     * @param factors 场景因子
     * @return 是否成功
     */
    public boolean setRemoteFactor(int pid, long factors) {
        boolean result = false;
        for (Map.Entry<IBinder, LinkedListener> entry : mListenerMap.entrySet()) {
            IBinder binder = entry.getKey();
            LinkedListener linkedListener = entry.getValue();
            if (pid == linkedListener.getPid()) {
                linkedListener.setFactors(factors);
                mListenerMap.replace(binder, linkedListener);
                result = true;
            }
            if (result) {
                break;
            }
        }
        return true;
    }

    /**
     * 新增场景因子
     *
     * @param pid     客户端进程
     * @param factors 场景因子
     * @return 是否成功
     */
    public boolean updateRemoteFactor(int pid, long factors) {
        boolean result = false;
        for (Map.Entry<IBinder, LinkedListener> entry : mListenerMap.entrySet()) {
            IBinder binder = entry.getKey();
            LinkedListener linkedListener = entry.getValue();
            if (pid == linkedListener.getPid()) {
                long sourceFactors = linkedListener.getFactors();
                sourceFactors |= factors;
                linkedListener.setFactors(sourceFactors);
                mListenerMap.replace(binder, linkedListener);
                result = true;
            }
            if (result) {
                break;
            }
        }
        return result;
    }

    /**
     * 删除场景因子
     *
     * @param pid     客户端进程号
     * @param factors 场景因子
     * @return 是否成功
     */
    public boolean removeRemoteFactor(int pid, long factors) {
        boolean result = false;
        for (Map.Entry<IBinder, LinkedListener> entry : mListenerMap.entrySet()) {
            IBinder binder = entry.getKey();
            LinkedListener linkedListener = entry.getValue();
            if (pid == linkedListener.getPid()) {
                long sourceFactors = linkedListener.getFactors();
                sourceFactors ^= factors;
                linkedListener.setFactors(sourceFactors);
                mListenerMap.replace(binder, linkedListener);
                result = true;
            }
            if (result) {
                break;
            }
        }
        return result;
    }

    /**
     * 判断所有的客户端里是否存在输入的场景因子
     *
     * @param factors 场景因子
     * @return 存在场景因子
     */
    public boolean checkFactor(long factors) {
        boolean exist = false;
        for (Map.Entry<IBinder, LinkedListener> entry : mListenerMap.entrySet()) {
            //IBinder binder = entry.getKey();
            LinkedListener linkedListener = entry.getValue();

            if ((linkedListener.getFactors() & factors) != 0) {
                exist = true;
            }

            if (exist) {
                break;
            }
        }

        return exist;
    }

    /**
     * 遍历所有的客户端
     *
     * @param operation 回调方
     * @param factors   场景因子
     */
    protected void foreach(ListenerOperation<TListener> operation, long factors) {
        synchronized (mListenerMap) {
            foreachUnsafe(operation, factors);
        }
    }

    private void foreachUnsafe(ListenerOperation<TListener> operation, long factors) {
        for (LinkedListener linkedListener : mListenerMap.values()) {
            if ((linkedListener.getFactors() & factors) != 0) {
                post(linkedListener.getUnderlyingListener(), operation, linkedListener.getPid());
            }
        }
    }

    private void post(TListener listener, ListenerOperation<TListener> operation, int pid) {
        if (operation != null) {
            mHandler.post(new HandlerRunnable(listener, operation, pid));
        }
    }

    /**
     * to string
     *
     * @return 组合之后的字符串
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        synchronized (mListenerMap) {
            str.append(mListenerMap.size() + " listeners:\n");
            for (LinkedListener linkedListener : mListenerMap.values()) {
                str.append("[");
                str.append("pid:" + linkedListener.getPid());
                str.append(" listener:" + linkedListener.getUnderlyingListener());
                str.append(" factors:" + Long.toHexString(linkedListener.getFactors()));
                str.append("]\n");
            }
        }
        return str.toString();
    }

    /**
     * 回调
     *
     * @param <TListener> 回调方
     */
    protected interface ListenerOperation<TListener extends IInterface> {
        void execute(TListener listener, int pid) throws RemoteException;
    }

    private class LinkedListener implements IBinder.DeathRecipient {
        private final TListener mListener;
        private final int mPid;
        private long mFactors;

        public LinkedListener(int pid, TListener listener) {
            mListener = listener;
            mPid = pid;
        }

        public TListener getUnderlyingListener() {
            return mListener;
        }

        @Override
        public void binderDied() {
            SmartLog.d(TAG, "Remote Listener died: " + mListener);
            removeRemoteListener(mListener);
        }

        public int getPid() {
            return mPid;
        }

        public long getFactors() {
            return mFactors;
        }

        public void setFactors(long factors) {
            if (SmartLog.isDebug()) {
                SmartLog.d(TAG, "factors = [" + factors + "]");
            }
            this.mFactors = factors;
        }
    }

    private class HandlerRunnable implements Runnable {
        private final TListener mListener;
        private final ListenerOperation<TListener> mOperation;
        private final int mPid;

        public HandlerRunnable(TListener listener, ListenerOperation<TListener> operation, int pid) {
            mListener = listener;
            mOperation = operation;
            mPid = pid;
        }

        @Override
        public void run() {
            try {
                mOperation.execute(mListener, mPid);
            } catch (RemoteException e) {
                SmartLog.v(TAG, "Error in monitored listener.");
            }
        }
    }
}