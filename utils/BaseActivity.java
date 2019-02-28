package utils;

import android.app.Activity;
import android.view.View;

public class BaseActivity extends Activity {
	public <E extends View> E $(int resId) {
		return  (E) this.findViewById(resId);
	}
}
