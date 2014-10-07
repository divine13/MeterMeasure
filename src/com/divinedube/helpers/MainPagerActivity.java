package com.divinedube.helpers;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.divinedube.metermeasure.FragmentList;
import com.divinedube.metermeasure.MeterReadingsDiffFragment;
import com.divinedube.metermeasure.R;

/**
 * Created by Divine Dube on 2014/09/24.
 */

public class MainPagerActivity extends FragmentActivity {

    private static final String TAG = MainPagerActivity.class.getSimpleName() ;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ViewPager mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        final FragmentManager fm = getSupportFragmentManager();

        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {

//            @Override
//            public Object instantiateItem(ViewGroup  container, int pos){
//                Log.d(TAG, "instantiate item in mainPager " + pos);
//                LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//
//                Fragment frag = null;
//                switch (pos){
//                    case 0:
//                       frag =  new FragmentList();
//                        break;
//                    case 1:
//                       frag = new MeterReadingsDiffFragment();
//                        break;
//                    default:
//                       return null;
//                }
//               return inflater.inflate(R.layout.activity_main, container);
////                container.addView(view ,0);
////                return view;
//            }

//            @Override
//            public Object instantiateItem(ViewGroup container, int position) {
//             //container.addView();
//            }

            @Override
            public Fragment getItem(int i) {
                return new FragmentList();
            }



            @Override
            public int getCount() {
                Log.d("MainPager", "two counts ");
                return 2;
            }

        });

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d(TAG, "the position is " + position);
                if (position == 0){
                     new FragmentList();
                    Log.d("MainPager", "Fragment list"); //todo this is not required because the this the default frag
                }else if (position == 1){
                     Fragment frag = new MeterReadingsDiffFragment();
                    Log.d("MainPager", "in this meter readings diff");
                }
            }
        });
    }
}
