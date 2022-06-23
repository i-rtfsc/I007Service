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

package com.journeyOS.i007Service.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.journeyOS.i007Service.debug.DebugActivity;
import com.journeyOS.i007manager.SmartLog;

/**
 * SecretCodeReceiver
 *
 * @author solo
 */
public class SecretCodeReceiver extends BroadcastReceiver {
    private static final String SECRET_CODE_DEBUG_FOR_TESTER = "001007";
    private static final String TAG = SecretCodeReceiver.class.getSimpleName();

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getData() == null || intent.getData().getHost().isEmpty()) {
            SmartLog.e(TAG, "onReceive: undefined secret code!");
            return;
        }

        switch (intent.getData().getHost()) {
            case SECRET_CODE_DEBUG_FOR_TESTER:
                ComponentName componentName = new ComponentName(context.getPackageName(), DebugActivity.class.getName());
                Intent intentDft = new Intent(Intent.ACTION_DEFAULT);
                intentDft.setComponent(componentName);
                intentDft.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intentDft.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentDft);
                break;
            default:
                Toast.makeText(context, "undefined secret code = " + intent.getData().getHost(), Toast.LENGTH_SHORT).show();
        }
    }
}

