package 文件检索;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by PanJunLong on 2017/10/13. ***工具类最好都用单例模式***
 * 这个类还需要改进,在文件过多时,4个集合变量极易溢出 使用方式: 1/对应修改FindFileUtil()中需要扫描的路径scan("路径")
 * 2/其他类如何获取数据: 1/获得FindFileUtil工具对象.FindFileUtil.getNewInstance()
 * 2/使用findFileUtil.getImageScanResult(mHandler)发起获取最新数据的请求
 * 3/上一步操作中作为参数传入的handler钩子对象,在它的handleMessage方法里获取数据. Message.what==1,表示全部文件
 * Message.what==2,表示全部图片文件 Message.what==3,表示全部音频文件 Message.what==4,表示全部视频文件
 * 4/handle中获得数据,通过message.getData().getSerializable(FindFileUtil.SCAN_RESULT)
 * 获得返回的List
 * 
 * 待改进: 1/目前是单次调用getScanResult()等一次,发出一个handle信息,刷新一次数据信息.未来改进成可循环发送
 * 2/4个getScanResult()方法,无法同时使用.
 */

public class FindFileUtil {
	static final int WHAT_ALL_FILES = 1;
	static final int WHAT_IMAGE_FILES = 2;
	static final int WHAT_AUDIO_FILES = 3;
	static final int WHAT_VIDEO_FILES = 4;

	private FindFileUtil() {
//		if (getScanState() == 0||getScanState() == -1) {
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					String path = "/storage/emulated/0/";
////					String path = "/mnt/udisk";
//					if (isNullFolder(path)) {
//						throw new Error("文件夹不存在");
//					}else{
//						scan(path);
//						scanState=2;
//					}
//				}
//			}).start();
//		}
		reScanAgain();
	}

	public static class FindFileUtilHolder {
		private static final FindFileUtil mFindFileUtil = new FindFileUtil();
	}

	public static FindFileUtil getNewInstance() {
		return FindFileUtilHolder.mFindFileUtil;
	}

	// boolean isScan=false;
	/**
	 * 扫描状态: -1 无U盘,没有扫描 0 有U盘,没有扫描 1扫描中 2扫描完成 3扫描暂停
	 */
	int scanState = -1;
	// 图片后缀
	public static final String[] IMAGE_EXTENSION = { ".png", ".jpg", ".bmp", ".jpeg" };
	// 音乐后缀
	public static final String[] AUDIO_EXTENSION = { ".mp3" };
	// 视频后缀
	public static final String[] VIDEO_EXTENSION = { ".mp4" };

	// 图片过滤
	static FileFilter imageFilter = new FileFilter() {

		@Override
		public boolean accept(File file) {
			if (file.isFile()) {
				for (int i = 0; i < IMAGE_EXTENSION.length; i++) {
					if (file.getAbsolutePath().endsWith(IMAGE_EXTENSION[i].toLowerCase())) {
						return true;
					}
				}
			}
			return false;
		}
	};

	// 声音过滤
	static FileFilter audioFilter = new FileFilter() {
		@Override
		public boolean accept(File file) {
			if (file.isFile()) {
				for (int i = 0; i < AUDIO_EXTENSION.length; i++) {
					if (file.getAbsolutePath().endsWith(AUDIO_EXTENSION[i].toLowerCase())) {
						return true;
					}
				}
			}
			return false;
		}
	};

	// 视频过滤
	static FileFilter videoFilter = new FileFilter() {
		@Override
		public boolean accept(File file) {
			if (file.isFile()) {
				for (int i = 0; i < VIDEO_EXTENSION.length; i++) {
					if (file.getAbsolutePath().endsWith(VIDEO_EXTENSION[i].toLowerCase())) {
						return true;
					}
				}
			}
			return false;
		}
	};
	// 文件夹过滤
	static FileFilter directoryFilter = new FileFilter() {

		@Override
		public boolean accept(File file) {
			if (file.isDirectory()) {
				return true;
			}
			return false;
		}

	};
	// 文件过滤
	static FileFilter fileFilter = new FileFilter() {

		@Override
		public boolean accept(File file) {
			if (file.isFile()) {
				return true;
			}
			return false;
		}

	};

	/**
	 * 用来检测初始的扫描路径下是否有文件
	 * 
	 * @param path
	 * @return
	 */
	private boolean isNullFolder(String path) {
		File temp = new File(path);
		return this.isNullFolder(temp);
	}

	private boolean isNullFolder(File path) {
		File[] childfiles2 = path.listFiles();
		if (childfiles2 == null || childfiles2.length < 1) {
			return true;
		} else {
			scanState=0;
			return false;
		}
	}

	/**
	 * 扫描某个指定路径下的文件
	 *
	 * @param path
	 */
	private void scan(String path) {
		File temp = new File(path);
		this.scan(temp);
	}

	// 这4个集合变量,在文件过多时,极易溢出
	List<File> allFiles = new ArrayList<File>();// 所有文件
	List<File> allImages = new ArrayList<File>();// 所有图片
	List<File> allAudios = new ArrayList<File>();// 所有音乐
	List<File> allVideos = new ArrayList<File>();// 所有视频

