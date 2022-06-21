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

package com.journeyOS.machinelearning.helpers;

import android.util.ArrayMap;

import com.journeyOS.common.SmartLog;

import java.util.ArrayList;
import java.util.Map;

/**
 * This class is only averaging out the time between calls to estimate average durations and fps.
 * Use it like:
 * timeStat.startInterval();
 * ...
 * timeStat.stopInterval("my interval", 10, true);
 * to print 10-period averages duration statistics.
 * <p>
 * Or use it like the following to get the frequency of a call:
 * timeStat.tick("fps", 30);
 * float fps = timeStat.getAverageTickFrequency("fps");
 *
 * @author solo
 */
public class TimeStat {
    private static final String TAG = TimeStat.class.getSimpleName();
    private static final boolean ALSO_LOG_FPS = false;

    private Map<String, ArrayList<Long>> mDurationsAndTicksMap = new ArrayMap<>();
    private long mLastBegin;

    private static float round2(float value) {
        return Math.round(value * 10f) / 10f;
    }

    public void startInterval() {
        mLastBegin = System.currentTimeMillis();
    }

    public void stopInterval(String label, int entries, boolean printMessage) {
        final long duration = System.currentTimeMillis() - mLastBegin;
        if (!mDurationsAndTicksMap.containsKey(label)) {
            mDurationsAndTicksMap.put(label, new ArrayList<>());
        }
        final ArrayList<Long> intervals = mDurationsAndTicksMap.get(label);
        intervals.add(duration);
        while (intervals.size() > entries) {
            intervals.remove(0);
        }

        if (printMessage) {
            final float avgDurationMs = getAverageInterval(label);
            final int avgFps = (int) Math.round(1000. / Math.max(0.1, avgDurationMs));
            SmartLog.d(TAG, label + ": " + avgDurationMs + (ALSO_LOG_FPS ? " (max fps: " + avgFps + ")" : ""));
        }
    }

    public float getAverageInterval(String label) {
        if (!mDurationsAndTicksMap.containsKey(label)) {
            return 0;
        }

        final ArrayList<Long> intervals = mDurationsAndTicksMap.get(label);
        if (intervals.isEmpty()) {
            return 0;
        }

        if (intervals.size() == 1) {
            return intervals.get(0);
        }

        long accum = 0;
        for (int i = 0; i < intervals.size(); i++) {
            accum += intervals.get(i);
        }

        return round2((float) accum / (float) intervals.size());
    }

    public void tick(String label, int entries) {
        long thisTime = System.currentTimeMillis();
        if (!mDurationsAndTicksMap.containsKey(label)) {
            mDurationsAndTicksMap.put(label, new ArrayList<>());
        }

        final ArrayList<Long> ticks = mDurationsAndTicksMap.get(label);
        ticks.add(thisTime);
        while (ticks.size() > (entries + 1)) {
            ticks.remove(0);
        }
    }

    public float getAverageTickFrequency(String label) {
        if (!mDurationsAndTicksMap.containsKey(label)) {
            return 0;
        }

        final ArrayList<Long> ticks = mDurationsAndTicksMap.get(label);
        if (ticks.size() < 2) {
            return 0;
        }

        final long firstTime = ticks.get(0);
        final long lastTime = ticks.get(ticks.size() - 1);
        float avgTime = (float) (lastTime - firstTime) / (float) (ticks.size() - 1);
        return round2((float) (1000. / Math.max(0.1, avgTime)));
    }
}