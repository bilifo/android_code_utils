package 文件信息;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import 文件信息.FileInfoFactory.IFileInfoUtil;

/**
 * 文件信息表工具 以读文件后缀的方式来判断类型
 * 
 * @author PanJunLong
 *
 */
public class FileInfoUtlis2 implements IFileInfoUtil {
	/**** 文件类型 *****/
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

	private String getFileType(File file) {
		// 判断单个文件是否是符合过滤规则的文件
		if (imageFilter.accept(file)) {
			return "image";
		} else if (audioFilter.accept(file)) {
			return "audio";
		} else if (videoFilter.accept(file)) {
			return "video";
		} else if (directoryFilter.accept(file)) {
			return "directory";
		} else if (fileFilter.accept(file)) {
			return "file";
		} else {
			return "null";
		}
	}

	@Override
	public String getPath(File file) {
		return file.getPath();
	}

	@Override
	public String getType(File file) {
		return getFileType(file);
	}

	@Override
	public String getSize(File file) {
		Long len = file.length();
		return len.toString();
	}

	@Override
	public String getUpdateTime(File file) {
		Long date = file.lastModified();
		return date.toString();
	}

	/***** 或得文件大小 ******/
	/***** 获得文件创建时间***BasicFileAttributeView在1.7以上才有 */

}
