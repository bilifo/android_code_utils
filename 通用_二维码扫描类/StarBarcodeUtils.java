package com.example.a1.zxingtest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.google.zxing.BarcodeFormat;
import com.kdp.starbarcode.core.BarCodeScanConfig;
import com.kdp.starbarcode.core.BarCodeType;
import com.kdp.starbarcode.inter.OnBarCodeScanResultListener;
import com.kdp.starbarcode.view.BarCodePreview;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * pjl++ 二维码识别,引用GitHub https://github.com/kangdongpu/StarBarcode
 * 使用:
 * 1.添加依赖 implementation 'com.kdp:starbarcode:1.0.2'
 * 2.初始化该类配置,可依次调用(也可不调用,使用默认) setRect(),setBarCodeScanConfig().再调用setBarCodePreview()方法将xml上写的<<com.kdp.starbarcode.view.BarCodePreview>控件传入
 * 3.调用onStart()和onStop()方法开始扫码和结束扫码
 *
 * eg:activity中使用: StarBarcodeUtils.newInstance().setRect(StarBarcodeUtils.getWindowRect(this)).showScanArea(barCodePreview,this,StarBarcodeUtils.getHalfWindowRect(this)).setBarCodePreview(barCodePreview).addCallResult(mcall);
 *      再StarBarcodeUtils.newInstance().onStart();和StarBarcodeUtils.newInstance().onStop();
 */
public class StarBarcodeUtils {
    private final String TAG = "StarBarcodeUtils";
    private static StarBarcodeUtils mStarBarcodeUtils = new StarBarcodeUtils();
    private Rect rect_default = new Rect(0, 0, 1000, 600);//左上xy和右下xy
    private Rect rect = null;//左上xy和右下xy
    private BarCodeScanConfig barCodeScanConfig;
    private BarCodeScanConfig.Builder builder;
    private BarCodePreview barCodePreview;//

    public static StarBarcodeUtils newInstance() {
        return mStarBarcodeUtils;
    }

    private StarBarcodeUtils() {
        mCallResults = null;
        mCallResults = new ArrayList<CallResult>();
        if (builder == null)
            builder = new BarCodeScanConfig.Builder();
    }

    /**
     * 设置扫描区域
     */
    public StarBarcodeUtils setRect(Rect mRect) {
        rect = mRect;
        return this;
    }

    /**
     * 设置摄像头扫码控件.
     * 注意setBarCodeScanConfig()和setRect()要在该方法前调用
     *
     * @param mBarCodePreview
     * @return
     */
    public StarBarcodeUtils setBarCodePreview(BarCodePreview mBarCodePreview) {
        Rect temp = null;
        if (rect != null) {
            temp = rect;
        } else {
            temp = rect_default;
        }

        barCodeScanConfig = builder
                .setROI(temp) //识别区域
                .setAutofocus(true) //自动对焦，默认为true
                .setDisableContinuous(false) //是否禁用连续对焦，必须在Autofocus为true的前提下，该参数才有效;默认为true
                .setBarCodeType(BarCodeType.ALL) //识别所有的条形码
//                    .setBarCodeType(BarCodeType.ONE_D_CODE) //仅识别所有的一维条形码
//                    .setBarCodeType(BarCodeType.TWO_D_CODE) //仅识别所有的二维条形码
//                    .setBarCodeType(BarCodeType.QR_CODE) //仅识别二维码，可提升识别速度
//                    .setBarCodeType(BarCodeType.CODE_128) //仅识别CODE 128码，可提升识别速度
//                    .setBarCodeType(BarCodeType.CUSTOME) //自定义条码类型，必须指定识别的条形码格式
                .setBarcodeFormats(EnumSet.of(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_128)) //自定义识别的条形码格式
//                    .setSupportAutoZoom(true) //当二维码图片较小时自动放大镜头(仅支持QR_CODE)
                .build();

        barCodePreview = mBarCodePreview;
        if (barCodePreview != null) {
            barCodePreview.setBarCodeScanConfig(barCodeScanConfig);
            barCodePreview.setOnBarCodeScanResultListener(mOnBarCodeScanResultListener);
        } else {
            Log.d(TAG, "init failure ,mBarCodePreview == null");
        }
        return this;
    }

