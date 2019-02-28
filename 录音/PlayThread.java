package 录音;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

/**
 * 播放录音线程
 * @author PanJunLong
 *
 */
public class PlayThread extends AsyncTask<Void, Integer, Void> {
	boolean isPlaying = false;
	private AudioTrack mAudioTrack;
	private String fileName;
	private TextView tv;
	private File file;

	/**
	 * 
	 * @param mAudioTrack
	 * @param file	录音的原始文件.pcm
	 * @param tv 传入textview对象，用它来显示当前播放进度
	 */
	public PlayThread(AudioTrack mAudioTrack, File file, TextView tv) {
		this.mAudioTrack = mAudioTrack;
		this.tv = tv;
		this.file = file;
		this.fileName = file.getPath();
		Log.d("PlayThread", ""+file.getPath());
	}

	/**
	 * 设置为false，即关闭当前线程
	 * @param isPlaying
	 */
	public void setFlag(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		isPlaying=true;
		int bufferSize = AudioTrack.getMinBufferSize(mAudioTrack.getSampleRate(), mAudioTrack.getChannelConfiguration(),
				mAudioTrack.getAudioFormat());
		// 定义缓冲
		short[] buffer = new short[bufferSize];
		// 定义输入流，将音频写入到AudioTrack类中，实现播放
		DataInputStream dis;
		try {
			Log.d("PlayThread", ""+file.getPath());
			dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

			// 实例AudioTrack
			AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, mAudioTrack.getSampleRate(),
					mAudioTrack.getChannelConfiguration(), mAudioTrack.getAudioFormat(), bufferSize,
					AudioTrack.MODE_STREAM);
			// 开始播放
			track.play();
			// 由于AudioTrack播放的是流，所以，我们需要一边播放一边读取
			while (isPlaying && dis.available() > 0) {
				int i = 0;
				while (dis.available() > 0 && i < buffer.length) {
					buffer[i] = dis.readShort();
					i++;
				}
				// 然后将数据写入到AudioTrack中
				track.write(buffer, 0, buffer.length);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		tv.setText("当前进度" + values[0]);
	}

	@Override
	protected void onPostExecute(Void result) {
		isPlaying = false;
		// 播放结束
		mAudioTrack.stop();
	}

}
