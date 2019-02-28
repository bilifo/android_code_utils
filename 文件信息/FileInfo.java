package 文件信息;

import java.io.File;

public class FileInfo {
	private String path;
	private String type;
	private String size;
	private String updateDate;
	private File mfile;
	
	public FileInfo(Object path){
		setFile(path);
//		path=mfile.getPath();
//		Long len = mfile.length();
//		size=len.toString();
//		Long date=mfile.lastModified();
//		updateDate=date.toString();
		
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public File getFile() {
		return mfile;
	}

	public void setFile(Object file) {
		if (file instanceof File) {
			mfile = (File) file;
		} else if (file instanceof String) {
			mfile = new File((String) file);
		} else {
			try {
				throw new Exception("参数不是File或String对象");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	

}
