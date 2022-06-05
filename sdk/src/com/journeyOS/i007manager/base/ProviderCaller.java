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
final public class ProviderCaller {
    private static final String TAG = ProviderCaller.class.getSimpleName();

    public static Bundle call(String authority, Context context, String methodName, String arg, Bundle bundle) {
        Uri uri = Uri.parse("content://" + authority);
        ContentResolver contentResolver = context.getContentResolver();
        return contentResolver.call(uri, methodName, arg, bundle);
    }

    public static final class Builder {
        private static final String TAG = Builder.class.getSimpleName();
        private Context context;
        private Bundle bundle = new Bundle();

        private String methodName;
        private String auth;
        private String arg;

        public Builder(Context context) {
            this.context = context;
            this.auth = auth;
        }

        public Builder authority(String auth) {
            this.auth = auth;
            return this;
        }

        public Builder methodName(String name) {
            this.methodName = name;
            return this;
        }

        public Builder arg(String arg) {
            this.arg = arg;
            return this;
        }

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

        public Bundle call() {
            return ProviderCaller.call(auth, context, methodName, arg, bundle);
        }

    }
}