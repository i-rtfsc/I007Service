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

package com.journeyOS.i007Service.liteprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class LiteSQLHelper extends SQLiteOpenHelper {

    private static final String TAG = LiteSQLHelper.class.getSimpleName();

    private Class<?> mTableClass;

    public LiteSQLHelper(Context context, String fileName, int schemaVersion) {
        super(context, fileName, null, schemaVersion);
    }

    public void setTableClass(Class<?> tableClass) {
        mTableClass = tableClass;
    }

    private Class<?> getTableClass() {
        if (mTableClass != null) {
            return mTableClass;
        } else {
            return getClass();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* Override in derived classes */
        upgradeTables(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* Override in derived classes */
        throw new SQLiteException("Can't downgrade database from version " + oldVersion + " to "
                + newVersion);
    }

    private void createTables(SQLiteDatabase db) {
        for (Class<?> clazz : getTableClass().getClasses()) {
            Table table = clazz.getAnnotation(Table.class);
            Log.d(TAG, "createTables() called with: table = [" + table + "]");
            if (table != null) {
                createTable(db, Utils.getTableName(clazz, table), clazz);
            }
        }
    }

    private void createTable(SQLiteDatabase db, String tableName, Class<?> tableClass) {
        ArrayList<String> columns = new ArrayList<String>();
        for (Field field : tableClass.getFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                try {
                    columns.add(Utils.getColumnConstraint(field, column));
                } catch (Exception e) {
                    Log.e(TAG, "Error accessing " + field, e);
                }
            }
        }

        db.execSQL("CREATE TABLE " + tableName + " (" + TextUtils.join(", ", columns) + ");");
    }

    private void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading Tables: " + oldVersion + " -> " + newVersion);
        for (Class<?> clazz : getTableClass().getClasses()) {
            Table table = clazz.getAnnotation(Table.class);
            Log.d(TAG, "upgradeTables() called with: table = [" + table + "]");
            if (table != null) {
                int since = table.since();
                if (oldVersion < since && newVersion >= since) {
                    createTable(db, Utils.getTableName(clazz, table), clazz);
                } else {
                    upgradeTable(db, oldVersion, newVersion, Utils.getTableName(clazz, table), clazz);
                }
            }
        }
    }

    private void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion, String tableName, Class<?> tableClass) {
        for (Field field : tableClass.getFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                int since = column.since();
                if (oldVersion < since && newVersion >= since) {
                    try {
                        db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + Utils.getColumnConstraint(field, column) + ";");
                    } catch (Exception e) {
                        Log.e(TAG, "Error accessing " + field, e);
                    }
                }
            }
        }
    }

}
