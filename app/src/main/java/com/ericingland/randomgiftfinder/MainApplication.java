package com.ericingland.randomgiftfinder;

import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        //  instance = this;
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        //  return instance.getApplicationContext();
        return mContext;
    }
}
