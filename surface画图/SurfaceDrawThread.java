package surface画图;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceView;

/**
 * 给surfaceview画直线图
 * @author PanJunLong
 *
 */
public class SurfaceDrawThread  extends Thread{
	private int oldX = 0;// 上次绘制的X坐标
	private int oldY = 0;// 上次绘制的Y坐标
	private SurfaceView sfv;// 画板
	private int X_index = 0;// 当前画图所在屏幕X轴的坐标
	private Paint mPaint;// 画笔
	/**
	 * X轴缩小的比例
	 */
	public int rateX = 4;
	/**
	 * Y轴缩小的比例
	 */
	public int rateY = 4;
	//Y轴默认基线
	public int baseLine = 300;
	//停止标记
	private boolean isRecording=false;
	private ArrayList<short[]> inBuf = new ArrayList<short[]>();//要画的数据
	
	/**
	 * 
	 * @param sfv	surfaceView对象
	 * @param mPaint	Paint对象
	 * @param bufferList	要画的数据
	 */
	public SurfaceDrawThread(SurfaceView sfv,Paint mPaint,ArrayList<short[]> bufferList) {
		this.sfv = sfv;
		this.mPaint=mPaint;		
		this.inBuf=(ArrayList<short[]>) bufferList.clone();//必须要clone，直接this.inBuf=bufferList是没数据的
	}
	
	/**
	 * 当要画的数据出现变化时，传入更新的数据集合
	 * @param bufferList
	 */
	public void updateData(ArrayList<short[]> bufferList){
		this.inBuf=(ArrayList<short[]>) bufferList.clone();
	}

	public void run() {
		isRecording=true;
		while (isRecording) {
			ArrayList<short[]> buf = new ArrayList<short[]>();
			synchronized (inBuf) {
				if (inBuf.size() == 0)
					continue;
				buf = (ArrayList<short[]>) inBuf.clone();// 保存
				inBuf.clear();// 清除
			}// 压缩，相当于buf中每隔rateX位抽出来的数，组成一个显示的数组
			for (int i = 0,ii=0; i < (buf.size()/rateX); i++,ii=i*rateX) {
				short[] tmpBuf = buf.get(ii);
				SimpleDraw(X_index, tmpBuf, rateY, baseLine);// 把缓冲区数据画出来
				X_index = X_index + tmpBuf.length;
				if (X_index > sfv.getWidth()) {
					X_index = 0;
				}
			}
		}
	}
	
	/**
	 * 设置为false，即关闭线程
	 * @param flag
	 */
	public void setFlag(boolean flag){
		isRecording=false;
	}

	/**
	 * 绘制指定区域
	 * 
	 * @param start
	 *            X轴开始的位置(全屏)
	 * @param buffer
	 *            缓冲区
	 * @param rate
	 *            Y轴数据缩小的比例
	 * @param baseLine
	 *            Y轴基线
	 */
	void SimpleDraw(int start, short[] buffer, int rate, int baseLine) {
		if (start == 0)
			oldX = 0;
		Canvas canvas = sfv.getHolder().lockCanvas(new Rect(start, 0, start + buffer.length, sfv.getHeight()));// 关键:获取画布
		canvas.drawColor(Color.BLACK);// 清除背景
		int y;
		for (int i = 0; i < buffer.length; i++) {// 有多少画多少
			int x = i + start;
			y = buffer[i] / rate + baseLine;// 调节缩小比例，调节基准线
			canvas.drawLine(oldX, oldY, x, y, mPaint);
			oldX = x;
			oldY = y;
		}
		sfv.getHolder().unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
	}
}