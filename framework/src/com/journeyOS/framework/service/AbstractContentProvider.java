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

package com.journeyOS.framework.service;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

/**
 * ContentProvider基类
 *
 * @author solo
 */
public abstract class AbstractContentProvider extends ContentProvider {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreate() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle call(@NonNull String method, String arg, Bundle extras) {
        return super.call(method, arg, extras);
    }
}
