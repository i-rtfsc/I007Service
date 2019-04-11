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

package com.journeyOS.i007Service.core.notification;

import android.app.PendingIntent;
import android.os.Build;

public class Notification extends android.app.Notification {

    private String mPackageName;

    private Notification(String packageName) {
        mPackageName = packageName;
    }

    public static Notification create(android.app.Notification n, String packageName) {
        Notification notification = new Notification(packageName);
        clone(n, notification);
        return notification;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public String getText() {
        return extras.getString(EXTRA_TEXT);
    }

    public String getTitle() {
        return extras.getString(EXTRA_TITLE);
    }

    public void click() {
        try {
            this.contentIntent.send();
        } catch (PendingIntent.CanceledException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete() {
        try {
            this.deleteIntent.send();
        } catch (PendingIntent.CanceledException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return "Notification{" +
                    "packageName='" + mPackageName + "', " +
                    "title='" + getTitle() + ", " +
                    "text='" + getText() + "'" +
                    "} ";
        }
        return super.toString();
    }

    public static void clone(android.app.Notification from, android.app.Notification to) {
        to.when = from.when;
        to.icon = from.icon;
        to.iconLevel = from.iconLevel;
        to.number = from.number;
        to.contentIntent = from.contentIntent;
        to.deleteIntent = from.deleteIntent;
        to.fullScreenIntent = from.fullScreenIntent;
        to.tickerText = from.tickerText;
        to.tickerView = from.tickerView;
        to.contentView = from.contentView;
        to.bigContentView = from.bigContentView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            to.headsUpContentView = from.headsUpContentView;
            to.audioAttributes = from.audioAttributes;
            to.color = from.color;
            to.visibility = from.visibility;
            to.category = from.category;
            to.publicVersion = from.publicVersion;
        }
        to.largeIcon = from.largeIcon;
        to.sound = from.sound;
        to.audioStreamType = from.audioStreamType;
        to.vibrate = from.vibrate;
        to.ledARGB = from.ledARGB;
        to.ledOnMS = from.ledOnMS;
        to.ledOffMS = from.ledOffMS;
        to.defaults = from.defaults;
        to.flags = from.flags;
        to.priority = from.priority;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            to.extras = from.extras;
            to.actions = from.actions;
        }
    }

}
