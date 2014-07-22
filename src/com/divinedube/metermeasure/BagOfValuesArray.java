package com.divinedube.metermeasure;



import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Divine Dube on 2014/07/14.
 */

public class BagOfValuesArray {

//    ArrayList<Object> meter = new ArrayList<Object>();
//    private transient HashMultimap<String, Object> map  =  HashMultimap.create();
//
//
//    public HashMultimap putValuesPairs(String key, Object object) {
//        map.put(key, object);
//        return map;
//    }
//
//    public void addValuePairs(){
//       meter.add(map);
//    }

    private ArrayList<Object> meter = new ArrayList<Object>();
    private transient HashMap<String, Object> rt = new HashMap<String, Object>();

    public HashMap setMap(int id, String day, String time, double reading, String note, String createdAt){
        HashMap<String, Object> obj = new HashMap<String, Object>();
        obj.put("id", id);
        obj.put("day", day);
        obj.put("time",time);
        obj.put("reading", reading);
        obj.put("note", note);
        obj.put("made_at", createdAt);
        rt = obj;
        return rt;
    }

    public void putArr() {
        meter.add(rt);
    }
}




