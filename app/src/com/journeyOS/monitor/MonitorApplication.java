package com.journeyOS.monitor;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.journeyOS.i007Service.core.I007Core;
import com.journeyOS.i007Service.hook.HookManager;

public class MonitorApplication extends Application {
    private Application mApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        I007Core.getCore().running(mApplication);
        HookManager.applyHooks(mApplication);
    }
}
