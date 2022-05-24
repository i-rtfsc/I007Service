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

package com.journeyOS.i007Service.service.clients;

import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.journeyOS.common.utils.Singleton;
import com.journeyOS.i007manager.I007Result;
import com.journeyOS.i007manager.II007Observer;

/**
 * @author solo
 */
public class ClientSession {

    private static final Singleton<ClientSession> gDefault = new Singleton<ClientSession>() {
        @Override
        protected ClientSession create() {
            return new ClientSession();
        }
    };
    private final HandlerThread mHandlerThread;
    private final Handler mHandler;
    private final Clients mClisnts;

    private ClientSession() {
        mHandlerThread = new HandlerThread("ClientSession");
        mHandlerThread.start();
        mHandler = new H(mHandlerThread.getLooper());
        mClisnts = new Clients(mHandler);
    }

    public static ClientSession getDefault() {
        return gDefault.get();
    }

    public void insertToCategory(long factorsFromClient, II007Observer listener) {
        int callingPid = Binder.getCallingPid();
        mClisnts.addListener(listener, factorsFromClient, callingPid);
    }

    public void removeFromCategory(II007Observer listener) {
        mClisnts.removeListener(listener);
    }

    public synchronized void dispatchFactorEvent(final I007Result result) {
        Message message = Message.obtain();
        message.what = H.MSG_OBJ;
        message.obj = result;
        mHandler.sendMessageDelayed(message, H.DELAYED_MILLIS);
    }

    private class H extends Handler {
        public static final long DELAYED_MILLIS = 2000;
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
            }
        }
    }
}