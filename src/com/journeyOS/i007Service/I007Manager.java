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

package com.journeyOS.i007Service;

import android.os.RemoteException;

import com.journeyOS.i007Service.DataResource.APP;
import com.journeyOS.i007Service.base.utils.AppUtils;
import com.journeyOS.i007Service.base.utils.DebugUtils;
import com.journeyOS.i007Service.core.I007Core;
import com.journeyOS.i007Service.core.NotifyManager;
import com.journeyOS.i007Service.core.SceneUtils;
import com.journeyOS.i007Service.core.detect.AccessibilityMonitor;
import com.journeyOS.i007Service.core.service.ServiceManagerNative;
import com.journeyOS.i007Service.database.App;
import com.journeyOS.i007Service.database.DatabaseManager;
import com.journeyOS.i007Service.interfaces.II007Listener;
import com.journeyOS.i007Service.interfaces.II007Register;
import com.journeyOS.i007Service.interfaces.II007Service;
import com.journeyOS.i007Service.task.TaskManager;

import static com.journeyOS.i007Service.DataResource.FACTORY;
import static com.journeyOS.i007Service.DataResource.NetWork;

public class I007Manager {
    /**
     * 前台变化为何种类型APP场景
     */
    public static final long SCENE_FACTOR_APP = 1 << 1;
    /**
     * 屏幕亮灭场景
     */
    public static final long SCENE_FACTOR_LCD = 1 << 2;
    /**
     * 网络变化场景
     */
    public static final long SCENE_FACTOR_NET = 1 << 3;
    /**
     * 耳机插拔场景
     */
    public static final long SCENE_FACTOR_HEADSET = 1 << 4;
    /**
     * 电池电量、温度等变化场景
     */
    public static final long SCENE_FACTOR_BATTERY = 1 << 5;
    /**
     * 默认APP
     */
    public static final String SCENE_APP_STATE_DEFAULT = "D";
    /**
     * 相册APP
     */
    public static final String SCENE_APP_STATE_ALBUM = "A";
    /**
     * 浏览器APP
     */
    public static final String SCENE_APP_STATE_BROWSER = "B";
    /**
     * 游戏APP
     */
    public static final String SCENE_APP_STATE_GAME = "G";
    /**
     * 聊天APP
     */
    public static final String SCENE_APP_STATE_IM = "I";
    /**
     * 音乐APP
     */
    public static final String SCENE_APP_STATE_MUSIC = "M";
    /**
     * 新闻APP
     */
    public static final String SCENE_APP_STATE_NEWS = "N";
    /**
     * 阅读APP
     */
    public static final String SCENE_APP_STATE_READER = "R";
    /**
     * 视频APP
     */
    public static final String SCENE_APP_STATE_VIDEO = "V";
    /**
     * 屏幕亮
     */
    public static final String SCENE_LCD_STATE_ON = "L";
    /**
     * 屏幕灭
     */
    public static final String SCENE_LCD_STATE_OFF = "X";
    /**
     * 网络开
     */
    public static final String SCENE_NET_STATE_ON = "N";
    /**
     * 网络关
     */
    public static final String SCENE_NET_STATE_OFF = "X";
    /**
     * WI-FI
     */
    public static final String SCENE_NET_STATE_TYPE_WIFI = "W";
    /**
     * 4G
     */
    public static final String SCENE_NET_STATE_TYPE_4G = "4";
    /**
     * 3G
     */
    public static final String SCENE_NET_STATE_TYPE_3G = "3";
    /**
     * 2G
     */
    public static final String SCENE_NET_STATE_TYPE_2G = "2";
    /**
     * 未知
     */
    public static final String SCENE_NET_STATE_TYPE_UNKNOWN = "U";
    /**
     * 耳机插入
     */
    public static final String SCENE_HEADSET_STATE_ON = "E";
    /**
     * 耳机拔出
     */
    public static final String SCENE_HEADSET_STATE_OFF = "X";
    private static final String TAG = I007Manager.class.getSimpleName();
    private static final boolean DEBUG = true;

    /**
     * 监听场景变化
     *
     * @param factors  场景因子
     * @param listener 回调
     */
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

    /**
     * 解注册
     *
     * @param listener 回调
     */
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

    /**
     * 获取当前状态
     *
     * @return 当状态
     */
    public String getCurrentState() {
        return NotifyManager.getDefault().getCurrentState();
    }

