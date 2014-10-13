package com.divinedube.http;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Divine Dube on 2014/10/11.
 */
public class FeederClient extends IntentService {

    public FeederClient(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
