package com.bdstar.mycar.info;


import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseObserable implements SettingObserable {
    Map<String, SettingObserver> mmap;

    @Override
    public void register(SettingObserver observer) {
        if(mmap==null){
            mmap=new HashMap<String, SettingObserver>();
        }
        if (!mmap.containsKey(observer.getTag())) {
            mmap.put(observer.getTag(), observer);
        }
    }

    @Override
    public void unregister(SettingObserver observer) {
        if(mmap==null||mmap.size()==0){
            Log.d("BaseObserable","Observer map is null");
            return;
        }
        if (mmap.containsKey(observer.getTag())) {
            mmap.remove(observer);
        }
    }

    @Override
    public void dispense(int eventCode, int eventValue) {
        if(mmap!=null&&mmap.size()!=0) {
            for (SettingObserver temp : mmap.values()) {
                temp.dataChange(eventCode, eventValue);
            }
        }
    }
}
