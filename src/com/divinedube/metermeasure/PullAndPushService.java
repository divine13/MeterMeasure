package com.divinedube.metermeasure;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Divine Dube on 2014/07/13.
 */

public class PullAndPushService extends IntentService {
    private static String TAG = PullAndPushService.class.getSimpleName();


    public PullAndPushService() { super(TAG); }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
