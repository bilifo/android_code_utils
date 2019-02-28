package 文件检索;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import 文件信息.FileInfo;
import 文件信息.FileInfoFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 这是针对findfileutil类进行的改进,把扫描数据放在一个list里的方式,改变成放在临时list中,handler传递后清空
 * 依赖FileInfo和FileInfoFactory类
 * 
 * 代完善:
 * 		扫描线程的暂停和取消,降低占用
 */

public class FindFileUtil2 {
	static final int WHAT_ALL_FILES = 1;
	static final int WHAT_IMAGE_FILES = 2;
	static final int WHAT_AUDIO_FILES = 3;
	static final int WHAT_VIDEO_FILES = 4;
	static final int WHAT_CACHE_FILES = 5;

	private FindFileUtil2() {
	
	}

	public static class FindFileUtilHolder {
		private static final FindFileUtil2 mFindFileUtil = new FindFileUtil2();
	}

	public static FindFileUtil2 getNewInstance() {
		return FindFileUtilHolder.mFindFileUtil;
	}

	// boolean isScan=false;
	/**
	 * 扫描状态: -1 无U盘,没有扫描 0 有U盘,没有扫描 1扫描中 2扫描完成 3扫描暂停
	 */
	int scanState = -1;


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
			scanState = 0;
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
	List<FileInfo> allFiles = new ArrayList<FileInfo>();// 所有文件
	List<FileInfo> cacheFiles = new ArrayList<FileInfo>();// 缓存文件集合,用来传递

	// List allDirectory=new ArrayList();
	// 扫描并筛选出对应文件
	private void scan(File path) {
		if (scanState == -1 || scanState == 0) {
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
			FileInfo info=new FileInfo(path);
			FileInfoFactory.newInstance(info);
			allFiles.add(info);
			cacheFiles.add(info);
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
			scanState = 2;
		}
	}

	/**
	 * 要求返回所有图片文件信息的扫描结果
	 * 
	 * @param handler
	 */
	Timer mtimer = new Timer();
	TimerTask mImageTimerTask;
	
	/**
	 * 定时发送缓冲数据
	 * @param handler
	 * @param dataLen
	 * @param delaytime
	 */
	public void getCacheScanResult(final Handler handler,long delaytime) {// 通过钩子handler把最新扫描的数据传回去让界面处理
		if (mImageTimerTask == null) {
			mImageTimerTask = new TimerTask() {
				@Override
				public void run() {
					Message message = handler.obtainMessage(WHAT_CACHE_FILES);
					List handlerList  = new ArrayList();
					
					if (cacheFiles.size() >0) {// 有更新数据						
						handlerList.addAll(cacheFiles);//添加到发送缓冲区
						cacheFiles.clear();//删去会被发送的数据						
						
						Bundle newbBundle = new Bundle();
						newbBundle.putSerializable(SCAN_RESULT, (Serializable) handlerList);
						message.setData(newbBundle);
						handler.sendMessage(message);
					} else if (cacheFiles.size()==0&&scanState!=2) {//暂时没有更新
						
					}

					if (scanState == 2) {// 扫描结束
						mImageTimerTask.cancel();
						mtimer.cancel();
					}
				}
			};
			mtimer.scheduleAtFixedRate(mImageTimerTask, 0, delaytime);//定时发送
		} else {

		}

	}

	/**
	 * 定量发送缓冲数据(报错java.util.ConcurrentModificationException)
	 * @param handler
	 * @param dataLen
	 * @param delaytime
	 */
	public void getCacheScanResult(final Handler handler, final int dataLen) {// 通过钩子handler把最新扫描的数据传回去让界面处理

					Message message = handler.obtainMessage(WHAT_CACHE_FILES);
					List handlerList  = new ArrayList(dataLen);
					
					if (cacheFiles.size() >=dataLen) {// 有更新数据
						List templist=cacheFiles.subList(0, dataLen-1); //取cache的区间
						handlerList.addAll(templist);//添加到发送缓冲区
						cacheFiles.removeAll(templist);//删去会被发送的数据					
						
						Bundle newbBundle = new Bundle();
						newbBundle.putSerializable(SCAN_RESULT, (Serializable) handlerList);
						message.setData(newbBundle);
						handler.sendMessage(message);
					}else if(cacheFiles.size()>0&&cacheFiles.size()<dataLen){
//						handler.removeMessages(WHAT_CACHE_FILES);
//						handler.sendMessageDelayed(handler.obtainMessage(WHAT_CACHE_FILES),1000);
						mImageTimerTask=new TimerTask() {							
							@Override
							public void run() {
								getCacheScanResult(handler, dataLen);
							}
						};
						mtimer.schedule(mImageTimerTask, 1000);//默认1秒
					} else if (cacheFiles.size()==0&&scanState!=2) {//暂时没有更新
						mImageTimerTask=new TimerTask() {							
							@Override
							public void run() {
								getCacheScanResult(handler, dataLen);
							}
						};
						mtimer.schedule(mImageTimerTask, 1000);//默认1秒
					}

					if (scanState == 2) {// 扫描结束
						mImageTimerTask.cancel();
						mtimer.cancel();
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

	public void reScanAgain() {
		if (getScanState() == -1 || getScanState() == 0) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String path = "/mnt/udisk";
					if (isNullFolder(path)) {
						throw new Error("文件夹不存在");
					} else {
						scan(path);
						scanState = 2;
					}
				}
			}).start();
		}
	}

}