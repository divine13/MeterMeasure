package com.divinedube.helpers;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.divinedube.metermeasure.FragmentList;
import com.divinedube.metermeasure.R;

/**
 * Created by Divine Dube on 2014/09/24.
 */
public class MainPagerActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewPager mViewPager = new ViewPager(this);
       mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        FragmentManager fm = getSupportFragmentManager();



        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int i) {
                Fragment fragment = new FragmentList();
                Bundle args = new Bundle();
                args.putInt(One.TAG, i + 1);
                fragment.setArguments(args);
                return fragment;
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return ("OBJECT " + (position + 1));
            }
        });
    }
}
