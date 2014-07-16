package com.divinedube.metermeasure;


import com.google.common.collect.HashMultimap;

import java.util.ArrayList;



/**
 * Created by Divine Dube on 2014/07/14.
 */

public class BagOfValuesArray {

    ArrayList<Object> meter = new ArrayList<Object>();
    private transient HashMultimap<String, Object> map  =  HashMultimap.create();


    public HashMultimap putValuesPairs(String key, Object object) {
        map.put(key, object);
        return map;
    }

    public void addValuePairs(){
       meter.add(map);
    }
}




