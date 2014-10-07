package com.divinedube.helpers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.divinedube.metermeasure.FragmentList;
import com.divinedube.metermeasure.MeterReadingsDiffFragment;
import com.divinedube.metermeasure.R;

import java.util.List;
import java.util.Vector;

/**
 * Created by Divine Dube on 2014/10/03.
 */
public class Main extends FragmentActivity {
    private  MyPagerAdapter myPagerAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        this.initialisePaging();


    }

    private void initialisePaging() {

        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, FragmentList.class.getName()));
        fragments.add(Fragment.instantiate(this, MeterReadingsDiffFragment.class.getName()));
        //will add the people frag some other time

        this.myPagerAdapter = new MyPagerAdapter(super.getSupportFragmentManager(), fragments);

        ViewPager pager = (ViewPager) findViewById(R.id.theViewPager);
        pager.setAdapter(this.myPagerAdapter);
    }
}