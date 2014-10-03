package com.divinedube.helpers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.divinedube.metermeasure.R;

/**
 * Created by Divine Dube on 2014/10/03.
 */
public class One extends Fragment {
    public static final String TAG = One.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View rootView =  inflater.inflate(R.layout.fagment_one, container, false);
        Bundle args = getArguments();
        ((TextView) rootView.findViewById(R.id.button)).setText(Integer.toString(args.getInt(TAG)));
        return rootView;
    }
}