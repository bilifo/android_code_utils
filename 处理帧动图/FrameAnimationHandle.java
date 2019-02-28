package 处理帧动图;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;

public class FrameAnimationHandle {
	static Context context;
	Bitmap mbitmap;
	int horSize;
	int verSize;

	String path;
	/**
	 * 构造方法
	 * @param context 
	 * @param bitmap 位图对象
	 * @param horSize 横向分几个
	 * @param verSize 竖向分几个
	 */
	public FrameAnimationHandle(Context context, Bitmap bitmap, int horSize, int verSize) {
		this.context = context;
		this.mbitmap = bitmap;

	}


	public static Bitmap getRawBitmap(Context context, int Rid) {
		return BitmapFactory.decodeResource(context.getResources(), Rid);
	}

	public static Bitmap getAssetsBitmap(Context context, String name) {
		Bitmap bitmap = null;
		try {
			InputStream is = context.getAssets().open(name);
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public Bitmap getSDBitmap(Context context, String path) {
		return BitmapFactory.decodeFile(path);
	}

	public Bitmap getNetBitmap(Context context, InputStream is) {
		Bitmap bitmap = null;
		bitmap = BitmapFactory.decodeStream(is);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}


	public static Picture exe(Bitmap mbitmap, int horSize, int verSize) {
		// List list=new ArrayList<>();
		Picture mPicture = new Picture();
		int bitmapW = mbitmap.getWidth();
		int bitmapH = mbitmap.getHeight();

		Canvas mCanvas = mPicture.beginRecording(bitmapW, bitmapH);
		for (int i = 0; i < verSize ; i++) {
			for (int j = 0; j < horSize; j++) {
				mCanvas.drawBitmap(mbitmap, bitmapW / horSize * i, bitmapH / verSize * i, new Paint());
			}
		}
		mPicture.endRecording();
		return mPicture;

	}
}
