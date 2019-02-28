package com.bdstar.mycar.info;


public interface SettingObserable {

    public void register(SettingObserver observer);

    public void unregister(SettingObserver observer);


    //数据改变
    public void dispense(int eventCode, int eventValue);
}