	// List allDirectory=new ArrayList();
	// 扫描并筛选出对应文件
	private void scan(File path) {
		if (scanState == -1||scanState == 0) {
			scanState = 1;
		}
		if (path.isDirectory()) {
			File[] childfiles2 = path.listFiles();
			if (childfiles2 != null && childfiles2.length > 0) {
				for (File childFolder : childfiles2) {
					scan(childFolder);
				}
			} else {
				// if(scanState==-1){//没插U盘
				//
				// }
			}
			childfiles2 = null;
		} else {
			// 判断单个文件是否是符合过滤规则的文件
			if (imageFilter.accept(path)) {
				allImages.add(path);
			} else if (audioFilter.accept(path)) {
				allAudios.add(path);
			} else if (videoFilter.accept(path)) {
				allVideos.add(path);
			}
			allFiles.add(path);
		}
	}

	public List getScanResult() {
		return allFiles;
	}

	public static final String SCAN_RESULT = "scan_result";

	/**
	 * 要求返回所有文件信息的扫描结果
	 * 
	 * @param handler
	 */
	public void getScanResult(Handler handler, long delaytime) {// 通过钩子handler把最新扫描的数据传回去让界面处理
		Message message = handler.obtainMessage(WHAT_ALL_FILES);
		List<File> handlerList = (List) message.getData().getSerializable(SCAN_RESULT);
		if (handlerList == null) {
			handlerList = new ArrayList<File>();
		}
		if (handlerList.size() < allFiles.size()) {// allFiles有更新
			Bundle newbBundle = new Bundle();
			newbBundle.putSerializable(SCAN_RESULT, (Serializable) allFiles);
			message.setData(newbBundle);

			handler.sendMessageDelayed(message, delaytime);
		} else if (handlerList.size() == allFiles.size()) {// 没有更新
			scanState=2;
		}
	}

	/**
	 * 要求返回所有图片文件信息的扫描结果
	 * 
	 * @param handler
	 */
	Timer mtimer = new Timer();
	TimerTask mImageTimerTask;

	public void getImageScanResult(final Handler handler, long delaytime) {// 通过钩子handler把最新扫描的数据传回去让界面处理
		if (mImageTimerTask == null) {
			mImageTimerTask = new TimerTask() {
				@Override
				public void run() {
					Message message = handler.obtainMessage(WHAT_IMAGE_FILES);
					List handlerList = (List) message.getData()
							.getSerializable(SCAN_RESULT);
					if (handlerList == null) {
						handlerList = new ArrayList();
					}
					if (handlerList.size() < allImages.size()) {// allFiles有更新
						Bundle newbBundle = new Bundle();
						newbBundle.putSerializable(SCAN_RESULT,
								(Serializable) allImages);
						message.setData(newbBundle);

						// handler.sendMessageDelayed(message, delaytime);
						handler.sendMessage(message);
					} else if (handlerList.size() == allImages.size()) {// 没有更新

					}

					if (scanState != 2) {//扫描结束

					}
				}
			};
			mtimer.scheduleAtFixedRate(mImageTimerTask,0, delaytime);
		} else {

		}

	}

	/**
	 * 要求返回所有声音文件信息的扫描结果
	 * 
	 * @param handler
	 */
	public void getAudioScanResult(Handler handler, long delaytime) {// 通过钩子handler把最新扫描的数据传回去让界面处理
		Message message = handler.obtainMessage(WHAT_AUDIO_FILES);
		List<File> handlerList = (List) message.getData().getSerializable(SCAN_RESULT);
		if (handlerList == null) {
			handlerList = new ArrayList<File>();
		}
		if (handlerList.size() < allAudios.size()) {// allFiles有更新
			Bundle newbBundle = new Bundle();
			newbBundle.putSerializable(SCAN_RESULT, (Serializable) allAudios);
			message.setData(newbBundle);

			handler.sendMessage(message);

		} else if (handlerList.size() == allAudios.size()) {// 没有更新

		}
	}

	/**
	 * 要求返回所有视频文件信息的扫描结果
	 * 
	 * @param handler
	 */
	public void getVideoScanResult(Handler handler, long delaytime) {// 通过钩子handler把最新扫描的数据传回去让界面处理
		Message message = handler.obtainMessage(WHAT_VIDEO_FILES);
		List<File> handlerList = (List) message.getData().getSerializable(SCAN_RESULT);
		if (handlerList == null) {
			handlerList = new ArrayList<File>();
		}
		if (handlerList.size() < allVideos.size()) {// allFiles有更新
			Bundle newbBundle = new Bundle();
			newbBundle.putSerializable(SCAN_RESULT, (Serializable) allVideos);
			message.setData(newbBundle);

			handler.sendMessageDelayed(message, delaytime);

		} else if (handlerList.size() == allVideos.size()) {// 没有更新

		}
	}

	/**
	 * 返回当前扫描状态 -1 无U盘,没有扫描 0 有U盘,没有扫描 1扫描中 2扫描完成 3扫描暂停
	 * 
	 * @return
	 */
	public int getScanState() {
		return scanState;
	}
	
	public void reScanAgain(){
		if (getScanState() == -1||getScanState() == 0) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String path = "/mnt/udisk";
					if (isNullFolder(path)) {
						throw new Error("文件夹不存在");
					}else{
						scan(path);
						scanState=2;
					}
				}
			}).start();
		}
	}

}