package 通用_listview的adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 废弃
 * 使用方式：
 * public View getView(int position, View convertView, ViewGroup parent) {
		View view2=null;
		ItemView newItem;
		Integer[] ints = new Integer[] { R.drawable.ic_launcher };
		
		if(convertView==null){
			newItem=new ItemView(mContext, parent);
			view2=newItem.getRootView();
			view2.setTag(newItem);			
		}else{
			newItem=(ItemView) convertView.getTag();
			view2=newItem.getRootView();
		}	
		
		if(convertView==null){
			newItem.setViewData(R.id.fragment_list_item_imageview,"setBackgroundResource", ints);//setViewData应用的是反射，效率很成问题。不变的控件内容，放到convertView==null的判断中
		}else{
			
		}
			newItem.setViewData(R.id.fragment_list_item_text, "setText", ""+mList.get(position));
		Log.d(BaseApplication.TAG+"->"+BaseApplication.getCurrentClassName()+"->"+BaseApplication.getCurrentMethodName(),""+ mList.get(position));
		return view2;
	}
 * @author jlpan
 *
 */

public class ItemView {

	public ItemView(Context context, ViewGroup root) {
		view = LayoutInflater.from(context).inflate(
				R.layout.fragment_list_item, root, false);
	}

	TextView tv1, tv2;
	ImageView image;
	View view;
	int viewCount = -1;

	List<MethodTag> MethodTagArray = new ArrayList<MethodTag>();//

	public int getRLayoutID() {
		return R.layout.fragment_list_item;
	}

	public int getItemChildViewCount() {
		viewCount = ((ViewGroup) view).getChildCount();
		return viewCount;
	}

	public View getRootView() {
		return view;
	}

	interface MyViewSetContent {

	};