    /**
     * 进程常驻
     */
    public static void keepAlive() {
        boolean isSR = AppUtils.isServiceRunning(I007Core.getCore().getContext(), "com.journeyOS.i007.core.daemon.DaemonService");
        if (DEBUG) DebugUtils.d(TAG, "is daemon service running = " + isSR);
        if (!isSR) {
            TaskManager.getDefault().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    try {
//                        AliveActivity.navigationActivity(I007Core.getCore().getContext());
                        ServiceManagerNative.running();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 根据场景因子获取场景
     *
     * @param factorId 场景因子
     * @return 场景
     */
    public static FACTORY getFactory(long factorId) {
        if ((factorId & SCENE_FACTOR_APP) != 0) {
            return FACTORY.APP;
        } else if ((factorId & SCENE_FACTOR_LCD) != 0) {
            return FACTORY.LCD;
        } else if ((factorId & SCENE_FACTOR_NET) != 0) {
            return FACTORY.NET;
        } else if ((factorId & SCENE_FACTOR_HEADSET) != 0) {
            return FACTORY.HEADSET;
        } else if ((factorId & SCENE_FACTOR_BATTERY) != 0) {
            return FACTORY.BATTERY;
        }
        return FACTORY.APP;
    }

    /**
     * 根据状态获取APP类型
     *
     * @param status 回调的状态
     * @return APP类型
     */
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

    /**
     * 根据状态获取APP类型
     *
     * @param packageName 包名
     * @return APP类型
     */
    public static APP getAppType(String packageName) {
        App app = DatabaseManager.getDefault().queryApp(packageName);
        if (app == null) {
            return APP.DEFAULT;
        }

        if (AccessibilityMonitor.ALBUM.equals(app.type)) {
            return APP.ALBUM;
        } else if (AccessibilityMonitor.BROWSER.equals(app.type)) {
            return APP.BROWSER;
        } else if (AccessibilityMonitor.GAME.equals(app.type)) {
            return APP.GAME;
        } else if (AccessibilityMonitor.IM.equals(app.type)) {
            return APP.IM;
        } else if (AccessibilityMonitor.MUSIC.equals(app.type)) {
            return APP.MUSIC;
        } else if (AccessibilityMonitor.NEWS.equals(app.type)) {
            return APP.NEWS;
        } else if (AccessibilityMonitor.READER.equals(app.type)) {
            return APP.READER;
        } else if (AccessibilityMonitor.VIDEO.equals(app.type)) {
            return APP.VIDEO;
        }
        return APP.DEFAULT;
    }

    /**
     * 根据状态判断是否是相册APP
     *
     * @param status 回调的状态
     * @return true:相册APP
     * false:非相册APP
     */
    public static boolean isAlbum(String status) {
        return SceneUtils.isAlbum(status);
    }

    /**
     * 根据状态判断是否是浏览器APP
     *
     * @param status 回调的状态
     * @return true:浏览器APP
     * false:非浏览器APP
     */
    public static boolean isBrowser(String status) {
        return SceneUtils.isBrowser(status);
    }

    /**
     * 根据包名判断是否是游戏
     *
     * @param packageName 应用包名
     * @return true:游戏
     * false:非游戏
     */
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

    /**
     * 把包名packageName添加到游戏列表中
     *
     * @param source      执行的应用，后续可能会加限制。指定某些应用才可以执行
     * @param packageName 需要添加到游戏列表的包名
     */
    public static void addGame(String source, String packageName) {
        boolean isGame = isGame(packageName);
        if (!isGame) {
            II007Service service = ServiceManagerNative.getI007Service();
            if (service != null) {
                try {
                    service.addGame(source, packageName);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 把包名packageName从游戏列表中移除
     *
     * @param source      执行的应用，后续可能会加限制。指定某些应用才可以执行
     * @param packageName 需要从游戏列表中移除包名
     */
    public static void removeGame(String source, String packageName) {
        boolean isGame = isGame(packageName);
        if (isGame) {
            II007Service service = ServiceManagerNative.getI007Service();
            if (service != null) {
                try {
                    service.removeGame(source, packageName);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据状态判断是否是游戏APP
     *
     * @param status 回调的状态
     * @return true:游戏APP
     * false:非游戏APP
     */
    public static boolean checkGame(String status) {
        return SceneUtils.isGame(status);
    }

    /**
     * 根据状态判断是否是聊天APP
     *
     * @param status 回调的状态
     * @return true:聊天APP
     * false:非聊天APP
     */
    public static boolean checkIM(String status) {
        return SceneUtils.isIM(status);
    }

    /**
     * 根据状态判断是否是音乐APP
     *
     * @param status 回调的状态
     * @return true:音乐APP
     * false:非音乐APP
     */
    public static boolean checkMusic(String status) {
        return SceneUtils.isMusic(status);
    }

    /**
     * 根据状态判断是否是新闻APP
     *
     * @param status 回调的状态
     * @return true:新闻APP
     * false:非新闻APP
     */
    public static boolean checkNews(String status) {
        return SceneUtils.isNews(status);
    }

    /**
     * 根据状态判断是否是阅读APP
     *
     * @param status 回调的状态
     * @return true:阅读APP
     * false:非阅读APP
     */
    public static boolean checkReader(String status) {
        return SceneUtils.isReader(status);
    }

    /**
     * 根据包名判断是否是视频APP
     *
     * @param status 回调的状态
     * @return true:视频APP
     * false:非视频APP
     */
    public static boolean isVideo(String packageName) {
        if (packageName == null) return false;
        II007Service service = ServiceManagerNative.getI007Service();
        if (service != null) {
            try {
                return service.isVideo(packageName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 把包名packageName添加到视频列表中
     *
     * @param source      执行的应用，后续可能会加限制。指定某些应用才可以执行
     * @param packageName 需要添加到视频列表的包名
     */
    public static void addVideo(String source, String packageName) {
        boolean isVideo = isVideo(packageName);
        if (!isVideo) {
            II007Service service = ServiceManagerNative.getI007Service();
            if (service != null) {
                try {
                    service.addVideo(source, packageName);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 把包名packageName从视频列表中移除
     *
     * @param source      执行的应用，后续可能会加限制。指定某些应用才可以执行
     * @param packageName 需要从视频列表中移除包名
     */
    public static void removeVideo(String source, String packageName) {
        boolean isVideo = isVideo(packageName);
        if (isVideo) {
            II007Service service = ServiceManagerNative.getI007Service();
            if (service != null) {
                try {
                    service.removeVideo(source, packageName);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据状态判断是否是视频APP
     *
     * @param status 回调的状态
     * @return true:视频APP
     * false:非视频APP
     */
    public static boolean checkVideo(String status) {
        return SceneUtils.isVideo(status);
    }

    /**
     * 根据状态判断屏幕是否亮
     *
     * @param status 回调的状态
     * @return true:屏幕亮
     * false:屏幕灭
     */
    public static boolean isScreenOn(String status) {
        return SceneUtils.isScreenOn(status);
    }

    /**
     * 根据状态判断耳机是否插入
     *
     * @param status 回调的状态
     * @return true:耳机插入
     * false:耳机拔出
     */
    public static boolean isHeadSetPlug(String status) {
        return SceneUtils.isHeadSetPlug(status);
    }

    /**
     * 根据状态判断网络是否可用
     *
     * @param status 回调的状态
     * @return true:网络可用
     * false:无法联网
     */
    public static boolean isNetAvailable(String status) {
        return SceneUtils.isNetAvailable(status);
    }

    /**
     * 根据状态判断网络是否为Wi-Fi
     *
     * @param status 回调的状态
     * @return true:Wi-Fi
     * false:非Wi-Fi
     */
    public static boolean isWifi(String status) {
        return SceneUtils.isWifi(status);
    }

    /**
     * 根据状态判断网络是否为4G
     *
     * @param status 回调的状态
     * @return true:4G
     * false:非4G
     */
    public static boolean is4G(String status) {
        return SceneUtils.is4G(status);
    }

    /**
     * 根据状态判断网络是否为3G
     *
     * @param status 回调的状态
     * @return true:3G
     * false:非3G
     */
    public static boolean is3G(String status) {
        return SceneUtils.is3G(status);
    }

    /**
     * 根据状态判断网络是否为2G
     *
     * @param status 回调的状态
     * @return true:2G
     * false:非2G
     */
    public static boolean is2G(String status) {
        return SceneUtils.is2G(status);
    }

    /**
     * 根据状态获取网络类型
     *
     * @param status 回调的状态
     * @return 网络类型
     */
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

    /**
     * 根据状态获取电池状态
     *
     * @param status 回调的状态
     * @return 电池状态
     */
    public static int getBatteryStatus(String status) {
        return SceneUtils.getBatteryStatus(status);
    }

    /**
     * 根据状态获取电池电量
     *
     * @param status 回调的状态
     * @return 电池电量
     */
    public static int getBatteryLevel(String status) {
        return SceneUtils.getBatteryLevel(status);
    }

    /**
     * 根据状态获取电池健康状态
     *
     * @param status 回调的状态
     * @return 电池健康状态
     */
    public static int getBatteryHealth(String status) {
        return SceneUtils.getBatteryHealth(status);
    }

    /**
     * 根据状态获取电池温度
     *
     * @param status 回调的状态
     * @return 电池温度
     */
    public static int getBatteryTemperature(String status) {
        return SceneUtils.getBatteryTemperature(status);
    }

    /**
     * 根据状态获取电池是否插入
     *
     * @param status 回调的状态
     * @return 电池是否插入
     */
    public static int getBatteryPlugged(String status) {
        return SceneUtils.getBatteryPlugged(status);
    }

    /**
     * 自动打开Accessibility服务
     */
    @Deprecated
    public static void autoEnableAccessibilityService() {
        AppUtils.autoEnableAccessibilityService(I007Core.getCore().getContext());
    }

    /**
     * Accessibility服务是否打开
     *
     * @return true:Accessibility服务打开
     */
    public static boolean isServiceEnabled() {
        return AppUtils.isServiceEnabled(I007Core.getCore().getContext());
    }

    /**
     * 跳转到设置的Accessibility管理界面
     */
    public static void openSettingsAccessibilityService() {
        AppUtils.openSettingsAccessibilityService(I007Core.getCore().getContext());
    }
}
