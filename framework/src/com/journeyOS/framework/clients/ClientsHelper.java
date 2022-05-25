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

import com.journeyOS.common.SmartLog;

import java.util.HashMap;
import java.util.Map;


/**
 * 客户端Helper
 *
 * @author solo
 */
public class ClientsHelper<TListener extends IInterface> {
    private static final String TAG = ClientsHelper.class.getSimpleName();
    private final Handler mHandler;

    /**
     * binder对象跟II007Observer对应起来
     */
    private final Map<IBinder, LinkedListener> mListenerMap = new HashMap<>();


    /**
     * TODO
     * 后期改成：
     * 1. binder对象跟II007Observer对应起来
     * 2. binder对象跟场景因子对应起来，这样做有几个好处：
     *      - 客户端不用解除监听可以随时更换场景因子
     *      - 在解注册的时候，或者客户端断开连接的时候检查相应的场景因子是否还相应monitor，如果不相应则stop，节省资源
     */

    /**
     * 构造函数
     *
     * @param handler Handler
     */
    public ClientsHelper(Handler handler) {
        mHandler = handler;
    }

    protected boolean addRemoteListener(TListener listener, long factors, int pid) {
        IBinder binder = listener.asBinder();
        LinkedListener deathListener = new LinkedListener(listener, factors, pid);
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

    protected void removeRemoteListener(TListener listener) {
        IBinder binder = listener.asBinder();
        LinkedListener linkedListener;
        synchronized (mListenerMap) {
            linkedListener = mListenerMap.remove(binder);
        }
        if (linkedListener != null) {
            binder.unlinkToDeath(linkedListener, 0);
        }
    }

    protected void foreach(ListenerOperation<TListener> operation, long factors) {
        synchronized (mListenerMap) {
            foreachUnsafe(operation, factors);
        }
    }

    private void foreachUnsafe(ListenerOperation<TListener> operation, long factors) {
        for (LinkedListener linkedListener : mListenerMap.values()) {
            if ((linkedListener.getFactors() & factors) != 0) {
                post(linkedListener.getUnderlyingListener(), operation, linkedListener.mPid);
            }
        }
    }

    private void post(TListener listener, ListenerOperation<TListener> operation, int pid) {
        if (operation != null) {
            mHandler.post(new HandlerRunnable(listener, operation, pid));
        }
    }

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

    protected interface ListenerOperation<TListener extends IInterface> {
        void execute(TListener listener, int pid) throws RemoteException;
    }

    private class LinkedListener implements IBinder.DeathRecipient {
        private final TListener mListener;
        private final int mPid;
        private final long mFactors;

        public LinkedListener(TListener listener, long factors, int pid) {
            mListener = listener;
            mPid = pid;
            mFactors = factors;
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
    }

    private class HandlerRunnable implements Runnable {
        private final TListener mListener;
        private final ListenerOperation<TListener> mOperation;
        private final int mPid;

        public HandlerRunnable(TListener listener,
                               ListenerOperation<TListener> operation, int pid) {
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