	/**
	 * 给控件设置数据。
	 * 
	 * @param id
	 *            控件的id
	 * @param methodname
	 *            该控件拥有的方法名字符串
	 * @param data
	 *            该方法需要的参数
	 * @return
	 */
	public <T extends View> ItemView setViewData(int id, String methodname,
			Object... data) {
		T itemview = (T) view.findViewById(id);// id对应的控件
		Class<T> temp = (Class<T>) itemview.getClass();// 控件的类型
		Method selectMethod = null;
		int typeNum = data.length;

		Class itemClass[] = new Class[typeNum];//
		for (int i = 0; i < typeNum; i++) {
			itemClass[i] = data[i].getClass();
		}

		if (MethodTagArray != null && MethodTagArray.size() > 0) {
			for (int i = 0; i < MethodTagArray.size(); i++) {
				if (MethodTagArray.get(i).getTagClass().equals(temp)
						&& MethodTagArray.get(i).methodName.equals(methodname)
						&& MethodTagArray.get(i).getParameNum() == typeNum
						&& Arrays.equals(itemClass, MethodTagArray.get(i)
								.getParameTypes())) {
					selectMethod = MethodTagArray.get(i).getMethod();
					break;
				} else if (i == (MethodTagArray.size() - 1)) {
					selectMethod = addTag(methodname, temp, typeNum, itemClass);
					break;
				}
			}
		} else {

			selectMethod = addTag(methodname, temp, typeNum, itemClass);
		}

		if (selectMethod != null) {
			try {
				selectMethod.invoke(itemview, data);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this;
	}

	/**
	 * 
	 * （如果发现查找过的类的方法，没有被添加到缓存数组。通过该方法添加。）
	 * @param methodname 方法名字符串
	 * @param temp 拥有该方法的类
	 * @param typeNum 参数个数
	 * @param itemClass 各个参数的类型组成的数组
	 * @return
	 */
	private <T extends View> Method addTag(String methodname, Class<T> temp,
			int typeNum, Class[] itemClass) {
		Method selectMethod;
		Method[] mMethod2 = getDeclaredMethods(temp, methodname);// 所有符合方法名的方法
		// 筛选出参数个数相等的方法
		List<Method> countEqualMethods = new ArrayList<Method>();
		for (int i = 0; i < mMethod2.length; i++) {
			Class[] types = mMethod2[i].getParameterTypes();// 所有符合方法名的方法的参数
			if (types.length == typeNum) {
				countEqualMethods.add(mMethod2[i]);
			} else {

			}
		}

		// 找到对应方法
		selectMethod = findMethod(
				countEqualMethods.toArray(new Method[countEqualMethods.size()]),
				itemClass);
		MethodTagArray.add(new MethodTag(temp, methodname, typeNum, itemClass,
				selectMethod));
		return selectMethod;
	}

//	// 给控件设置数据。传入id和控件设置数据的方法名.（通过getdeclaredMethod直接找到方法）
//	public <T extends View> ItemView setViewData2(int id, String methodname,
//			Object... data) {
//		T itemview = (T) view.findViewById(id);// id对应的控件
//		Class<T> temp = (Class<T>) itemview.getClass();// 控件的类型
//		Method selectMethod = getDeclaredMethod(temp, methodname, data);// 所有符合方法名的方法
//
//		if (selectMethod != null) {
//			try {
//				selectMethod.invoke(itemview, data);
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (InvocationTargetException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return this;
//	}

	/**
	 * 找到符合参数data类型的方法（通过传入参数和反射的方法参数一一匹配判断）
	 * 
	 * @param data
	 *            参数数据，不定长度
	 * @param mMethod2
	 *            一堆经过getDeclaredMethods()方法名匹配的方法数组，但可能参数个数不同或参数类型不符
	 * @return 一个符合参数个数和类型匹配的方法
	 */
	private Method findMethod(Method[] mMethod2, Class[] data) {
		Method selectMethod = null;

		for (int i = 0; i < mMethod2.length; i++) {
			Class[] types = mMethod2[i].getParameterTypes();
			for (int j = 0; j < types.length; j++) {
				if (data[j].isArray()) {// 传入的参数是数组类型
					Class arrayTypeClass = data[j].getComponentType();// 数组类型
					if (arrayTypeClass.isPrimitive())
						data[j] = objectClassTobaseType(arrayTypeClass);
				}
				if (types[j].isPrimitive()) {
					Class baseTypeToObject = baseTypeToObjectClass(types[j]);
					if (data[j].isAssignableFrom(baseTypeToObject)) {
						// 相同的类型
						selectMethod = mMethod2[i];
					}
				} else if (types[j].isArray()) {

				} else {
					boolean isInstance = types[j].isAssignableFrom(data[j]);
					if (isInstance) {
						// 相同的类型
						selectMethod = mMethod2[i];
					}
				}
			}
		}
		return selectMethod;
	}

	// 基础数据类型转复合数据类
	private Class<? extends Object> baseTypeToObjectClass(Class mclass) {
		if (mclass.isAssignableFrom(int.class)) {
			return Integer.class;
		} else if (mclass.isAssignableFrom(long.class)) {
			return Long.class;
		} else if (mclass.isAssignableFrom(float.class)) {
			return Float.class;
		} else if (mclass.isAssignableFrom(boolean.class)) {
			return Boolean.class;
		} else if (mclass.isAssignableFrom(char.class)) {
			return Character.class;
		} else if (mclass.isAssignableFrom(byte.class)) {
			return Byte.class;
		} else if (mclass.isAssignableFrom(short.class)) {
			return Short.class;
		}
		return null;
	}

	// 复合数据类转基础数据类型
	private Class objectClassTobaseType(Class<? extends Object> mclass) {
		if (mclass.isAssignableFrom(Integer.class)) {
			return int.class;
		} else if (mclass.isAssignableFrom(Long.class)) {
			return long.class;
		} else if (mclass.isAssignableFrom(Float.class)) {
			return float.class;
		} else if (mclass.isAssignableFrom(Boolean.class)) {
			return boolean.class;
		} else if (mclass.isAssignableFrom(Character.class)) {
			return char.class;
		} else if (mclass.isAssignableFrom(Byte.class)) {
			return byte.class;
		} else if (mclass.isAssignableFrom(Short.class)) {
			return short.class;
		}
		return null;
	}

	// 是不是基础类型的类。如Integer、Long、Short等
	private boolean isBaseTypeObject(Class mclass) {
		if (mclass == Integer.class || mclass == Long.class
				|| mclass == Float.class || mclass == Boolean.class
				|| mclass == Character.class || mclass == Byte.class
				|| mclass == Short.class) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 传入class和方法名的字符串，输出包含该字符串的所有方法
	 * 
	 * @param mclass
	 *            包含方法名的类
	 * @param methodname
	 *            方法名字符串
	 * @return
	 */
	private Method[] getDeclaredMethods(Class mclass, String methodname) {
		// getDeclaredMethods包括所有的公有私有方法和接口方法，不包括继承方法。getMethods返回所有public的方法包括父类和接口

		Method[] mMethod = mclass.getMethods();

		List<Method> list = new ArrayList<Method>();
		for (int i = 0; i < mMethod.length; i++) {
			if (mMethod[i].getName().equals(methodname)) {
				list.add(mMethod[i]);
			}
		}
		Method[] mMethod2 = list.toArray(new Method[list.size()]);
		return mMethod2;
	}

//	/**
//	 * 传入class和方法名的字符串，输出包含该字符串的方法，并且类型相同(待完善)
//	 * 
//	 * @param mclass
//	 * @param methodname
//	 * @param data
//	 * @return
//	 */
//	private Method getDeclaredMethod(Class mclass, String methodname,
//			Object[] data) {
//
//		// 把參數的類型放到list中
//		Method mMethod = null;
//		Class[] dataClass = new Class[data.length];
//		// boolean[] isBaseType = new boolean[data.length];// duiying
//		int ifBaseType = 0;// 是否含有基本类型,
//		for (int i = 0; i < data.length; i++) {// 一一拿到参数类型
//			Class itemClass = data[i].getClass();
//			dataClass[i] = itemClass;
//			if (isBaseTypeObject(itemClass)) {
//				// isBaseType[i] = true;
//				if (ifBaseType == -1) {
//					ifBaseType = 0;
//				}
//				ifBaseType += Math.pow(2, i);
//			} else {
//				// isBaseType[i] = false;
//			}
//		}
//
//		finding: while (ifBaseType >= 0) {// goto判断二进制数ifBaseType的哪些位可能是基础类型
//			try {
//				mMethod = mclass.getDeclaredMethod(methodname, dataClass);
//			} catch (NoSuchMethodException e) {
//				if (ifBaseType > 0) {// ifBaseType要大于0
//					int i = 0;
//					if (ifBaseType % 2 == 1) {// 当ifBaseType对2取余为1时，表示该位是1，即是可能的基础类型
//						if (ifBaseType > 1)// 当ifBaseType大于1，才用sqrt来求当前是二进制的第几位
//							i = (int) Math.sqrt(ifBaseType);
//						else
//							i = 0;
//					} else {
//
//					}
//
//					dataClass[i] = objectClassTobaseType(dataClass[i]);
//
//					ifBaseType = ifBaseType >> 1;
//					continue finding;
//				} else {
//					e.printStackTrace();
//					break;
//				}
//			}
//		}
//
//		return mMethod;
//	}

	/**
	 * 传入object对象，获得该对象的父类和所有实现接口
	 * 
	 * @param obj
	 * @return
	 */
	private Class[] getInterfaceAndSuperClass(Object obj) {
		Class[] temp3;
		List<Class> list = new ArrayList<Class>();
		list.addAll(getInterfaceAndSuperClass(obj.getClass()));
		temp3 = list.toArray(new Class[list.size()]);
		return temp3;
	}

	/**
	 * 传入object对象，迭代获得该对象的所有父类和所有实现接口
	 * 
	 * @param obj
	 * @return
	 */
	private Class[] getAllInterfaceAndSuperClass(Object obj) {
		Class[] temp3;
		List<Class> list = new ArrayList<Class>();
		list.addAll(getAllInterfaceAndSuperClass(obj.getClass()));
		temp3 = list.toArray(new Class[list.size()]);
		return temp3;
	}

	/**
	 * 传入Class对象，获得该对象的父类和当前类所有实现接口
	 * 
	 * @param obj
	 * @return
	 */
	private Collection<Class> getInterfaceAndSuperClass(Class mclass) {
		Set<Class> list = new HashSet<Class>();
		Class thisClass = mclass;
		Class superClass = mclass.getSuperclass();
		Class[] thisInterface = mclass.getInterfaces();
		list.add(thisClass);
		// 父类和接口
		if (superClass != null) {
			list.add(superClass);
		}
		if (thisInterface != null && thisInterface.length > 0) {
			list.addAll(Arrays.asList(thisInterface));
		}
		return list;
	}

	/**
	 * 传入Class对象，迭代获得该对象的所有父类和所有实现接口
	 * 
	 * @param obj
	 * @return
	 */
	private Collection<Class> getAllInterfaceAndSuperClass(Class mclass) {
		Set<Class> list = new HashSet<Class>();
		Class thisClass = mclass.getDeclaringClass();
		Class superClass = mclass.getSuperclass();
		Class[] thisInterface = mclass.getInterfaces();
		list.add(thisClass);
		// 迭代父类的父类和接口
		if (superClass != null) {
			list.add(superClass);
			list.addAll(getAllInterfaceAndSuperClass(superClass));
		}
		// 迭代接口的父类和接口(接口没有implements,只有extends)
		if (thisInterface != null && thisInterface.length > 0) {
			list.addAll(Arrays.asList(thisInterface));
			for (int i = 0; i < thisInterface.length; i++) {
				list.addAll(getAllInterfaceAndSuperClass(thisInterface[i]));
			}
		}

		return list;
	}

	// private Class isT

	/**
	 * 单个object转class
	 * 
	 * @param obj
	 * @return
	 */
	private Class ObjectToClass(Object obj) {
		return obj.getClass();
	}

	/**
	 * 多个object转class
	 * 
	 * @param objs
	 * @return
	 */
	private Class[] ObjectToClassS(Object[] objs) {
		Class[] temp = new Class[objs.length];
		for (int i = 0; i < objs.length; i++) {
			temp[i] = objs.getClass();
		}
		return temp;
	}

	/**
	 * 作用：保存已经查找过的类的方法，已避免相同方法每次都反射，影响效率
	 * 
	 * @author jlpan
	 * 
	 */
	private class MethodTag {
		private Class tagClass = null;
		private String methodName = null;
		private int parameNum = -1;
		private Class[] parameTypes;// 保存的是传入参数们的类型，并非方法method的参数类型。即传入的integer型虽然可能在后面会对应int型的方法，但这里为了识别，还是保留传入参数们的类型
		private Method method = null;

		public MethodTag(Class tagClass, String methodName, int parameNum,
				Class[] parameTypes, Method method) {
			super();
			this.tagClass = tagClass;
			this.methodName = methodName;
			this.parameNum = parameNum;
			this.parameTypes = parameTypes;
			this.method = method;
		}

		public Class getTagClass() {
			return tagClass;
		}

		public void setTagClass(Class tagClass) {
			this.tagClass = tagClass;
		}

		public String getMethodName() {
			return methodName;
		}

		public void setMethodName(String methodName) {
			this.methodName = methodName;
		}

		public int getParameNum() {
			return parameNum;
		}

		public void setParameNum(int parameNum) {
			this.parameNum = parameNum;
		}

		public Class[] getParameTypes() {
			return parameTypes;
		}

		public void setParameTypes(Class[] parameTypes) {
			this.parameTypes = parameTypes;
		}

		public Method getMethod() {
			return method;
		}

		public void setMethod(Method method) {
			this.method = method;
		}

		@Override
		public boolean equals(Object o) {
			MethodTag mMethodTag = (MethodTag) o;
			if (mMethodTag.getTagClass().equals(tagClass)
					&& mMethodTag.getMethodName().equals(methodName)
					// &&mMethodTag.getMethod().equals(method)
					&& Arrays.equals(mMethodTag.getParameTypes(), parameTypes)) {
				return true;
			} else {
				return false;
			}
		}

	}

}
