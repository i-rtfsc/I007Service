package com.journeyOS.i007Service.core;

import android.os.BatteryManager;

import com.journeyOS.i007Service.I007Manager;
import com.journeyOS.i007Service.base.utils.BaseUitls;
import com.journeyOS.i007Service.base.utils.DebugUtils;
import com.journeyOS.i007Service.database.App;
import com.journeyOS.i007Service.database.DatabaseManager;

import static com.journeyOS.i007Service.DataResource.FACTORY;
import static com.journeyOS.i007Service.core.NotifyManager.POS_FISRT;
import static com.journeyOS.i007Service.core.NotifyManager.POS_FISRT_APP;
import static com.journeyOS.i007Service.core.NotifyManager.POS_FISRT_HEADSET;
import static com.journeyOS.i007Service.core.NotifyManager.POS_FISRT_LCD;
import static com.journeyOS.i007Service.core.NotifyManager.POS_FISRT_NET;
import static com.journeyOS.i007Service.core.NotifyManager.POS_FISRT_NETTYPE;
import static com.journeyOS.i007Service.core.NotifyManager.POS_SECOND;
import static com.journeyOS.i007Service.core.NotifyManager.POS_SECOND_BATTERY_HEALTH;
import static com.journeyOS.i007Service.core.NotifyManager.POS_SECOND_BATTERY_LEVEL_END;
import static com.journeyOS.i007Service.core.NotifyManager.POS_SECOND_BATTERY_LEVEL_START;
import static com.journeyOS.i007Service.core.NotifyManager.POS_SECOND_BATTERY_STATUS;
import static com.journeyOS.i007Service.core.NotifyManager.POS_THIRD;
import static com.journeyOS.i007Service.core.NotifyManager.POS_THIRD_BATTERY_PLUGGED;
import static com.journeyOS.i007Service.core.NotifyManager.POS_THIRD_BATTERY_TEMPERATURE_END;
import static com.journeyOS.i007Service.core.NotifyManager.POS_THIRD_BATTERY_TEMPERATURE_START;
import static com.journeyOS.i007Service.core.detect.PackageNameMonitor.ALBUM;
import static com.journeyOS.i007Service.core.detect.PackageNameMonitor.BROWSER;
import static com.journeyOS.i007Service.core.detect.PackageNameMonitor.GAME;
import static com.journeyOS.i007Service.core.detect.PackageNameMonitor.IM;
import static com.journeyOS.i007Service.core.detect.PackageNameMonitor.MUSIC;
import static com.journeyOS.i007Service.core.detect.PackageNameMonitor.NEWS;
import static com.journeyOS.i007Service.core.detect.PackageNameMonitor.READER;
import static com.journeyOS.i007Service.core.detect.PackageNameMonitor.VIDEO;

public class SceneUtils {
    public static final String SEPARATOR = "-";
    private static final String TAG = SceneUtils.class.getSimpleName();
    private static final boolean DEBUG = false;
    private static final int LENGTH = 5;

    private static String[] getAllWords(String status) {
        if (status == null) {
            DebugUtils.w(TAG, "status was NULL");
            return null;
        }
        String[] words = status.split(SEPARATOR);
//        if (DEBUG) {
//            for (String word : words) {
//                DebugUtils.w(TAG, "get all word = " + word);
//            }
//        }
        return words;
    }

    private static String getFisrtWords(String status) {
        String[] allWords = getAllWords(status);
        return getFisrtWords(allWords);
    }

    private static String getFisrtWords(String[] words) {
        if (words != null && words.length > 0) {
            String word = words[0];
            if (DEBUG) DebugUtils.w(TAG, "get first word = " + word);
            return word;
        } else {
            return null;
        }
    }


    private static String getSecondWords(String status) {
        String[] allWords = getAllWords(status);
        return getSecondWords(allWords);
    }

    private static String getSecondWords(String[] words) {
        if (words != null && words.length > 1) {
            String word = words[1];
            if (DEBUG) DebugUtils.w(TAG, "get second word = " + word);
            return word;
        } else {
            return null;
        }
    }

    private static String getThirdWords(String status) {
        String[] allWords = getAllWords(status);
        return getThirdWords(allWords);
    }

    private static String getThirdWords(String[] words) {
        if (words != null && words.length > 2) {
            String word = words[2];
            if (DEBUG) DebugUtils.w(TAG, "get third word = " + word);
            return word;
        } else {
            return null;
        }
    }

    private static String getFourthWords(String status) {
        String[] allWords = getAllWords(status);
        return getThirdWords(allWords);
    }

