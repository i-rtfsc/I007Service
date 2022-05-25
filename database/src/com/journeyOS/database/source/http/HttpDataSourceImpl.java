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

package com.journeyOS.database.source.http;

import android.content.Context;

import com.journeyOS.database.source.api.HttpDataSource;

/**
 * data from network
 *
 * @author solo
 */
public class HttpDataSourceImpl implements HttpDataSource {
    private static final String TAG = HttpDataSourceImpl.class.getSimpleName();
    private volatile static HttpDataSourceImpl INSTANCE = null;

    private Context mContext;

    private HttpDataSourceImpl(Context context) {
        mContext = context;
    }

    /**
     * 获取HttpDataSourceImpl实例
     *
     * @param context 上下文
     * @return HttpDataSourceImpl
     */
    public static HttpDataSourceImpl getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (HttpDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpDataSourceImpl(context);

                }
            }
        }
        return INSTANCE;
    }
}
