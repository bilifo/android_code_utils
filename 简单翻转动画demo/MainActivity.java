package 简单翻转动画demo;

import com.example.PTutils.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {  
    private RelativeLayout mRelativeLayout;  
    private ImageView mImageView;  
    private Button mButton;  
    private int count=0;  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
        init();  
    }  
      
    private void init(){  
        mRelativeLayout=(RelativeLayout) findViewById(R.id.relativeLayout);  
        mImageView=(ImageView) findViewById(R.id.imageView);  
        mButton=(Button) findViewById(R.id.button);  
        mButton.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View view) {  
//                float centerX = mRelativeLayout.getWidth() / 2f;  
//                float centerY = mRelativeLayout.getHeight() / 2f;  
//                // 构建3D旋转动画对象,旋转角度为0到90度  
//                Rotate3dAnimation rotation = new Rotate3dAnimation(0, 90, centerX, centerY,310.0f, true);  
//                // 动画持续时间500毫秒  
//                rotation.setDuration(500);  
//                // 动画完成后保持完成状态  
//                rotation.setFillAfter(true);  
//                rotation.setInterpolator(new AccelerateInterpolator());  
            	Animation rotation=new Animation() {
				};
                // 设置动画的监听器  
                if (count%2==0) {  
                    rotation.setAnimationListener(new RotateToTheSecondImageViewAnimationListener());  
                }else{  
                    rotation.setAnimationListener(new RotateToTheFirstImageViewAnimationListener());  
                }  
                mRelativeLayout.startAnimation(rotation);  
                count++;  
            }  
        });  
    }  
      
      
    class RotateToTheFirstImageViewAnimationListener implements AnimationListener {  
        @Override  
        public void onAnimationStart(Animation animation) {  
        }  
          
        @Override  
        public void onAnimationEnd(Animation animation) {  
            mImageView.setImageResource(R.drawable.a11);  
              
            // 获取布局的中心点位置，作为旋转的中心点  
            float centerX = mRelativeLayout.getWidth();  
            float centerY =mRelativeLayout.getHeight()/2f;  
              
            // 构建3D旋转动画对象,旋转角度为90到0度  
            Rotate3dAnimation rotation = new Rotate3dAnimation(90,0, centerX, centerY,310.0f, false );  
            // 动画持续时间500毫秒  
            rotation.setDuration(500);  
            // 动画完成后保持完成状态  
            rotation.setFillAfter(true);  
            rotation.setInterpolator(new AccelerateInterpolator());  
            mRelativeLayout.startAnimation(rotation);  
        }  
  
        @Override  
        public void onAnimationRepeat(Animation animation) {  
        }  
  
    }  
      
      
    class RotateToTheSecondImageViewAnimationListener implements AnimationListener {  
  
        @Override  
        public void onAnimationStart(Animation animation) {  
        }  
  
          
        @Override  
        public void onAnimationEnd(Animation animation) {  
            //动画完成后展示另外的图片  
            mImageView.setImageResource(R.drawable.a22);  
              
            // 获取布局的中心点位置,作为旋转的中心点  
            float centerX = 0 ;  
            float centerY = mRelativeLayout.getHeight()/2 ;  
            // 构建3D旋转动画对象,旋转角度为270到360度  
            Rotate3dAnimation rotation = new Rotate3dAnimation(-90, 0, centerX, centerY,310.0f, false);  
            // 动画持续时间500毫秒  
            rotation.setDuration(500);  
            // 动画完成后保持完成状态  
            rotation.setFillAfter(true);  
            rotation.setInterpolator(new AccelerateInterpolator());  
            mRelativeLayout.startAnimation(rotation);  
        }  
  
        @Override  
        public void onAnimationRepeat(Animation animation) {  
        }  
  
    }  
  
}  