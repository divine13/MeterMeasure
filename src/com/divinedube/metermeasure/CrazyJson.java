package com.divinedube.metermeasure;

import android.util.ArrayMap;

import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Divine Dube on 2014/07/16.
 */
public class CrazyJson {
    public static void main(String[] args){
        Some cj = new Some();

        cj.setMap(1,45.8);
        cj.putArr();
        cj.setMap(1,67.9);
        cj.putArr();

        Gson gson = new Gson();
        System.out.print( gson.toJson(cj));
    }
}
class Some{
   private ArrayList<Object> arr = new ArrayList<Object>();
    private transient HashMap<String, Object> rt = new HashMap<String, Object>();

    public HashMap setMap(int s, double s2){
        HashMap<String, Object> obj = new HashMap<String, Object>();
        obj.put("id", s);
        obj.put("reading", s2);
        rt = obj;
        return rt;
    }

    public void putArr() {
        arr.add(rt);
    }
}
