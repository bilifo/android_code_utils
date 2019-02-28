package 文件缓存;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类是ACache的工厂封装，即应对不同需求进行的定制的ACache
 * 使用方式：
 * 清空緩存---ACacheFactory.newInstance().clearCache();
 * 緩存圖片列表----ACacheFactory.newInstance().setCacheImages(temp);
 * @author jlpan
 *
 */

public class ACacheFactory {
	private ACache aCache;
	final String CACHE_ALL_IMAGES_TAG = "images_lists";//
	final String CACHE_ALL_FOLDERS_TAG = "folders_lists";
	final String CACHE_CURRENT_IMAGE_NUM_TAG = "current_image_num";

	// final String CACHE_ALL_FOLDERS_TAG="folders_lists";

	private ACacheFactory() {
		aCache = ACache.get(BaseApplication.context);
	}

	private static class ACacheFactoryHolder {
		private final static ACacheFactory aCacheFactory = new ACacheFactory();
	}

	public static ACacheFactory newInstance() {
		return ACacheFactoryHolder.aCacheFactory;
	}

	public List<String> getCacheImages() {
		return (ArrayList<String>) aCache.getAsObject(CACHE_ALL_IMAGES_TAG);
	}

	public List<String> getCacheFolders() {
		return (ArrayList<String>) aCache.getAsObject(CACHE_ALL_FOLDERS_TAG);
	}

	public void setCacheImages(ArrayList<String> images) {
		aCache.put(CACHE_ALL_IMAGES_TAG, images);
	}

	public void setCacheFolders(ArrayList<String> folders) {
		aCache.put(CACHE_ALL_FOLDERS_TAG, folders);
	}

	public void setCurrentImageNUM(int position) {
		aCache.put(CACHE_CURRENT_IMAGE_NUM_TAG, position);
	}
	public int getCurrentImageNUM() {
		int temp=-1;
		temp=(Integer) aCache.getAsObject(CACHE_CURRENT_IMAGE_NUM_TAG);
		return temp;
	}

	public void clearCache() {
		aCache.clear();
	}
}
