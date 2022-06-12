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

package com.journeyOS.i007manager.base;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @author solo
 */
public final class ProviderCaller {
    private static final String TAG = ProviderCaller.class.getSimpleName();

    /**
     * Call a provider-defined method.
     * This can be used to implement read or write interfaces which are cheaper
     * than using a Cursor and/or do not fit into the traditional table model.
     *
     * @param authority  the authority of the ContentProvider to which this batch should be applied
     * @param context    上下文
     * @param methodName 方法名
     * @param arg        arg
     * @param bundle     bundle
     * @return Bundle
     */
    public static Bundle call(String authority, Context context, String methodName, String arg, Bundle bundle) {
        Uri uri = Uri.parse("content://" + authority);
        ContentResolver contentResolver = context.getContentResolver();
        return contentResolver.call(uri, methodName, arg, bundle);
    }

    /**
     * Builder
     */
    public static final class Builder {
        private static final String TAG = Builder.class.getSimpleName();
        private Context context;
        private Bundle bundle = new Bundle();

        private String methodName;
        private String auth;
        private String arg;

        /**
         * 设置上下文
         *
         * @param context 上下文
         */
        public Builder(Context context) {
            this.context = context;
            this.auth = auth;
        }

        /**
         * 设置authority
         *
         * @param auth authority
         * @return Builder
         */
        public Builder authority(String auth) {
            this.auth = auth;
            return this;
        }

        /**
         * 设置方法名
         *
         * @param name 方法名
         * @return Builder
         */
        public Builder methodName(String name) {
            this.methodName = name;
            return this;
        }

        /**
         * 设置arg
         *
         * @param arg arg
         * @return Builder
         */
        public Builder arg(String arg) {
            this.arg = arg;
            return this;
        }

        /**
         * 设置arg
         *
         * @param key   key
         * @param value value
         * @return Builder
         */
        public Builder addArg(String key, Object value) {
            if (value != null) {
                if (value instanceof IBinder) {
                    bundle.putBinder(key, (IBinder) value);
                } else if (value instanceof Boolean) {
                    bundle.putBoolean(key, (Boolean) value);
                } else if (value instanceof Integer) {
                    bundle.putInt(key, (Integer) value);
                } else if (value instanceof String) {
                    bundle.putString(key, (String) value);
                } else if (value instanceof Serializable) {
                    bundle.putSerializable(key, (Serializable) value);
                } else if (value instanceof Bundle) {
                    bundle.putBundle(key, (Bundle) value);
                } else if (value instanceof Parcelable) {
                    bundle.putParcelable(key, (Parcelable) value);
                } else {
                    throw new IllegalArgumentException("Unknown type " + value.getClass() + " in Bundle.");
                }
            }
            return this;
        }

        /**
         * call
         *
         * @return Bundle
         */
        public Bundle call() {
            return ProviderCaller.call(auth, context, methodName, arg, bundle);
        }

    }
}
