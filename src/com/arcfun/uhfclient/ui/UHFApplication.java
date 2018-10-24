package com.arcfun.uhfclient.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import cn.jpush.android.api.JPushInterface;

import com.arcfun.uhfclient.data.GoodInfo;
import com.reader.helper.ReaderHelper;

public class UHFApplication extends Application {
    private List<Activity> activities = new ArrayList<Activity>();
    private List<GoodInfo> mInfos = new ArrayList<GoodInfo>();

    public List<GoodInfo> getInfos() {
        return mInfos;
    }

    public synchronized void setInfos(List<GoodInfo> infos) {
        this.mInfos = infos;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        try {
            ReaderHelper.setContext(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        for (Activity activity : activities) {
            try {
                activity.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.exit(0);
    };

}