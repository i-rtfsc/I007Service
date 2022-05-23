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

package com.journeyOS.i007Service.debug;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyOS.common.SmartLog;
import com.journeyOS.i007manager.I007Core;
import com.journeyOS.i007manager.I007Manager;
import com.journeyOS.i007manager.II007Listener;

/**
 * @author solo
 */
public class DebugActivity extends AppCompatActivity {
    private static final String TAG = "solo-debug-Activity";
    private LinearLayout mLayout;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mLayout = new LinearLayout(this);
        mLayout.setOrientation(LinearLayout.VERTICAL);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        SmartLog.d(TAG, "init view");
        TextView textView = new TextView(this);
        mLayout.addView(textView);

        Button button = new Button(this);
        button.setText("Text");
        button.setOnClickListener(v -> {
            SmartLog.d(TAG, "text button click");
            test();
        });
        mLayout.addView(button);

        button = new Button(this);
        button.setText("Image");
        button.setOnClickListener(v -> {
            SmartLog.d(TAG, "image button click");
            //TODO
        });
        mLayout.addView(button);

        ScrollView sv = new ScrollView(this);
        sv.addView(mLayout);
        setContentView(sv);
    }

    private void test() {
        I007Core.getCore().startup(mContext);
        I007Manager.getInstance(mContext).registerListener(I007Manager.SCENE_FACTOR_APP,
                new II007Listener.Stub() {
                    @Override
                    public void onSceneChanged(long factorId, String status, String packageName) throws RemoteException {

                    }
                });
    }
}
