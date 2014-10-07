package com.divinedube.helpers;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.divinedube.metermeasure.R;

import java.util.List;

/**
 * Created by Divine Dube on 2014/10/03.
 */
public class MyPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = MyPagerAdapter.class.getSimpleName();
    private List<Fragment> fragments;

    public MyPagerAdapter(FragmentManager supportFragmentManager, List<Fragment> fragments) {
        super(supportFragmentManager);
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
       return this.fragments.size();
    }

    @Override
    public Fragment getItem(int i) {
        return this.fragments.get(i);
    }



    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }
}
