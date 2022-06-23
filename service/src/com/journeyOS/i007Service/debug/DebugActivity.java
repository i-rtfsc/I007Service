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

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyOS.i007manager.I007Core;
import com.journeyOS.i007manager.I007Manager;
import com.journeyOS.i007manager.I007Observer;
import com.journeyOS.i007manager.I007Result;
import com.journeyOS.i007manager.SmartLog;

/**
 * @author solo
 */
public class DebugActivity extends AppCompatActivity {
    private static final String TAG = "solo-debug-Activity";
    private LinearLayout mLayout;
    private Context mContext;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mLayout = new LinearLayout(this);
        mLayout.setOrientation(LinearLayout.VERTICAL);
        initView();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        SmartLog.d(TAG, "init view");
        TextView textView = new TextView(this);
        mLayout.addView(textView);

        Button button = new Button(this);
        button.setText("Set");
        button.setOnClickListener(v -> {
            SmartLog.d(TAG, "set button click");
            init();
        });
        mLayout.addView(button);

        button = new Button(this);
        button.setText("Update");
        button.setOnClickListener(v -> {
            SmartLog.d(TAG, "update button click");
            update();
        });
        mLayout.addView(button);

        button = new Button(this);
        button.setText("Remove");
        button.setOnClickListener(v -> {
            SmartLog.d(TAG, "remove button click");
            remove();
        });
        mLayout.addView(button);

        button = new Button(this);
        button.setText("Ai-Text");
        button.setOnClickListener(v -> {
            SmartLog.d(TAG, "ai-text button click");
            startActivityImpl(AiTextActivity.class);
        });
        mLayout.addView(button);

        button = new Button(this);
        button.setText("Ai-Image");
        button.setOnClickListener(v -> {
            SmartLog.d(TAG, "ai-image button click");
            startActivityImpl(AiImageActivity.class);
        });
        mLayout.addView(button);

        ScrollView sv = new ScrollView(this);
        sv.addView(mLayout);
        setContentView(sv);
    }

    private void init() {
        I007Core.getCore().startup(mContext);
        I007Manager i007m = I007Manager.getInstance();
        i007m.subscribeObserver(new I007Observer() {
            @Override
            public void onSceneChanged(I007Result result) throws RemoteException {
                SmartLog.d(TAG, "onSceneChanged() called with: result = [" + result.toString() + "]");
            }
        });

        i007m.setFactor(I007Manager.SCENE_FACTOR_APP | I007Manager.SCENE_FACTOR_LCD);
    }

    private void update() {
        I007Manager i007m = I007Manager.getInstance();
        i007m.updateFactor(I007Manager.SCENE_FACTOR_BATTERY);
    }

    private void remove() {
        I007Manager i007m = I007Manager.getInstance();
        i007m.removeFactor(I007Manager.SCENE_FACTOR_BATTERY);
    }

    private void startActivityImpl(Class<?> cls) {
        try {
            Intent intent = new Intent(this, cls);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

}
