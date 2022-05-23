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

package com.journeyOS.i007Service.service.core.base;

import android.os.IBinder;
import android.os.RemoteException;

import com.journeyOS.i007manager.base.IServiceFetcher;

/**
 * @author solo
 */
public class ServiceFetcher extends IServiceFetcher.Stub {

    /**
     * Returns a reference to a service with the given name.
     *
     * @param name the name of the service to get
     * @return a reference to the service, or <code>null</code> if the service doesn't exist
     */
    @Override
    public IBinder getService(String name) throws RemoteException {
        if (name != null) {
            return ServiceCache.getService(name);
        }
        return null;
    }

    /**
     * Place a new @a service called @a name into the service
     * manager.
     *
     * @param name    the name of the new service
     * @param service the service object
     */
    @Override
    public void addService(String name, IBinder service) throws RemoteException {
        if (name != null && service != null) {
            ServiceCache.addService(name, service);
        }
    }

    /**
     * Remove a exist @a service called @a name into the service
     * manager.
     *
     * @param name the name of the new service
     */
    @Override
    public void removeService(String name) throws RemoteException {
        if (name != null) {
            ServiceCache.removeService(name);
        }
    }
}
