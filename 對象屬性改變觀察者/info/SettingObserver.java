package com.bdstar.mycar.info;

public interface SettingObserver {
    //数据改变
    void dataChange(int eventCode, int eventValue);
    //设置唯一tag
    String getTag();
}
