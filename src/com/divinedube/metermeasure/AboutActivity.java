package com.divinedube.metermeasure;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Divine Dube on 2014/07/02.
 */
public class AboutActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
 public void showCoolToast(View view){
     Toast.makeText(this,"Wasup!.dude REMEMBER to read the meter at the same time every single day, okay?",Toast.LENGTH_LONG).show();
 }
}