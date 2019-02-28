package com.bdstar.mycar.info;


import com.bdstar.mycar.bean.CarBean;

/**
 * S401 一些全局设置的值
 */
public class GlobalSettingValue extends BaseObserable {
    public static final int CHANGE_EVENT_PowerAdjust = 1;
    public static final int CHANGE_EVENT_LightsOutDelay = 2;
    public static final int CHANGE_EVENT_AtmosphereLamp = 3;
    public static final int CHANGE_EVENT_DeviationWarn = 4;
    public static final int CHANGE_EVENT_HeadCrashWarn = 5;
    public static final int CHANGE_EVENT_TailCrashWarn = 6;
    public static final int CHANGE_EVENT_MoveingWarn = 7;
    public static final int CHANGE_EVENT_OpenDoorWarn = 8;
    public static final int CHANGE_EVENT_ThemeLinkage = 9;
    public static final int CHANGE_EVENT_CarModelColor = 10;

    private static GlobalSettingValue mGlobalSettingValue = new GlobalSettingValue();

    private GlobalSettingValue() {

    }

    public static GlobalSettingValue getInstance() {
        return mGlobalSettingValue;
    }

    //**********bean的set和get通道****************
    CarBean bean = new CarBean();

    public GlobalSettingValue setCarBean(CarBean bean) {
        this.bean = bean;
        return mGlobalSettingValue;
    }

    public CarBean getCarBean() {
        return this.bean;
    }

//    public int getPowerAdjust() {
//        return bean.getPowerAdjust();
//    }

    public GlobalSettingValue setPowerAdjust(int powerAdjust) {
        int i = bean.getPowerAdjust();
        if (i != powerAdjust) {
            bean.setPowerAdjust(powerAdjust);
            if (i != -1) {
                dispense(CHANGE_EVENT_PowerAdjust, powerAdjust);
            }
        }
        return mGlobalSettingValue;
    }

//    public int getLightsOutDelay() {
//        return bean.getLightsOutDelay();
//    }

    public GlobalSettingValue setLightsOutDelay(int lightsOutDelay) {
        int i = bean.getLightsOutDelay();
        if (i != lightsOutDelay) {
            bean.setLightsOutDelay(lightsOutDelay);
            if (i != -1) {
                dispense(CHANGE_EVENT_LightsOutDelay, lightsOutDelay);
            }
        }
        return mGlobalSettingValue;
    }

//    public int getAtmosphereLamp() {
//        return bean.getAtmosphereLamp();
//    }

    public GlobalSettingValue setAtmosphereLamp(int atmosphereLamp) {
        int i = bean.getAtmosphereLamp();
        if (i != atmosphereLamp) {
            bean.setAtmosphereLamp(atmosphereLamp);
            if (i != -1) {
                dispense(CHANGE_EVENT_AtmosphereLamp, atmosphereLamp);
            }
        }
        return mGlobalSettingValue;
    }

//    public int getDeviationWarn() {
//        return bean.getDeviationWarn();
//    }

    public GlobalSettingValue setDeviationWarn(int deviationWarn) {
        int i = bean.getDeviationWarn();
        if (i != deviationWarn) {
            bean.setDeviationWarn(deviationWarn);
            if (i != -1) {
                dispense(CHANGE_EVENT_DeviationWarn, deviationWarn);
            }
        }
        return mGlobalSettingValue;
    }

//    public int getHeadCrashWarn() {
//        return bean.getHeadCrashWarn();
//    }

    public GlobalSettingValue setHeadCrashWarn(int headCrashWarn) {
        int i = bean.getHeadCrashWarn();
        if (i != headCrashWarn) {
            bean.setHeadCrashWarn(headCrashWarn);
            if (i != -1) {
                dispense(CHANGE_EVENT_HeadCrashWarn, headCrashWarn);
            }
        }
        return mGlobalSettingValue;
    }

//    public int getTailCrashWarn() {
//        return bean.getTailCrashWarn();
//    }

    public GlobalSettingValue setTailCrashWarn(int tailCrashWarn) {
        int i = bean.getTailCrashWarn();
        if (i != tailCrashWarn) {
            bean.setTailCrashWarn(tailCrashWarn);
            if (i != -1) {
                dispense(CHANGE_EVENT_TailCrashWarn, tailCrashWarn);
            }
        }
        return mGlobalSettingValue;
    }

//    public int getMoveingWarn() {
//        return bean.getMoveingWarn();
//    }

    public GlobalSettingValue setMoveingWarn(int moveingWarn) {
        int i = bean.getMoveingWarn();
        if (i != moveingWarn) {
            bean.setMoveingWarn(moveingWarn);
            if (i != -1) {
                dispense(CHANGE_EVENT_MoveingWarn, moveingWarn);
            }
        }
        return mGlobalSettingValue;
    }

//    public int getOpenDoorWarn() {
//        return bean.getOpenDoorWarn();
//    }

    public GlobalSettingValue setOpenDoorWarn(int openDoorWarn) {
        int i = bean.getOpenDoorWarn();
        if (i != openDoorWarn) {
            bean.setOpenDoorWarn(openDoorWarn);
            if (i != -1) {
                dispense(CHANGE_EVENT_OpenDoorWarn, openDoorWarn);
            }
        }
        return mGlobalSettingValue;
    }

//    public int getThemeLinkage() {
//        return bean.getThemeLinkage();
//    }

    public GlobalSettingValue setThemeLinkage(int themeLinkage) {
        int i = bean.getThemeLinkage();
        if (i != themeLinkage) {
            bean.setThemeLinkage(themeLinkage);
            if (i != -1) {
                dispense(CHANGE_EVENT_ThemeLinkage, themeLinkage);
            }
        }
        return mGlobalSettingValue;
    }

//    public int getCarModelColor() {
//        return bean.getCarModelColor();
//    }

    public GlobalSettingValue setCarModelColor(int carModelColor) {
        int i = bean.getCarModelColor();
        if (i != carModelColor) {
            bean.setCarModelColor(carModelColor);
            if (i != -1) {
                dispense(CHANGE_EVENT_CarModelColor, carModelColor);
            }
        }
        return mGlobalSettingValue;
    }
}
