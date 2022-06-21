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

package com.journeyOS.i007test;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.journeyOS.i007manager.I007Core;
import com.journeyOS.i007manager.I007Manager;
import com.journeyOS.i007manager.I007Observer;
import com.journeyOS.i007manager.I007Result;

/**
 * @author solo
 */
public class TestActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TestActivity";

    private TextView resultTextView;
    private ScrollView scrollView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Log.v(TAG, "onCreate");

        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(this);
        resultTextView = findViewById(R.id.result_text_view);
        scrollView = findViewById(R.id.scroll_view);

        I007Core.getCore().startup(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                test();
                break;
            default:
                break;
        }
    }

    private void test() {
        I007Manager i007m = I007Manager.getInstance(this);
        Log.d(TAG, "I007Manager = [" + i007m + "]");
        i007m.subscribeObserver(new I007Observer() {
            @Override
            public void onSceneChanged(I007Result result) throws RemoteException {
                Log.d(TAG, "onSceneChanged() called with: result = [" + result.toString() + "]");
            }
        });

        i007m.setFactor(I007Manager.SCENE_FACTOR_BATTERY | I007Manager.SCENE_FACTOR_LCD);
    }
}
