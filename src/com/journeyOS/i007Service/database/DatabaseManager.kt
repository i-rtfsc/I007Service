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

package com.journeyOS.i007Service.database

import com.journeyOS.i007Service.core.I007Core

object DatabaseManager {
    private val TAG = DatabaseManager::class.java.simpleName as String
    lateinit var appDao: AppDao
    private val mLock = Any()

    //game dock config cache
    private val mCacheApps: HashMap<String, App> = HashMap()

    init {
        val database = DBHelper.provider(I007Core.getCore().context, I007Database::class.java, DBConfigs.DB_NAME)
        appDao = database.appDao()
    }

    fun queryApp(packageName: String): App? {
        var app: App? = null
        synchronized(mLock) {
            app = mCacheApps.get(packageName)
            if (app == null) {
                app = appDao.searchApp(packageName)
                app?.let {
                    mCacheApps.put(packageName, it)
                }
            }
            return app
        }
    }

    fun addApp(packageName: String, type: String): Boolean {
        synchronized(mLock) {
            try {
                val app = App()
                app.packageName = packageName
                app.type = type
                appDao.insert(app)
                return true
            } catch (e: Throwable) {
                e.printStackTrace()
            }

        }
        return false
    }
}