    PopupWindow popWnd;
    ScanView scanView;
    View paremtView;
    Context parentContext;

    public StarBarcodeUtils showScanArea(View paremtView, Context parentContext, Rect mRect) {
        this.paremtView = paremtView;
        this.parentContext = parentContext;
        Rect temp = null;
        if (mRect != null) {
            temp = mRect;
        } else {
            temp = rect == null ? rect_default : rect;
        }
        scanView = new ScanView(parentContext);
        scanView.setBorder(new int[]{temp.left, temp.top, temp.right, temp.bottom});
        popWnd = new PopupWindow(parentContext);

        popWnd.setContentView(scanView);
        popWnd.setBackgroundDrawable(null);
        popWnd.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popWnd.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        return this;
    }

    /**
     * 监听扫码是否成功的监听
     */
    private OnBarCodeScanResultListener mOnBarCodeScanResultListener = new OnBarCodeScanResultListener() {

        @Override
        public void onSuccess(String result) {
            if (mCallResults != null && mCallResults.size() > 0) {
                for (CallResult mCallResult : mCallResults) {
                    mCallResult.onSuccess(result);
                }
            } else {
                Log.d(TAG, "mCallResults == null  &&  mCallResults.size()==0");
            }
        }

        @Override
        public void onFailure() {
            if (mCallResults != null && mCallResults.size() > 0) {
                for (CallResult mCallResult : mCallResults) {
                    mCallResult.onFailure();
                }
            } else {
                Log.d(TAG, "mCallResults == null  &&  mCallResults.size()==0");
            }
        }
    };

    /**
     * 关闭扫码,在setBarCodePreview() 方法后调用
     * 尽量在activity的生命周期onStop 调用
     */
    public void onStop() {
        if (barCodePreview != null) {
            barCodePreview.stopRecognize();
            barCodePreview.closeCamera();
            scanView.stopScan();
            popWnd.dismiss();
        }
    }

    /**
     * 打开扫码,在setBarCodePreview() 方法后调用
     * 尽量在activity的生命周期onStart 调用
     */
    public void onStart() {
        if (barCodePreview != null) {
            barCodePreview.openCamera();
            barCodePreview.startRecognize();
            handler.removeMessages(0);
            handler.sendEmptyMessageDelayed(0,500);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int value = msg.what;
            if (value == 0) {
                if(!scanView.isRunning()) {
                    popWnd.showAtLocation(paremtView, Gravity.CENTER, 0, 0);
                    //开启扫描动画
                    scanView.startScan();
                }
            }
        }
    };

    /**
     * 为了降低对OnBarCodeScanResultListener的耦合,使用新的功能接口进行屏蔽
     */
    interface CallResult {
        void onSuccess(String result);//识别成功

        void onFailure();//识别失败
    }

    /**
     * 多处注册,结果多处回调
     */
    private List<CallResult> mCallResults;

    /**
     * 添加回调,可在任意地点调用
     *
     * @param listener
     * @return
     */
    public StarBarcodeUtils addCallResult(CallResult listener) {
        if (mCallResults == null) {
            mCallResults = new ArrayList<CallResult>();
        }
        mCallResults.add(listener);
        return this;
    }

    //-----------不相关方法----------------

    /**
     * 返回全屏的宽高Rect
     */
    public static Rect getWindowRect(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        int left = 0;
        int top = 0;
        int right = screenWidth;
        int bottom = screenHeight;

        return new Rect(left, top, right, bottom);
    }

    /**
     * 返回半屏的宽高Rect
     */
    public static Rect getHalfWindowRect(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        int scanWidth = screenWidth / 6 * 4;
        int scanHeight = screenHeight / 3;
        int left = (screenWidth - scanWidth) / 2;
        int top = (screenHeight - scanHeight) / 2;
        int right = scanWidth + left;
        int bottom = scanHeight + top;

        return new Rect(left, top, right, bottom);
    }
}
