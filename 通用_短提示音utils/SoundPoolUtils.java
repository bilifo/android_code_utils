package com.dense.kuiniu.dense_frame.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.dense.kuiniu.dense_frame.MyApplication;
import com.dense.kuiniu.dense_frame.R;

public class SoundPoolUtils {
    /**
     * Created by lenovo on 2018/1/25.
     * 音乐池
     */
    // SoundPool对象
    public static SoundPool mSoundPlayer = new SoundPool(10,
            AudioManager.STREAM_SYSTEM, 5);
    public static SoundPoolUtils soundPlayUtils;

    private int soundID;
    // 上下文

    private SoundPoolUtils() {
        // 初始化声音
        /**
         * 参数1:加载音乐流第多少个 (只用了俩个音乐) 2:设置音乐的质量 音乐流 3:资源的质量  0
         */
        soundID = mSoundPlayer.load(MyApplication.getContext(), R.raw.aaa, 1);// 1
    }

    /**
     * 初始化
     * 初始化音乐池  播发喇叭的   BaseApplication初始化 方法里面用
     */
    public static SoundPoolUtils newInstance() {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPoolUtils();
        }
        return soundPlayUtils;
    }

    /**
     * 播放声音
     * 注意:play是在子线程中执行,无法在load后立即执行, 程序退出时，手动终止播放并释放资源是必要的。
     */
    public void play() {
        mSoundPlayer.play(soundID, 1, 1, 0, 0, 1);
    }

    public void stop() {
        mSoundPlayer.stop(soundID);
    }

    public void release(){
        mSoundPlayer.release();
    }

    //release()
    // pause()、resume()

}
