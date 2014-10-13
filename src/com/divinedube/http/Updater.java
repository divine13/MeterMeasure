package com.divinedube.http;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Divine Dube on 2014/10/12.
 */
/*this class updates the readings and notifies the user how much electricity is left*/
public class Updater extends Service implements Runnable{

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void run() {

    }
}