    private static String getFourthWords(String[] words) {
        if (words != null && words.length > 3) {
            String word = words[3];
            if (DEBUG) DebugUtils.w(TAG, "get third word = " + word);
            return word;
        } else {
            return null;
        }
    }

    public static String getAppChar(String packageName) {
        App app = DatabaseManager.getDefault().queryApp(packageName);
        if (ALBUM.equals(app.type)) {
            return I007Manager.SCENE_APP_STATE_ALBUM;
        } else if (BROWSER.equals(app.type)) {
            return I007Manager.SCENE_APP_STATE_BROWSER;
        } else if (GAME.equals(app.type)) {
            return I007Manager.SCENE_APP_STATE_GAME;
        } else if (IM.equals(app.type)) {
            return I007Manager.SCENE_APP_STATE_IM;
        } else if (MUSIC.equals(app.type)) {
            return I007Manager.SCENE_APP_STATE_MUSIC;
        } else if (NEWS.equals(app.type)) {
            return I007Manager.SCENE_APP_STATE_NEWS;
        } else if (READER.equals(app.type)) {
            return I007Manager.SCENE_APP_STATE_READER;
        } else if (VIDEO.equals(app.type)) {
            return I007Manager.SCENE_APP_STATE_VIDEO;
        } else {
            return I007Manager.SCENE_APP_STATE_DEFAULT;
        }
    }

    public static String parseData(long factoryId, String data, String status) {
        FACTORY factory = I007Manager.getFactory(factoryId);
        String[] allWords = SceneUtils.getAllWords(status);
        switch (factory) {
            case APP:
                char appChar = getAppChar(data).charAt(0);
                char[] appWord = getFisrtWords(allWords).toCharArray();
                appWord[POS_FISRT_APP] = appChar;
                allWords[POS_FISRT] = new String(appWord);
                return BaseUitls.join(allWords, SceneUtils.SEPARATOR);
            case LCD:
                char lcdChar = data.toUpperCase().charAt(0);
                char[] lcdWord = getFisrtWords(allWords).toCharArray();
                lcdWord[POS_FISRT_LCD] = lcdChar;
                allWords[POS_FISRT] = new String(lcdWord);
                return BaseUitls.join(allWords, SceneUtils.SEPARATOR);
            case NET:
                char netChar = data.toUpperCase().charAt(0);
                char netTypeChar = data.toUpperCase().charAt(1);
                char[] netWord = getFisrtWords(allWords).toCharArray();
                netWord[POS_FISRT_NET] = netChar;
                netWord[POS_FISRT_NETTYPE] = netTypeChar;
                allWords[POS_FISRT] = new String(netWord);
                return BaseUitls.join(allWords, SceneUtils.SEPARATOR);
            case HEADSET:
                char headsetChar = data.toUpperCase().charAt(0);
                char[] headsetWord = getFisrtWords(allWords).toCharArray();
                headsetWord[POS_FISRT_HEADSET] = headsetChar;
                allWords[POS_FISRT] = new String(headsetWord);
                return BaseUitls.join(allWords, SceneUtils.SEPARATOR);
            case BATTERY:
                String[] battery = data.toUpperCase().split(SEPARATOR);
                allWords[POS_SECOND] = battery[0];
                allWords[POS_THIRD] = battery[1];
                return BaseUitls.join(allWords, SceneUtils.SEPARATOR);
        }
        return status;
    }

    public static boolean isGame(String status) {
        String app = getFisrtWords(status);
        if (app != null) {
            return I007Manager.SCENE_APP_STATE_GAME.equals(String.valueOf(app.charAt(POS_FISRT_APP)));
        }
        return false;
    }

    public static boolean isAlbum(String status) {
        String app = getFisrtWords(status);
        if (app != null) {
            return I007Manager.SCENE_APP_STATE_ALBUM.equals(String.valueOf(app.charAt(POS_FISRT_APP)));
        }
        return false;
    }

    public static boolean isBrowser(String status) {
        String app = getFisrtWords(status);
        if (app != null) {
            return I007Manager.SCENE_APP_STATE_BROWSER.equals(String.valueOf(app.charAt(POS_FISRT_APP)));
        }
        return false;
    }

    public static boolean isIM(String status) {
        String app = getFisrtWords(status);
        if (app != null) {
            return I007Manager.SCENE_APP_STATE_IM.equals(String.valueOf(app.charAt(POS_FISRT_APP)));
        }
        return false;
    }

