package 通用_文件扫描工具类;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * 
 * 使用方式
 * 1.使用 ScanUtils.newInstance().setSaveFileHandle(mSaveFileHandle).setRootPath("/mnt/udisk/8-15/");
 * 设置是否保存扫描结果的操作和必须的扫描起点
 * 2.使用 ScanUtils.newInstance().setScanMode(1).setFilter(mFileFilter)来设置扫描方式和过滤类型(非必要)
 * 3.使用 ScanUtils.newInstance().start()启动扫描
 * 4.使用 ScanUtils.newInstance().setScanState(2)来暂停扫描
 * 5.使用 ScanUtils.newInstance().setScanState(1)来恢复扫描
 * 
 * 改进: 
 * 1.添加更多扫描方式 (迭代,树型)
 * 2.这是一个比较标准的工具类,以后都参照这样做
 * 
 * 
 * @author jlpan
 * 
 */
public class ScanUtils extends Thread {
	private static class ScanUtilsHolder {
		private static final ScanUtils mScanUtils = new ScanUtils();
	}

	public static ScanUtils newInstance() {
		return ScanUtilsHolder.mScanUtils;
	}

	private int scanState = 0;// 0:未扫描 1:扫描中 2:暂停 3:中断结束 4:扫描完成

	public int getScanState() {
		return scanState;
	}

	public void setScanState(int scanState) {
		this.scanState = scanState;
	}

	private ScanUtils() {
		if (folderList == null)
			folderList = new ArrayList<String>();
	}

	String rootPath = "/mnt/udisk";

	public ScanUtils setRootPath(String rootPath) {
		if (scanState == 0)// 防止在运行时改变扫描方式
			this.rootPath = rootPath;
		return ScanUtilsHolder.mScanUtils;
	}

	// ****将保存文件操作进行分离,默认是无保存操作
	private SaveFileHandle mSaveFileHandle = new SaveFileHandle() {

		@Override
		public void writeSaveFile(String text) {

		}

		@Override
		public void removeSaveFile() {

		}
	};

	public interface SaveFileHandle {
		void removeSaveFile();

		void writeSaveFile(String text);
	}

	public ScanUtils setSaveFileHandle(SaveFileHandle mSaveFileHandle) {
		if (scanState == 0)// 防止在运行时改变扫描方式
			this.mSaveFileHandle = mSaveFileHandle;
		return ScanUtilsHolder.mScanUtils;
	}

	// ****将过滤对象FilenameFilter分离,向外提供,实现不同过滤方式,默认是过滤文件夹
	private FileFilter mfilter = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	};

	public ScanUtils setFilter(FileFilter mfilter) {
		if (scanState == 0)// 防止在运行时改变扫描方式
			this.mfilter = mfilter;
		return ScanUtilsHolder.mScanUtils;
	}

	// ****选择扫描方式 0:广度队列扫描 1:深度栈扫描 2:迭代扫描
	int scanMode = 0;// 默认扫描方式

	public ScanUtils setScanMode(int sele) {
		if (scanState == 0)// 防止在运行时改变扫描方式
			scanMode = sele;
		return ScanUtilsHolder.mScanUtils;
	}

	private List<String> folderList;
	// ****深度遍历,用栈
	private Stack mStack = null;
	private int scanStack(int scanState) throws Exception {

		if (mStack == null || mStack.isEmpty()) {
			// 栈为空,要么是开始,要么是结束
			if (scanState == 0) {// 开始
				mStack=new Stack();
				File rootfile = new File(rootPath);
				folderList.add(rootPath);
				mSaveFileHandle.writeSaveFile(rootfile.getPath());
				File[] childs = rootfile.listFiles(mfilter);
				for (File element : childs) {
					mStack.add(element);
					// folderList.add(element.getPath());
				}
				scanState = 1;
			} else if (scanState == 1) {// 结束
				scanState = 4;
			}
		} else {
			if (scanState == 1) {// 继续扫描
				File father = (File) mStack.pop();
				folderList.add(father.getPath());
				mSaveFileHandle.writeSaveFile(father.getPath());
				if (father == null) {
				}
				File[] childs = father.listFiles(mfilter);
				for (File element : childs) {
					mStack.add(element);
				}
			} else if (scanState == 2) {// 暂停扫描
				scanState = 2;
			} else if (scanState == 3) {// 中断扫描
				mStack = null;
				folderList = null;
				scanState = 3;
			} else if (scanState == 4) {// 扫描完成
				scanState = 4;
				mStack = null;
			}
		}
		return scanState;
	}

	// ****广度遍历,用队列
	private Queue mQueue = null;
	private int scanQueue(int scanState) throws Exception {
		if (mQueue == null || mQueue.isEmpty()) {
			// 队列为空,要么是开始,要么是结束
			if (scanState == 0) {// 开始
				mQueue=new LinkedList();
				File rootfile = new File(rootPath);
				folderList.add(rootPath);
				mSaveFileHandle.writeSaveFile(rootfile.getPath());
				File[] childs = rootfile.listFiles(mfilter);
				for (File element : childs) {
					mQueue.add(element);
					folderList.add(element.getPath());
					mSaveFileHandle.writeSaveFile(element.getPath());
				}
				scanState = 1;
			} else if (scanState == 1) {// 结束
				scanState = 4;
			}
		} else {
			if (scanState == 1) {// 继续扫描
				File father = (File) mQueue.poll();
				if (father == null) {
					scanState = 4;
				}
				File[] childs = father.listFiles(mfilter);
				for (File element : childs) {
					mQueue.add(element);
					folderList.add(element.getPath());
					mSaveFileHandle.writeSaveFile(element.getPath());
				}
			} else if (scanState == 3) {// 中断扫描
				mQueue = null;
				folderList = null;
				scanState = 3;
			} else if (scanState == 4) {// 扫描完成
				scanState = 4;
				mQueue = null;
			}
		}
		return scanState;
	}

	@Override
	public void run() {
//		super.run();
		mSaveFileHandle.removeSaveFile();
		while (scanState != 3 && scanState != 4) {
			try {
				if (scanMode == 0) {
					scanState = scanQueue(scanState);
				} else if (scanMode == 1) {
					scanState = scanStack(scanState);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

