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

import android.os.Handler;
import android.os.RemoteException;

import com.journeyOS.i007.base.util.DebugUtils;
import com.journeyOS.i007.interfaces.II007Listener;

import java.io.FileDescriptor;
import java.io.PrintWriter;


public class Clients extends ClientsHelper {
    private static final String TAG = Clients.class.getSimpleName();

    public Clients(Handler handler) {
        super(handler);
    }

    public boolean addListener(II007Listener listener, long factors, int callingPid) {
        if (addRemoteListener(listener, factors, callingPid)) {
            DebugUtils.i(TAG, "addListener " + listener
                    + " for " + callingPid + " " + Long.toHexString(factors));
            return true;
        } else {
            return false;
        }
    }

    public void removeListener(II007Listener listener) {
        try {
            removeRemoteListener(listener);
            DebugUtils.i(TAG, "removeListener " + listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dispatchFactorEvent(final long factors, final long state, final String packageName) {
        Operation operation = new Operation() {
            @Override
            public void execute(II007Listener listener, int pid) throws RemoteException {
                try {
                    DebugUtils.i(TAG, "dispatch to " + pid + " ("
                            + state
                            + packageName
                            + ")");
                    listener.onSceneChanged(factors, state, packageName);
                } catch (Throwable e) {
                    DebugUtils.w(TAG, "Exception in dispatch factor event with appInfo!!!");
                }
            }
        };
        foreach(operation, factors);
    }

    public void dispatchFactorEvent(final long factors, final String msg) {
        Operation operation = new Operation() {
            @Override
            public void execute(II007Listener listener, int pid) throws RemoteException {
                try {
                    DebugUtils.i(TAG, "dispatch to " + pid + " ("
                            + msg
                            + ")");
                    listener.onSceneChangedJson(factors, msg);
                } catch (Throwable e) {
                    DebugUtils.w(TAG, "Exception in dispatch factor event with appInfo!!!");
                }
            }
        };
        foreach(operation, factors);
    }

    public void dump(FileDescriptor fd, PrintWriter pw) {
        pw.println(super.toString());
    }

    private interface Operation extends ListenerOperation<II007Listener> {
    }
}
