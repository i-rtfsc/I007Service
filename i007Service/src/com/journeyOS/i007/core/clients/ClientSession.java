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

package com.journeyOS.i007.core.clients;

import android.os.Binder;
import android.os.Message;

import com.journeyOS.i007.base.util.Singleton;
import com.journeyOS.i007.core.AppInfo;
import com.journeyOS.i007.interfaces.II007Listener;
import com.journeyOS.i007.task.TaskManager;
import com.journeyOS.litetask.TaskScheduler;


public class ClientSession {
    private final Clients mClisnts;

    private static final int DELAYED_MILLIS = 0;
    private static final int MSG_STATE = 1;
    private static final int MSG_OBJ = 2;

    private static final Singleton<ClientSession> gDefault = new Singleton<ClientSession>() {
        @Override
        protected ClientSession create() {
            return new ClientSession();
        }
    };

    public static ClientSession getDefault() {
        return gDefault.get();
    }

    private ClientSession() {
        mClisnts = new Clients(TaskScheduler.getInstance().provideHandler(TaskManager.HANDLER_CLIENT_SESSION));
        listener();
    }

    public void insertToCategory(long factorsFromClient, II007Listener listener) {
        int callingPid = Binder.getCallingPid();
        mClisnts.addListener(listener, factorsFromClient, callingPid);
    }

    public void removeFromCategory(II007Listener listener) {
        mClisnts.removeListener(listener);
    }

    public void dispatchFactorEvent(final long factoryId, final long state, final String packageName) {
        Message message = Message.obtain();
        message.what = MSG_STATE;
        AppInfo appInfo = new AppInfo(factoryId, state, packageName);
        message.obj = appInfo;
        TaskScheduler.getInstance().getHandler(TaskManager.HANDLER_CLIENT_SESSION).sendMessageDelayed(message, DELAYED_MILLIS);
    }

    public void dispatchFactorEvent(final long factoryId, final String msg) {
        Message message = Message.obtain();
        message.what = MSG_OBJ;
        AppInfo appInfo = new AppInfo(factoryId, 0, msg);
        message.obj = appInfo;
        TaskScheduler.getInstance().getHandler(TaskManager.HANDLER_CLIENT_SESSION).sendMessageDelayed(message, DELAYED_MILLIS);
    }

    private void listener() {
        TaskScheduler.getInstance().setOnMessageListener(TaskScheduler.getInstance().getHandler(TaskManager.HANDLER_CLIENT_SESSION),
                new TaskScheduler.OnMessageListener() {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case MSG_STATE:
                                AppInfo appInfo = (AppInfo) msg.obj;
                                if (appInfo != null)
                                    mClisnts.dispatchFactorEvent(appInfo.factorId, appInfo.state, appInfo.packageName);
                                break;
                            case MSG_OBJ:
                                AppInfo obj = (AppInfo) msg.obj;
                                mClisnts.dispatchFactorEvent(obj.factorId, obj.packageName);
                                break;
                        }
                    }
                }
        );
    }
}
