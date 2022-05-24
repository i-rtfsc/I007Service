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

import android.os.Handler;
import android.os.RemoteException;

import com.journeyOS.common.SmartLog;
import com.journeyOS.i007manager.I007Result;
import com.journeyOS.i007manager.II007Observer;

import java.io.FileDescriptor;
import java.io.PrintWriter;


/**
 * @author solo
 */
public class Clients extends ClientsHelper {
    private static final String TAG = Clients.class.getSimpleName();

    public Clients(Handler handler) {
        super(handler);
    }

    public boolean addListener(II007Observer listener, long factors, int callingPid) {
        if (addRemoteListener(listener, factors, callingPid)) {
            SmartLog.i(TAG, "addListener " + listener
                    + " for " + callingPid + " " + Long.toHexString(factors));
            return true;
        } else {
            return false;
        }
    }

    public void removeListener(II007Observer listener) {
        try {
            removeRemoteListener(listener);
            SmartLog.i(TAG, "removeListener " + listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void dispatchFactorEvent(final I007Result result) {
        Operation operation = new Operation() {
            @Override
            public void execute(II007Observer listener, int pid) throws RemoteException {
                try {
                    SmartLog.d(TAG, "dispatch to pid = [" + pid + "], result = [" + result + "]");
                    listener.onSceneChanged(result);
                } catch (Throwable e) {
                    SmartLog.w(TAG, "Exception in dispatch factor event with appInfo!!!");
                }
            }
        };
        foreach(operation, result.getFactoryId());
    }

    public void dump(FileDescriptor fd, PrintWriter pw) {
        pw.println(super.toString());
    }

    private interface Operation extends ListenerOperation<II007Observer> {
    }
}