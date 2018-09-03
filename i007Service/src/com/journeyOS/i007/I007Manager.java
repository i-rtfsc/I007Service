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

import android.content.Context;
import android.os.RemoteException;

import com.journeyOS.i007.DataResource.APP;
import com.journeyOS.i007.base.util.AppUtils;
import com.journeyOS.i007.base.util.DebugUtils;
import com.journeyOS.i007.core.I007Core;
import com.journeyOS.i007.core.SceneUtils;
import com.journeyOS.i007.core.service.ServiceManagerNative;
import com.journeyOS.i007.interfaces.II007Listener;
import com.journeyOS.i007.interfaces.II007Register;
import com.journeyOS.i007.interfaces.II007Service;
import com.journeyOS.litetask.TaskScheduler;

import static com.journeyOS.i007.DataResource.FACTORY;
import static com.journeyOS.i007.DataResource.NetWork;


public class I007Manager {
    private static final String TAG = I007Manager.class.getSimpleName();
    private static final boolean DEBUG = true;

    //////////////////// factoryId ////////////////////
    public static final long SCENE_FACTOR_APP = 1 << 1;
    public static final long SCENE_FACTOR_LCD = 1 << 2;
    public static final long SCENE_FACTOR_NET = 1 << 3;
    public static final long SCENE_FACTOR_HEADSET = 1 << 4;
    public static final long SCENE_FACTOR_BATTERY = 1 << 5;

    public static final String SCENE_APP_STATE_DEFAULT = "D";
    public static final String SCENE_APP_STATE_ALBUM = "A";
    public static final String SCENE_APP_STATE_BROWSER = "B";
    public static final String SCENE_APP_STATE_GAME = "G";
    public static final String SCENE_APP_STATE_IM = "I";
    public static final String SCENE_APP_STATE_MUSIC = "M";
    public static final String SCENE_APP_STATE_NEWS = "N";
    public static final String SCENE_APP_STATE_READER = "R";
    public static final String SCENE_APP_STATE_VIDEO = "V";
    /**
     * when the device wakes up and becomes interactive
     */
    public static final String SCENE_LCD_STATE_ON = "L";
    /**
     * when the device goes to sleep and becomes non-interactive
     */
    public static final String SCENE_LCD_STATE_OFF = "X";

    public static final String SCENE_NET_STATE_ON = "N";
    public static final String SCENE_NET_STATE_OFF = "X";
    public static final String SCENE_NET_STATE_TYPE_WIFI = "W";
    public static final String SCENE_NET_STATE_TYPE_4G = "4";
    public static final String SCENE_NET_STATE_TYPE_3G = "3";
    public static final String SCENE_NET_STATE_TYPE_2G = "2";
    public static final String SCENE_NET_STATE_TYPE_UNKNOWN = "U";

    public static final String SCENE_HEADSET_STATE_ON = "E";
    public static final String SCENE_HEADSET_STATE_OFF = "X";


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
        if (packageName == null) return false;
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

    public static void keepAlive(final Context context) {
        boolean isSR = AppUtils.isServiceRunning(context, "com.journeyOS.i007.core.daemon.DaemonService");
        if (DEBUG) DebugUtils.d(TAG, "is daemon service running = " + isSR);
        if (!isSR) {
            TaskScheduler.getInstance().getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        ServiceManagerNative.running(context);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            });
        }
    }

    public static FACTORY getFactory(long factorId) {
        if ((factorId & SCENE_FACTOR_APP) != 0) {
            return FACTORY.APP;
        } else if ((factorId & SCENE_FACTOR_LCD) != 0) {
            return FACTORY.LCD;
        } else if ((factorId & SCENE_FACTOR_NET) != 0) {
            return FACTORY.NET;
        } else if ((factorId & SCENE_FACTOR_BATTERY) != 0) {
            return FACTORY.BATTERY;
        }
        return FACTORY.APP;
    }

    public static APP getApp(String status) {
        if (SceneUtils.isAlbum(status)) {
            return APP.ALBUM;
        } else if (SceneUtils.isBrowser(status)) {
            return APP.BROWSER;
        } else if (SceneUtils.isGame(status)) {
            return APP.GAME;
        } else if (SceneUtils.isIM(status)) {
            return APP.IM;
        } else if (SceneUtils.isMusic(status)) {
            return APP.MUSIC;
        } else if (SceneUtils.isNews(status)) {
            return APP.NEWS;
        } else if (SceneUtils.isReader(status)) {
            return APP.READER;
        } else if (SceneUtils.isVideo(status)) {
            return APP.VIDEO;
        }
        return APP.DEFAULT;
    }

    public static boolean isAlbum(String status) {
        return SceneUtils.isAlbum(status);
    }

    public static boolean isBrowser(String status) {
        return SceneUtils.isBrowser(status);
    }

    public static boolean isGame2(String status) {
        return SceneUtils.isGame(status);
    }

    public static boolean isIM(String status) {
        return SceneUtils.isIM(status);
    }

    public static boolean isMusic(String status) {
        return SceneUtils.isMusic(status);
    }

    public static boolean isNews(String status) {
        return SceneUtils.isNews(status);
    }

    public static boolean isReader(String status) {
        return SceneUtils.isReader(status);
    }

    public static boolean isVideo(String status) {
        return SceneUtils.isVideo(status);
    }

    public static boolean isScreenOn(String status) {
        return SceneUtils.isScreenOn(status);
    }

    public static boolean isHeadSetPlug(String status) {
        return SceneUtils.isHeadSetPlug(status);
    }

    public static boolean isNetAvailable(String status) {
        return SceneUtils.isNetAvailable(status);
    }

    public static boolean isWifi(String status) {
        return SceneUtils.isWifi(status);
    }

    public static boolean is4G(String status) {
        return SceneUtils.is4G(status);
    }

    public static boolean is3G(String status) {
        return SceneUtils.is3G(status);
    }

    public static boolean is2G(String status) {
        return SceneUtils.is2G(status);
    }

    public static NetWork getNetWork(String status) {
        if (SceneUtils.isNetAvailable(status)) {
            if (SceneUtils.isWifi(status)) {
                return NetWork.WIFI;
            } else if (SceneUtils.is4G(status)) {
                return NetWork.NET4G;
            } else if (SceneUtils.is3G(status)) {
                return NetWork.NET3G;
            } else if (SceneUtils.is2G(status)) {
                return NetWork.NET2G;
            } else {
                return NetWork.UNKNOWN;
            }
        }

        return NetWork.DISCONNECTED;
    }

    public static int getBatteryStatus(String status) {
        return SceneUtils.getBatteryStatus(status);
    }

    public static int getBatteryLevel(String status) {
        return SceneUtils.getBatteryLevel(status);
    }

    public static int getBatteryHealth(String status) {
        return SceneUtils.getBatteryHealth(status);
    }

    public static int getBatteryTemperature(String status) {
        return SceneUtils.getBatteryTemperature(status);
    }

    public static int getBatteryPlugged(String status) {
        return SceneUtils.getBatteryPlugged(status);
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
}
