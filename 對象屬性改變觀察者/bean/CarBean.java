package com.bdstar.mycar.bean;

public class CarBean {
    //暫定0:open 1:close
    public static final int OPEN=0;
    public static final int CLOSE=1;
/*後面優化代碼的時候,可以將靜態常量轉為枚舉類*/
//    enum OpenOrClose{
//        OPEN,CLOSE
//    }

    /**
     * 转向力度控制(多选其一)
     */
    int powerAdjust = -1;
    /**
     * 伴我回家时间设置(多选其一)
     */
    int lightsOutDelay = -1;
    /**
     * 氛围灯
     */
    int atmosphereLamp = -1;
    /**
     * 车辆偏离预警(多选其一)
     */
    int deviationWarn = -1;
    /**
     * 前方碰撞预警(多选其一)
     */
    int headCrashWarn = -1;
    /**
     * 后方盲区监测
     */
    int tailCrashWarn = -1;
    /**
     * 移动物体预警
     */
    int moveingWarn = -1;
    /**
     * 开门预警
     */
    int openDoorWarn = -1;
    /**
     * 主题联动
     */
    int themeLinkage = -1;
    /**
     * 车模颜色设置(多选其一)
     */
    int carModelColor = -1;

    public CarBean() {
    }

    public CarBean(int powerAdjust, int lightsOutDelay, int atmosphereLamp, int deviationWarn, int headCrashWarn, int tailCrashWarn, int moveingWarn, int openDoorWarn, int themeLinkage, int carModelColor) {
        this.powerAdjust = powerAdjust;
        this.lightsOutDelay = lightsOutDelay;
        this.atmosphereLamp = atmosphereLamp;
        this.deviationWarn = deviationWarn;
        this.headCrashWarn = headCrashWarn;
        this.tailCrashWarn = tailCrashWarn;
        this.moveingWarn = moveingWarn;
        this.openDoorWarn = openDoorWarn;
        this.themeLinkage = themeLinkage;
        this.carModelColor = carModelColor;
    }

    public int getPowerAdjust() {
        return powerAdjust;
    }

    public void setPowerAdjust(int powerAdjust) {
        this.powerAdjust = powerAdjust;
    }

    public int getLightsOutDelay() {
        return lightsOutDelay;
    }

    public void setLightsOutDelay(int lightsOutDelay) {
        this.lightsOutDelay = lightsOutDelay;
    }

    public int getAtmosphereLamp() {
        return atmosphereLamp;
    }

    public void setAtmosphereLamp(int atmosphereLamp) {
        this.atmosphereLamp = atmosphereLamp;
    }

    public int getDeviationWarn() {
        return deviationWarn;
    }

    public void setDeviationWarn(int deviationWarn) {
        this.deviationWarn = deviationWarn;
    }

    public int getHeadCrashWarn() {
        return headCrashWarn;
    }

    public void setHeadCrashWarn(int headCrashWarn) {
        this.headCrashWarn = headCrashWarn;
    }

    public int getTailCrashWarn() {
        return tailCrashWarn;
    }

    public void setTailCrashWarn(int tailCrashWarn) {
        this.tailCrashWarn = tailCrashWarn;
    }

    public int getMoveingWarn() {
        return moveingWarn;
    }

    public void setMoveingWarn(int moveingWarn) {
        this.moveingWarn = moveingWarn;
    }

    public int getOpenDoorWarn() {
        return openDoorWarn;
    }

    public void setOpenDoorWarn(int openDoorWarn) {
        this.openDoorWarn = openDoorWarn;
    }

    public int getThemeLinkage() {
        return themeLinkage;
    }

    public void setThemeLinkage(int themeLinkage) {
        this.themeLinkage = themeLinkage;
    }

    public int getCarModelColor() {
        return carModelColor;
    }

    public void setCarModelColor(int carModelColor) {
        this.carModelColor = carModelColor;
    }
}
