package 文件信息;

import java.io.File;

import 文件信息.FileInfoFactory.IFileInfoUtil;

/**
 * 文件信息表
 * 		文件路径	
 * 		文件类型	//通过文件头判断
 * 		文件大小	//file.length() 单位字节
 * 		文件信息创建时间
 * 		文件最后修改时间	//file.lastModified() 
 * 使用方式:
 * 		1/声明bean变量FileInfo info=new FileInfo(file);
 * 		2/使用FileInfoFactory.newInstance(info);来给info各个属性赋值
 * 		2.1/由于FileInfoFactory使用了策略模式,可以在内部替换mIFileInfoUtil对象来改变获得各个属性的策略
 * 		3/使用FileInfo的各个get方法获得属性
 * @author PanJunLong
 *
 */
public class FileInfoFactory {
	private static IFileInfoUtil mIFileInfoUtil=new FileInfoUtlis(); 
	private static FileInfoFactory mFileInfo=new FileInfoFactory();
	private static File mfile;
	private static FileInfo info;
	interface IFileInfoUtil{
		public String getPath(File file);
		public String getType(File file);
		public String getSize(File file);
		public String getUpdateTime(File file);
	}
	
	private FileInfoFactory(){
		
	}
	
	public static FileInfoFactory newInstance(FileInfo file){
		info=file;
		mfile=info.getFile();
		info.setPath(mIFileInfoUtil.getPath(mfile));
		info.setSize(mIFileInfoUtil.getSize(mfile));
		info.setType(mIFileInfoUtil.getType(mfile));
		info.setUpdateDate(mIFileInfoUtil.getUpdateTime(mfile));
		return mFileInfo;
	}
}
