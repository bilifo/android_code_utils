package 简单翻转动画demo;

import android.view.View;
import android.widget.ImageView;

public class ItemRotationUtil {
	//n型转换
	void Ntransformation(ImageView next, ImageView prev,ImageView currentimage)
	{
		//gallery中会加载屏幕可见的几张图，这个方法只是3个的变换，3个参数只需传入两个，另一个为null，因为在变换时，是当前图片和前/后一张之间的变换。可以试下5个的变换
		Rotate3dAnimation rotation_next = null, rotation_prev = null, rotation_current = null;
		 if (next != null)
		 {
		 next.setVisibility(View.GONE);
		 rotation_next = new Rotate3dAnimation(-90, 0, 0,next.getHeight() / 2, 0.0f, true);
		 // 动画持续时间500毫秒
		 rotation_next.setDuration(1000);
		 // 动画完成后保持完成状态
		 rotation_next.setFillAfter(false);
		 next.setVisibility(View.VISIBLE);
		 next.setAnimation(rotation_next);
		 }
		if (prev != null)
		{
			prev.setVisibility(View.GONE);
			rotation_prev = new Rotate3dAnimation(90, 0, prev.getWidth(), prev.getHeight() / 2, 0.0f, true);
			// 动画持续时间500毫秒
			rotation_prev.setDuration(1000);
			// 动画完成后保持完成状态
			rotation_prev.setFillAfter(false);
			prev.setVisibility(View.VISIBLE);
			prev.setAnimation(rotation_prev);

		}
		if (currentimage != null)
		{
			currentimage.setVisibility(View.GONE);
			if (prev==null&&next!=null)//右边图片进入
			{// view改currentItem
				rotation_current = new Rotate3dAnimation(0, 90, currentimage.getWidth(), currentimage.getHeight() / 2, 0.0f, true);
			} else if (next==null&&prev!=null)//左边图片进入
			{
				rotation_current = new Rotate3dAnimation(0, -90, 0, currentimage.getHeight() / 2, 0.0f, true);
			}
			// 动画持续时间500毫秒
			rotation_current.setDuration(1000);
			// 动画完成后保持完成状态
			rotation_current.setFillAfter(true);
//			currentimage.setVisibility(View.VISIBLE);
			currentimage.setAnimation(rotation_current);
		}

	}

}
