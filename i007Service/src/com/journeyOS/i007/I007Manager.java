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

package com.journeyOS.i007;

import android.os.RemoteException;

import com.journeyOS.i007.base.util.AppUtils;
import com.journeyOS.i007.core.I007Core;
import com.journeyOS.i007.core.service.ServiceManagerNative;
import com.journeyOS.i007.data.BatteryInfo;
import com.journeyOS.i007.interfaces.II007Listener;
import com.journeyOS.i007.interfaces.II007Register;
import com.journeyOS.i007.interfaces.II007Service;


public class I007Manager {
    private static final String TAG = I007Manager.class.getSimpleName();

    //////////////////// factoryId ////////////////////
    public static final long SCENE_FACTOR_APP = 1 << 1;
    public static final long SCENE_FACTOR_LCD = 1 << 2;

    /////////////////////////// app type ///////////////////////////
    public static final long SCENE_FACTOR_APP_STATE_DEFAULT = 1 << 1;
    public static final long SCENE_FACTOR_APP_STATE_ALBUM = 1 << 2;
    public static final long SCENE_FACTOR_APP_STATE_BROWSER = 1 << 3;
    public static final long SCENE_FACTOR_APP_STATE_GAME = 1 << 4;
    public static final long SCENE_FACTOR_APP_STATE_IM = 1 << 5;
    public static final long SCENE_FACTOR_APP_STATE_MUSIC = 1 << 6;
    public static final long SCENE_FACTOR_APP_STATE_NEWS = 1 << 7;
    public static final long SCENE_FACTOR_APP_STATE_READER = 1 << 8;
    public static final long SCENE_FACTOR_APP_STATE_VIDEO = 1 << 9;
    /////////////////////////// app type ///////////////////////////

    //////////////////////// lcd state ////////////////////////
    /**
     * when the device wakes up and becomes interactive
     */
    public static final long SCENE_FACTOR_LCD_STATE_ON = 1 << 1;
    /**
     * when the device goes to sleep and becomes non-interactive
     */
    public static final long SCENE_FACTOR_LCD_STATE_OFF = 1 << 2;
    //////////////////////// lcd state ////////////////////////


    public static void registerListener(long factors, II007Listener listener) {

        II007Register register = ServiceManagerNative.getI007Register();
        if (register != null) {
            try {
                register.registerListener(factors, listener);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void unregisterListener(II007Listener listener) {
        II007Register register = ServiceManagerNative.getI007Register();
        if (register != null) {
            try {
                register.unregisterListener(listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isGame(String packageName) {
        II007Service service = ServiceManagerNative.getI007Service();
        if (service != null) {
            try {
                return service.isGame(packageName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isGame(long state) {
        boolean isGame = (state & SCENE_FACTOR_APP_STATE_GAME) != 0;
        return isGame;
    }

    @Deprecated
    public static void autoEnableAccessibilityService() {
        AppUtils.autoEnableAccessibilityService(I007Core.getCore().getContext());
    }

    public static boolean isServiceEnabled() {
        return AppUtils.isServiceEnabled(I007Core.getCore().getContext());
    }

    public static void openSettingsAccessibilityService() {
        AppUtils.openSettingsAccessibilityService(I007Core.getCore().getContext());
    }

    public static FACTORY getFactory(long factorId) {
        if ((factorId & SCENE_FACTOR_APP) != 0) {
            return FACTORY.APP;
        } else if ((factorId & SCENE_FACTOR_LCD) != 0) {
            return FACTORY.LCD;
        }
        return FACTORY.APP;
    }

    public enum FACTORY {//factoryId
        APP(1),
        LCD(2);

        private int value;

        private FACTORY(int value) {
            this.value = value;
        }
    }

}
