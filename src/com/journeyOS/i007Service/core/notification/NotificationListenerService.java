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

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaMetadataEditor;
import android.media.MediaMetadataRetriever;
import android.media.RemoteController;
import android.media.session.MediaSessionManager;
import android.service.notification.StatusBarNotification;

import com.journeyOS.i007Service.core.ServiceLifecycleListener;

import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationListenerService extends android.service.notification.NotificationListenerService implements RemoteController.OnClientUpdateListener {
    private static final String TAG = NotificationListenerService.class.getSimpleName();

    private CopyOnWriteArrayList<NotificationListener> mNotificationListeners = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<ServiceLifecycleListener> mLifecycleListeners = new CopyOnWriteArrayList<>();
    private static NotificationListenerService sInstance;

    private AudioManager mAudioManager;
    private MediaSessionManager mMediaSessionManager;
    private RemoteController mRemoteController;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mAudioManager = (AudioManager) sInstance.getSystemService(Context.AUDIO_SERVICE);
        registerRemoteController();
        for (ServiceLifecycleListener listener : mLifecycleListeners) {
            listener.onRunning();
        }
    }

    public static NotificationListenerService getInstance() {
        return sInstance;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        for (NotificationListener listener : mNotificationListeners) {
            listener.onNotification(sbn, Notification.create(
                    sbn.getNotification(), sbn.getPackageName()));
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        for (NotificationListener listener : mNotificationListeners) {
            listener.onNotificationRemoved(sbn, null);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        for (NotificationListener listener : mNotificationListeners) {
            listener.onNotificationRemoved(sbn, rankingMap);
        }
    }

    public void addListener(NotificationListener listener) {
        mNotificationListeners.add(listener);
    }

    public boolean removeListener(NotificationListener listener) {
        return mNotificationListeners.remove(listener);
    }


    public void addLifecycleListener(ServiceLifecycleListener listener) {
        mLifecycleListeners.add(listener);
    }

    public boolean removeLifecycleListener(ServiceLifecycleListener listener) {
        return mLifecycleListeners.remove(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (ServiceLifecycleListener listener : mLifecycleListeners) {
            listener.onStoping();
        }
        sInstance = null;
    }


    public void registerRemoteController() {
        if (sInstance == null) {
            return;
        }

        mMediaSessionManager = (MediaSessionManager) sInstance.getSystemService(Context.MEDIA_SESSION_SERVICE);
        mRemoteController = new RemoteController(sInstance, this);
        boolean registered = false;
        try {
            registered = mAudioManager.registerRemoteController(mRemoteController);
        } catch (NullPointerException e) {
            registered = false;
        }
        if (registered) {
            try {
                mRemoteController.setArtworkConfiguration(500, 500);
                mRemoteController.setSynchronizationMode(RemoteController.POSITION_SYNCHRONIZATION_CHECK);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClientChange(boolean b) {
//        DebugUtils.d(TAG, "onClientChange() called with: b = [" + b + "]");
    }

    @Override
    public void onClientPlaybackStateUpdate(int i) {
//        DebugUtils.d(TAG, "onClientPlaybackStateUpdate() called with: i = [" + i + "]");
    }

    @Override
    public void onClientPlaybackStateUpdate(int i, long l, long l1, float v) {
//        DebugUtils.d(TAG, "onClientPlaybackStateUpdate() called with: i = [" + i + "], l = [" + l + "], l1 = [" + l1 + "], v = [" + v + "]");
    }

    @Override
    public void onClientTransportControlUpdate(int i) {
//        DebugUtils.d(TAG, "onClientTransportControlUpdate() called with: i = [" + i + "]");
    }

    @Override
    public void onClientMetadataUpdate(RemoteController.MetadataEditor metadataEditor) {
        MusicMetadata music = new MusicMetadata();
        music.title = metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_TITLE, "");
        music.singer = metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_ARTIST, "");
        music.album = metadataEditor.getBitmap(MediaMetadataEditor.BITMAP_KEY_ARTWORK, null);

        for (NotificationListener listener : mNotificationListeners) {
            listener.onMusicMetadataUpdate(music);
        }
    }
}
