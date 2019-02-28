package 自定义View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;

/**
 * 用于画折线的控件，可画多条折线，能不断显示。
 * 待改进：数据传入需分离，添加数据显示
 * @author PanJunLong
 *
 */
public class PolylineTrendView extends View {
	public PolylineTrendView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}
	public PolylineTrendView(Context context) {
		super(context);

	}
	Paint paint;
	int W, H;
	Float[] ydata;
//	ArrayList<Float[]> dataList=new ArrayList<Float[]>();
	ArrayList<Float> xPoint;
	ArrayList<Paint> paintList = new ArrayList<Paint>();
	ArrayList<ArrayList<Float>> yPointList = new ArrayList<ArrayList<Float>>();
	
	int INTERVAL = 10;
	int UPDATE = 100;
	boolean isRun = false;


	/**
	 * 设置是否开始运行折线图绘画，注意调用前，要先调用setTitle确定要画多少条线
	 * 
	 * @param isRun
	 * @return
	 */
	public String setRunFlag(boolean isRun) {
		this.isRun = isRun;
		if (isRun == false) {
			return "stop run";
		} else {
			initData();
			return "runing";
		}
	}

	public void setTitle(String title, int color) {
		paint = new Paint();
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
		paintList.add(paint);
		ArrayList<Float> yPoint = new ArrayList<Float>();
		yPointList.add(yPoint);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		W = w;
		H = h;
		xPoint = new ArrayList<Float>(W / INTERVAL);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isRun) {
			jisuan();
			for (int i = 1; i < xPoint.size(); i++) {
				for (int j = 0; j < yPointList.size(); j++) {
					canvas.drawLine(xPoint.get(i - 1), yPointList.get(j).get(i - 1), xPoint.get(i),
							yPointList.get(j).get(i), paintList.get(j));
					System.out.println("x[" + i + "]:" + xPoint.get(i) + "  " + "y[" + j + "][" + i + "]:"
							+ yPointList.get(j).get(i));
				}
			}
			try {
				Thread.sleep(UPDATE);// 刷新时间，想办法提成输入参数
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		invalidate();
	}

	private void jisuan() {
		xPoint.remove(0);
		for (int i = 0; i < yPointList.size(); i++) {
			yPointList.get(i).remove(0);
		}
		for (int i = 0; i < xPoint.size(); i++) {
			xPoint.set(i, xPoint.get(i) - (W / INTERVAL));
		}
		xPoint.add(getXMax());
		//这里是需要修改的，最好是数据输入和使用应该分开，输入是个线程，把结果保存到list中，使用list中的数据。
		upData(upData(),upData(),upData(),upData());
//		for (int i = 0; i < yPointList.size(); i++) {
//			yPointList.get(i).add(getData());
//		}
	}

	private void initData() {
		for (int i = 0; i < (W / INTERVAL); i++) {
			xPoint.add((float) (i * INTERVAL));
			for (int j = 0; j < yPointList.size(); j++) {
				yPointList.get(j).add(0.0f);
			}
		}
		ydata=new Float[yPointList.size()];
	}

	private float getXMax() {
		return W;
	}

	public void upData(float... fs){
		int num=0;
		ArrayList<Float> yPoint = new ArrayList<Float>();
		for(float i:fs){
			num=0;
			yPoint.add(i);
			num++;
		}		
		for (int i = 0; i < yPointList.size(); i++) {
			yPointList.get(i).add(yPoint.get(i));
		}
	}
	// 测试用
	public float upData() {
		float y = (float) (Math.random() * H);
		return y;
	}

}


