package 通用_dialog模板;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import 通用_Log打印.MyLog;


/**
 * 一个带关闭按钮的通用动态dialogfragmnet模板.具体显示内容,依赖initIncludeView返回的view
 * (注意,生命周期是从show开始的.getdialog在除了生命周期以外的地方使用,可能会引起空指针问题.)
 * 使用方式:
 *      1/子类实现该类,实现各个方法
 *      2/子类的initIncludeView是返回要填充内容的View
 *      3/new 并show
 *
 * 生命周期:initIncludeView(在onCreateView中)--->initListener(在onViewCreated中)--->initData(在onResume中)
 *
 * @param <T> 数据的类型
 */
public abstract class BaseDialogFragment<T> extends DialogFragment implements MyLog.ThisClassCloseLog {
    boolean isExternalClick = false;//是否允许接收外部的点击而消失.依需要修改

    /**
     * 实现待添加的VIew
     */
    abstract View initIncludeView(Context mContext);

    abstract void initData(T data);

    abstract void initListener();

    private Context mContext;

    public Context getContext() {
        return mContext;
    }

    private ViewGroup rootview;
    private View includeview;
    private ImageView close;

    private RelativeLayout.LayoutParams rootviewparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private RelativeLayout.LayoutParams clickviewparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private RelativeLayout.LayoutParams includeviewparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    private T data;//数据
    /**
     * dialog的总体大小比例:0~1
     */
    double DIALOG_SIZE_SCALE = 1.0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(127, 127, 127, 127)));//背景透明
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE); //去除标题栏
//        getDialog().getWindow().setLayout(300,200); //宽高.实测无用,定制dialog的大小,还是依据includeview的大小来吧

//        getDialog().setCanceledOnTouchOutside(isExternalClick);//设置点击dialog外部是否取消弹框

        //点击返回键不消失，需要监听OnKeyListener:
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        //**注意,getdialog在除了生命周期以外的地方使用,可能会引起空指针问题.生命周期是从show开始的

        //通过DisplayMetrics获得当前屏幕大小,并对dialog显示大小做出限制
        Window window = this.getDialog().getWindow();
        DisplayMetrics dm2 = getResources().getDisplayMetrics();
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (dm2.heightPixels * DIALOG_SIZE_SCALE); // 改变的是dialog框在屏幕中的位置而不是大小
        p.width = (int) (dm2.widthPixels * DIALOG_SIZE_SCALE); // 宽度设置为屏幕的0.65
        window.setAttributes(p);

        //根布局
        rootview = new RelativeLayout(mContext);
        rootview.setLayoutParams(rootviewparams);

        //代添加布局.依赖ListViewItem

        includeview = initIncludeView(mContext);
        //当引入的布局不是相对布局,而是其他布局时,获得宽高
        if (includeview.getLayoutParams() != null) {
            ViewGroup.LayoutParams tempparams = includeview.getLayoutParams();
            includeviewparams.width = tempparams.width;
            includeviewparams.height = tempparams.height;
        }
        includeviewparams.addRule(RelativeLayout.CENTER_IN_PARENT);
        includeview.setLayoutParams(includeviewparams);

        includeview.setId(View.generateViewId());
        rootview.addView(includeview);
        //关闭按钮
        close = new ImageView(mContext);
        close.setBackgroundResource(android.R.drawable.btn_dialog);
        close.setId(View.generateViewId());
        clickviewparams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        clickviewparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        close.setLayoutParams(clickviewparams);
        rootview.addView(close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return rootview;
    }

    public BaseDialogFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        MyLog.showLog(getActivity(), "---" + "onAttach:start" + "---");
        mContext = activity.getBaseContext();
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MyLog.showLog(getActivity(), "---" + "onCreate:start" + "---");
        super.onCreate(savedInstanceState);
        //设置dialog全屏显示
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        MyLog.showLog(getActivity(), "---" + "onActivityCreated:start" + "---");
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListener();
    }

    @Override
    public void onStart() {
        MyLog.showLog(getActivity(), "---" + "onStart:start" + "---");
        super.onStart();
    }

    @Override
    public void onResume() {
        MyLog.showLog(getActivity(), "---" + "onResume:start" + "---");
        super.onResume();
        initData(data);
    }

    @Override
    public void onPause() {
        MyLog.showLog(getActivity(), "---" + "onPause:start" + "---");
        super.onPause();

    }

    @Override
    public void onStop() {
        MyLog.showLog(getActivity(), "---" + "onStop:start" + "---");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        MyLog.showLog(getActivity(), "---" + "onDestroyView:start" + "---");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        MyLog.showLog(getActivity(), "---" + "onDestroy:start" + "---");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        MyLog.showLog(getActivity(), "---" + "onDetach:start" + "---");
        super.onDetach();
    }

    public View getIncludeview() {
        return includeview;
    }

    public ImageView getCloseView() {
        return close;
    }

    /**
     * 给dialog设置数据
     *
     * @param data
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * 设置dialog大小系数
     * @param scale
     */
    public void setDialogSize(double scale) {
        if (scale > 0 && scale <= 1) {
            DIALOG_SIZE_SCALE = scale;
        }
    }
}

