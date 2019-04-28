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

package com.journeyOS.i007Service.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.journeyOS.i007Service.base.constants.Constant;
import com.journeyOS.i007Service.base.security.AESUtils;
import com.journeyOS.i007Service.base.utils.DebugUtils;
import com.journeyOS.i007Service.base.utils.FileUtils;
import com.journeyOS.i007Service.base.utils.JsonHelper;
import com.journeyOS.i007Service.base.utils.Singleton;
import com.journeyOS.i007Service.base.utils.SpUtils;
import com.journeyOS.i007Service.core.I007Core;

import java.util.ArrayList;
import java.util.List;


public class DBOperate {
    private static final String TAG = DBOperate.class.getSimpleName();
    private static final Singleton<DBOperate> gDefault = new Singleton<DBOperate>() {
        @Override
        protected DBOperate create() {
            return new DBOperate();
        }
    };
    private Context mContext;
    private ContentResolver mCr;

    private DBOperate() {
        mContext = I007Core.getCore().getContext();
        this.mCr = mContext.getContentResolver();
    }

    public static DBOperate getDefault() {
        return gDefault.get();
    }

    private Boolean isExists(Uri uri, String packageName) {
        String localPackageName = null;
        Cursor cursor = null;

        try {
            cursor = mCr.query(uri, null, DBConfig.PACKAGE_NAME + "=?", new String[]{packageName}, null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    localPackageName = cursor.getString(cursor.getColumnIndex(DBConfig.PACKAGE_NAME));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return packageName.equals(localPackageName);
    }

    public void initTableApp() {
        DebugUtils.d(TAG, "init app table!");
        String game = AESUtils.decrypt(FileUtils.readFileFromAsset(mContext, FileUtils.GAME));
        if (game != null) {
            Apps appInfo = JsonHelper.fromJson(game, Apps.class);
            if (appInfo != null) {
                List<App> gameApps = appInfo.apps;
                saveOrUpdate(DBConfig.APPS_URL, gameApps);
            }
        }

        String album = AESUtils.decrypt(FileUtils.readFileFromAsset(mContext, FileUtils.ALBUM));
        if (album != null) {
            Apps appInfo = JsonHelper.fromJson(album, Apps.class);
            if (appInfo != null) {
                List<App> albumApps = appInfo.apps;
                saveOrUpdate(DBConfig.APPS_URL, albumApps);
            }
        }

        String browser = AESUtils.decrypt(FileUtils.readFileFromAsset(mContext, FileUtils.BROWSER));
        if (browser != null) {
            Apps appInfo = JsonHelper.fromJson(browser, Apps.class);
            if (appInfo != null) {
                List<App> browserApps = appInfo.apps;
                saveOrUpdate(DBConfig.APPS_URL, browserApps);
            }
        }

        String im = AESUtils.decrypt(FileUtils.readFileFromAsset(mContext, FileUtils.IM));
        if (im != null) {
            Apps appInfo = JsonHelper.fromJson(im, Apps.class);
            if (appInfo != null) {
                List<App> imApps = appInfo.apps;
                saveOrUpdate(DBConfig.APPS_URL, imApps);
            }
        }

        String music = AESUtils.decrypt(FileUtils.readFileFromAsset(mContext, FileUtils.MUSIC));
        if (music != null) {
            Apps appInfo = JsonHelper.fromJson(music, Apps.class);
            if (appInfo != null) {
                List<App> musicApps = appInfo.apps;
                saveOrUpdate(DBConfig.APPS_URL, musicApps);
            }
        }

        String news = AESUtils.decrypt(FileUtils.readFileFromAsset(mContext, FileUtils.NEWS));
        if (news != null) {
            Apps appInfo = JsonHelper.fromJson(news, Apps.class);
            if (appInfo != null) {
                List<App> newsApps = appInfo.apps;
                saveOrUpdate(DBConfig.APPS_URL, newsApps);
            }
        }

        String reader = AESUtils.decrypt(FileUtils.readFileFromAsset(mContext, FileUtils.READER));
        if (reader != null) {
            Apps appInfo = JsonHelper.fromJson(reader, Apps.class);
            if (appInfo != null) {
                List<App> readerApps = appInfo.apps;
                saveOrUpdate(DBConfig.APPS_URL, readerApps);
            }
        }

        String video = AESUtils.decrypt(FileUtils.readFileFromAsset(mContext, FileUtils.VIDEO));
        if (video != null) {
            Apps appInfo = JsonHelper.fromJson(video, Apps.class);
            if (appInfo != null) {
                List<App> videoApps = appInfo.apps;
                saveOrUpdate(DBConfig.APPS_URL, videoApps);
                SpUtils.getInstant().put(Constant.DB_INIT, Integer.parseInt(appInfo.version));
            }
        }

        //init "blapps" table
        String bl = FileUtils.readFileFromAsset(mContext, FileUtils.BL);
        if (bl != null) {
            Apps appInfo = JsonHelper.fromJson(bl, Apps.class);
            if (appInfo != null) {
                List<App> blApps = appInfo.apps;
                saveOrUpdate(DBConfig.BL_APPS_URL, blApps);
            }
        }
    }

    public void saveOrUpdate(Uri uri, List<App> apps) {
        for (App app : apps) {
            saveOrUpdate(uri, app);
        }
    }

    public void saveOrUpdate(Uri uri, App app) {
        if (app == null) {
            return;
        }
        DebugUtils.d(TAG, "save or update app = [" + app.toString() + "]");

        ContentValues values = new ContentValues();
        String packageName = app.packageName;
        values.put(DBConfig.PACKAGE_NAME, packageName);
        values.put(DBConfig.TYPE, app.type);
        if (isExists(uri, packageName)) {
            int result = mCr.update(uri, values, DBConfig.PACKAGE_NAME + "=?", new String[]{packageName});
            DebugUtils.d(TAG, "update app, result = [" + result + "]");
        } else {
            mCr.insert(uri, values);
        }
    }

    public App queryApp(Uri uri, String packageName) {
        App app = new App();

        Cursor cursor = null;
        try {
            cursor = mCr.query(uri, null, DBConfig.PACKAGE_NAME + " = ?", new String[]{packageName}, null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    app.packageName = cursor.getString(cursor.getColumnIndex(DBConfig.PACKAGE_NAME));
                    app.type = cursor.getString(cursor.getColumnIndex(DBConfig.TYPE));
                    app.subType = cursor.getString(cursor.getColumnIndex(DBConfig.SUB_TYPE));
                } while (cursor.moveToNext());
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return app;
    }

    @Deprecated
    public List<App> queryApps(Uri uri) {
        List<App> apps = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = mCr.query(uri, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    App app = new App();
                    app.packageName = cursor.getString(cursor.getColumnIndex(DBConfig.PACKAGE_NAME));
                    app.type = cursor.getString(cursor.getColumnIndex(DBConfig.TYPE));
                    app.subType = cursor.getString(cursor.getColumnIndex(DBConfig.SUB_TYPE));
                    apps.add(app);
                } while (cursor.moveToNext());
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return apps;
    }

    public List<App> queryApps(Uri uri, String type) {
        List<App> apps = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = mCr.query(uri, null, DBConfig.TYPE + " = ?", new String[]{type}, null);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    App app = new App();
                    app.packageName = cursor.getString(cursor.getColumnIndex(DBConfig.PACKAGE_NAME));
                    app.type = cursor.getString(cursor.getColumnIndex(DBConfig.TYPE));
                    app.subType = cursor.getString(cursor.getColumnIndex(DBConfig.SUB_TYPE));
                    apps.add(app);
                } while (cursor.moveToNext());
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return apps;
    }

    public void deleteApp(Uri uri, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        try {
            int count = mCr.delete(uri, DBConfig.PACKAGE_NAME + "=?", new String[]{packageName});
            DebugUtils.d(TAG, "delete = " + count);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