    public static boolean isMusic(String status) {
        String app = getFisrtWords(status);
        if (app != null) {
            return I007Manager.SCENE_APP_STATE_MUSIC.equals(String.valueOf(app.charAt(POS_FISRT_APP)));
        }
        return false;
    }

    public static boolean isNews(String status) {
        String app = getFisrtWords(status);
        if (app != null) {
            return I007Manager.SCENE_APP_STATE_NEWS.equals(String.valueOf(app.charAt(POS_FISRT_APP)));
        }
        return false;
    }

    public static boolean isReader(String status) {
        String app = getFisrtWords(status);
        if (app != null) {
            return I007Manager.SCENE_APP_STATE_READER.equals(String.valueOf(app.charAt(POS_FISRT_APP)));
        }
        return false;
    }

    public static boolean isVideo(String status) {
        String app = getFisrtWords(status);
        if (app != null) {
            return I007Manager.SCENE_APP_STATE_VIDEO.equals(String.valueOf(app.charAt(POS_FISRT_APP)));
        }
        return false;
    }

    public static boolean isScreenOn(String status) {
        String lcd = getFisrtWords(status);
        if (lcd != null) {
            return I007Manager.SCENE_LCD_STATE_ON.equals(String.valueOf(lcd.charAt(POS_FISRT_LCD)));
        }
        return false;
    }

    public static boolean isNetAvailable(String status) {
        String net = getFisrtWords(status);
        if (net != null) {
            return I007Manager.SCENE_NET_STATE_ON.equals(String.valueOf(net.charAt(POS_FISRT_NET)));
        }
        return false;
    }

    public static boolean isWifi(String status) {
        String net = getFisrtWords(status);
        if (net != null) {
            return I007Manager.SCENE_NET_STATE_TYPE_WIFI.equals(String.valueOf(net.charAt(POS_FISRT_NETTYPE)));
        }
        return false;
    }

    public static boolean is4G(String status) {
        String net = getFisrtWords(status);
        if (net != null) {
            return I007Manager.SCENE_NET_STATE_TYPE_4G.equals(String.valueOf(net.charAt(POS_FISRT_NETTYPE)));
        }
        return false;
    }

    public static boolean is3G(String status) {
        String net = getFisrtWords(status);
        if (net != null) {
            return I007Manager.SCENE_NET_STATE_TYPE_3G.equals(String.valueOf(net.charAt(POS_FISRT_NETTYPE)));
        }
        return false;
    }

    public static boolean is2G(String status) {
        String net = getFisrtWords(status);
        if (net != null) {
            return I007Manager.SCENE_NET_STATE_TYPE_2G.equals(String.valueOf(net.charAt(POS_FISRT_NETTYPE)));
        }
        return false;
    }

    public static boolean isHeadSetPlug(String status) {
        String headSet = getFisrtWords(status);
        if (headSet != null) {
            return I007Manager.SCENE_HEADSET_STATE_ON.equals(String.valueOf(headSet.charAt(POS_FISRT_HEADSET)));
        }
        return false;
    }

    public static int getBatteryStatus(String status) {
        String battery = getSecondWords(status);
        if (battery != null) {
            String bs = String.valueOf(battery.charAt(POS_SECOND_BATTERY_STATUS));
            return Integer.parseInt(bs);
        }
        return BatteryManager.BATTERY_STATUS_UNKNOWN;
    }

    public static int getBatteryLevel(String status) {
        String battery = getSecondWords(status);
        if (battery != null) {
            String bl = String.valueOf(battery.substring(POS_SECOND_BATTERY_LEVEL_START, POS_SECOND_BATTERY_LEVEL_END));
            return Integer.parseInt(bl);
        }
        return -1;
    }

    public static int getBatteryHealth(String status) {
        String battery = getSecondWords(status);
        if (battery != null) {
            String bs = String.valueOf(battery.charAt(POS_SECOND_BATTERY_HEALTH));
            return Integer.parseInt(bs);
        }
        return BatteryManager.BATTERY_HEALTH_UNKNOWN;
    }

    public static int getBatteryTemperature(String status) {
        String battery = getThirdWords(status);
        if (battery != null) {
            String bt = String.valueOf(battery.substring(POS_THIRD_BATTERY_TEMPERATURE_START, POS_THIRD_BATTERY_TEMPERATURE_END));
            return Integer.parseInt(bt);
        }
        return -1;
    }

    public static int getBatteryPlugged(String status) {
        String battery = getThirdWords(status);
        if (battery != null) {
            String bt = String.valueOf(battery.charAt(POS_THIRD_BATTERY_PLUGGED));
            return Integer.parseInt(bt);
        }
        return -1;
    }

}
