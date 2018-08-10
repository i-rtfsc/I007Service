package com.journeyOS.monitor;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.journeyOS.i007.core.I007Core;

public class MonitorApplication extends Application {
    private Application mApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    I007Core.getCore().running(mApplication);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }
}
