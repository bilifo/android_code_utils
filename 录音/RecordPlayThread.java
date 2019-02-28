package 录音;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioRecord;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;

/**
 * 录制声音线程
 * @author PanJunLong
 *
 */
public class RecordPlayThread extends AsyncTask<Void, Integer, File> {
	boolean isRecording = false;
	private AudioRecord mAudioRecord;
	private String fileName;
	private TextView tv;

	/**
	 * 
	 * @param mAudioRecord AudioRecord对象
	 * @param tv 传入textview对象，用它来显示当前录制进度
	 */
	public RecordPlayThread(AudioRecord mAudioRecord, TextView tv) {
		this.mAudioRecord = mAudioRecord;
		this.tv = tv;
	}

	public void setFlag(boolean isRecording) {
		this.isRecording = isRecording;
	}

	@Override
	protected File doInBackground(Void... arg0) {
		isRecording = true;
		File file = creatFile(getNewTime());
		// 开通输出流到指定的文件
		try {
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			// 根据定义好的几个配置，来获取合适的缓冲大小
			int bufferSize = AudioRecord.getMinBufferSize(mAudioRecord.getSampleRate(),
					mAudioRecord.getChannelConfiguration(), mAudioRecord.getAudioFormat());
			// 定义缓冲
			short[] buffer = new short[bufferSize];
			// 开始录制
			mAudioRecord.startRecording();
			int r = 0; // 存储录制进度
			// 定义循环，根据isRecording的值来判断是否继续录制
			while (isRecording) {
				// 从bufferSize中读取字节，返回读取的short个数
				// 这里老是出现buffer overflow，不知道是什么原因，试了好几个值，都没用，TODO：待解决
				int bufferReadResult = mAudioRecord.read(buffer, 0, buffer.length);
				// 循环将buffer中的音频数据写入到OutputStream中
				for (int i = 0; i < bufferReadResult; i++) {
					dos.writeShort(buffer[i]);
				}
				publishProgress(new Integer(r)); // 向UI线程报告当前进度
				r++; // 自增进度值
			}
			// 录制结束
			mAudioRecord.stop();
			Log.v("The DOS available:", "::" + file.length());
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		tv.setText("当前进度" + values[0]);

	}

	@Override
	protected void onPostExecute(File result) {
		isRecording = false;
		mAudioRecord.stop();
		tv.setText(result.getPath());
	}

	//以当前时间为文件名
	private String getNewTime() {
		Time time = new Time();
		time.setToNow();
		return time.format("%Y%m%d%H%M%S");
	}

	//在内置sd卡下建立一个名叫“testdate”的文件夹放录音文件
	private File creatFile(String fileName) {
		File filedir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
				+ "testdate" + File.separator);
		filedir.mkdirs();//
		Log.d("RecordPlayThread", "文件夹地址：" + filedir.getPath());

		try {
			File file = File.createTempFile(fileName, ".pcm", filedir);
			Log.d("RecordPlayThread", "文件地址：" + file.getPath());
			return file;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
}
