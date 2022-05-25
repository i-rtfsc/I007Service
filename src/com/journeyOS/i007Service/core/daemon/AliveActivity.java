/*
 * Copyright (c) 2019 anqi.huang@outlook.com
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

package com.journeyOS.i007Service.core.daemon;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.journeyOS.i007Service.base.utils.DebugUtils;

public class AliveActivity extends Activity {
    private static final String TAG = AliveActivity.class.getSimpleName();
    public static final boolean DEBUG = true;
    private static final String KEEP_ALIVE = "com.journeyOS.i007Service.daemon.keep_alive";

    private static Activity activity = null;

    public static void navigationActivity(Context context) {
        if (activity != null) {
            DebugUtils.d(TAG, "alive activity was run");
            return;
        }
        try {
            DebugUtils.d(TAG, "start daemon activity!");
            Intent intent = new Intent();
            intent.setAction(KEEP_ALIVE);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            DebugUtils.d(TAG, e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) DebugUtils.d(TAG, "onCreate() called");
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);

        activity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DEBUG) DebugUtils.d(TAG, "onResume() called");
        checkScreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (DEBUG) DebugUtils.d(TAG, "onDestroy() called");
        //start service
    }


    void checkScreen() {
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (isScreenOn) {
            destroy();
        }
    }

    public static void destroy() {
        if (activity != null) {
            activity.finish();
            activity = null;
        }
    }
}
