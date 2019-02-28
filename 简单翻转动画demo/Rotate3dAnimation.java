package 简单翻转动画demo;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class Rotate3dAnimation extends Animation {
	private final float mFromDegrees;
	private final float mToDegrees;
	private final float mCenterX;
	private final float mCenterY;
	private final float mDepthZ;
	private final boolean isReverse;
	private Camera mCamera;

	/**
	 * Creates a new 3D rotation on the Y axis. The rotation is defined by its
	 * start angle and its end angle. Both angles are in degrees. The rotation
	 * is performed around a center point on the 2D space, definied by a pair of
	 * X and Y coordinates, called centerX and centerY. When the animation
	 * starts, a translation on the Z axis (depth) is performed. The length of
	 * the translation can be specified, as well as whether the translation
	 * should be reversed in time. 在Y轴上创建新的3D旋转。 旋转由其起始角和终止角定义。 两个角度都是度数。
	 * 围绕2D空间上的中心点执行旋转，由被称为centerX和centerY的一对X和Y坐标定义。 当动画开始时，执行Z轴（深度）上的平移。
	 * 可以指定变换的长度，以及变换是否应该在时间上颠倒。
	 * 
	 * @param fromDegrees
	 *            the start angle of the 3D rotation
	 *            开始角度。）0度即面向我们的屏幕面，-90度逆时针，+90度顺时针。
	 * @param toDegrees
	 *            the end angle of the 3D rotation 结束角度
	 * @param centerX
	 *            the X center of the 3D rotation 旋转中点X
	 * @param centerY
	 *            the Y center of the 3D rotation 旋转中点Y。
	 *            这两个可能难以理解，其实想象个空间3维坐标系，把这个当做（x，y）当作坐标系原点。
	 *            当（x,y）=(0,0)时，是俯视图片旋转。当（x,y）=(图宽,图长)时，是俯视图片旋转。
	 * @param depthZ
	 *            深度，在Z轴上移动多少。直观上是原图缩放多少。为正是缩小，为负是放大。
	 * @param reverse
	 *            true if the translation should be reversed, false otherwise
	 *            true为翻转，false不翻转。实测对图像没影响，只有depthZ设置后，才会缩放图片。
	 *            它是影响正播放还是倒播放。
	 */
	public Rotate3dAnimation(float fromDegrees, float toDegrees, float centerX, float centerY, float depthZ,
			boolean reverse) {
		mFromDegrees = fromDegrees;
		mToDegrees = toDegrees;
		mCenterX = centerX;
		mCenterY = centerY;
		mDepthZ = depthZ;
		isReverse = reverse;
	}

	/**
	 * 这是一个回调函数告诉Animation目标View的大小参数，在这里可以初始化一些相关的参数，例如设置动画持续时间、设置Interpolator、
	 * 设置动画的参考点等
	 */
	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);

		/*
		 * Camera类，该类是一个空间变换工具，作用有点类似于Matrix,提供了如下常用的方法。
		 * 
		 * getMatrix(Matrix matrix) :将Camera所做的变换应用到指定的maxtrix上 rotateX(float
		 * deg):将目标组件沿X轴旋转 rotateY(float deg)、 rotateZ(float deg)
		 * translate(float x, float y, float z):把目标组件在三维空间类进行位移变换。
		 * applyToCanvas(Canvas canvas):把Camera所做的变换应用到Canvas上。
		 */
		mCamera = new Camera();
	}

	/**
	 * interpolatedTime:该参数代表了时间的进行程度（如：你设置的时间是1000ms,
	 * 那么interploatedTime就会从0开始一直到1,当该参数为1时表明动画结束.
	 * Transformation:代表补间动画在不同时刻对图形或组建的变形程度。该对象中封装了一个Matrix对象，
	 * 对它所包含的Matrix对象进行位移、倾斜、旋转等变换时
	 */
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final float fromDegrees = mFromDegrees;
		float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

		final float centerX = mCenterX;
		final float centerY = mCenterY;
		final Camera camera = mCamera;

		final Matrix matrix = t.getMatrix();

		camera.save();
		if (isReverse) {
			camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
		} else {
			camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
		}
		camera.rotateY(degrees);
		camera.getMatrix(matrix);
		camera.restore();

		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}
}
