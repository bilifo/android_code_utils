package 反射;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 用于adapter中holder映射的子itemlayout。继承这个类，通过反射，实现整体控制item布局中的各个控件
 * @author PanJunLong
 *
 */
public class BaseAdapterItemView {
	TextView tv1, tv2;
	ImageView image;
	View view;
	int viewCount = -1;

	public int getRLayoutID() {
		return R.layout.fragment_list_item;
	}

	public BaseAdapterItemView(Context context, ViewGroup root) {
		view = LayoutInflater.from(context).inflate(
				R.layout.fragment_list_item, root, false);
		String[] str = new String[] { "sssss" };
//		String[] str = new String[] { "sssss","ddddddd" };//是错误的
		Integer[] ints = new Integer[] { R.drawable.ic_launcher };

		setViewData(R.id.fragment_list_item_imageview,
				"setBackgroundResource",ints);
		setViewData(R.id.fragment_list_item_text, "setText", str);

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
	 * ���ؼ��������ݡ�
	 * @param id �ؼ���id
	 * @param methodname �ÿؼ�ӵ�еķ������ַ���
	 * @param data �÷�����Ҫ�Ĳ���
	 * @return
	 */
	public <T extends View> BaseAdapterItemView setViewData(int id, String methodname,
			Object... data) {
		T itemview = (T) view.findViewById(id);// id��Ӧ�Ŀؼ�
		Class<T> temp = (Class<T>) itemview.getClass();// �ؼ�������
		Method[] mMethod2 = getDeclaredMethods(temp, methodname);// ���з��Ϸ������ķ���
		// �ҵ���Ӧ����
		Method selectMethod = findMethod(mMethod2, data);

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

	// ���ؼ��������ݡ�����id�Ϳؼ��������ݵķ�����.��ͨ��getdeclaredMethodֱ���ҵ�������
	public <T extends View> BaseAdapterItemView setViewData2(int id, String methodname,
			Object... data) {
		T itemview = (T) view.findViewById(id);// id��Ӧ�Ŀؼ�
		Class<T> temp = (Class<T>) itemview.getClass();// �ؼ�������
		Method selectMethod = getDeclaredMethod(temp, methodname, data);// ���з��Ϸ������ķ���

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
	 * �ҵ����ϲ���data���͵ķ�����ͨ����������ͷ���ķ�������һһƥ���жϣ�
	 * @param data �������ݣ���������
	 * @param mMethod2 һ�Ѿ���getDeclaredMethods()������ƥ��ķ������飬�����ܲ���������ͬ��������Ͳ���
	 * @return һ�����ϲ�������������ƥ��ķ���
	 */
	private Method findMethod(Method[] mMethod2, Object... data) {
		Method selectMethod = null;
		// �х�������ͷŵ�list��
		List<Class> dataClass = new ArrayList<Class>();

		for (int i = 0; i < data.length; i++) {
			Class itemClass = data[i].getClass();
			dataClass.add(itemClass);
		}

		// ɸѡ������������ȵķ���
		List<Method> countEqualMethods = new ArrayList<Method>();
		for (int i = 0; i < mMethod2.length; i++) {
			Class[] types = mMethod2[i].getParameterTypes();// ���з��Ϸ������ķ����Ĳ���
			if (types.length == data.length) {
				countEqualMethods.add(mMethod2[i]);
			} else {

			}
		}

		for (int i = 0; i < countEqualMethods.size(); i++) {
			Class[] types = countEqualMethods.get(i).getParameterTypes();
			for (int j = 0; j < types.length; j++) {
				if (dataClass.get(j).isArray()) {// ����Ĳ�������������
					Class arrayTypeClass = dataClass.get(j).getComponentType();// ��������
					if (arrayTypeClass.isPrimitive())
						dataClass.set(j, objectClassTobaseType(arrayTypeClass));
				}
				if (types[j].isPrimitive()) {
					Class baseTypeToObject = baseTypeToObjectClass(types[j]);
					if (dataClass.get(j).isAssignableFrom(baseTypeToObject)) {
						// ��ͬ������
						selectMethod = countEqualMethods.get(i);
					}
				} else if (types[j].isArray()) {

				} else {
					boolean isInstance = types[j].isAssignableFrom(dataClass
							.get(j));
					if (isInstance) {
						// ��ͬ������
						selectMethod = countEqualMethods.get(i);
					}
				}
			}
		}
		return selectMethod;
	}

	// ������������ת����������
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

	// ����������ת������������
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

	// �ǲ��ǻ������͵��ࡣ��Integer��Long��Short��
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

	// // ���ؼ��������ݡ�����id�Ϳؼ��������ݵķ�����.������ԭ���ڷ��䷽��ʱ��������������Ͳ���ȷ�����п����ǽӿ�.����Ľ�������Ǵ���
	// public <T extends View> ItemView setViewData(int id, String methodname,
	// Object[] data) {
	// T itemview = (T) view.findViewById(id);// id��Ӧ�Ŀؼ�
	// Class<T> temp = (Class<T>) itemview.getClass();// �ؼ�������
	// Class[][] mClass = new Class[data.length][];//
	// һά��Ӧ�û�����ĸ�����������ά��Ӧ�����������еļ̳к�ʵ�ֵ���
	// for (int i = 0; i < data.length; i++) {// һһȷ���û�����Ĳ�������
	// Class[] tempclass = getAllInterfaceAndSuperClass(data[i]);
	// mClass[i] = tempclass;
	// }
	// // Log.d(BaseApplication.TAG, "ItemView���"+"setViewData��"+);
	// Method[] mMethod2 = getDeclaredMethods(temp, methodname);// ���Ϸ������ķ���
	//
	// Class[][] parameters = new Class[mMethod2.length][];//
	// һά��Ӧÿ��������ͬ�ķ�������ά��Ӧ�÷����Ĳ���
	// List<Class[]> parameterAllow = new ArrayList<Class[]>();// ����������ͬ�ķ���
	// for (int i = 0; i < mMethod2.length; i++) {
	// parameters[i] = mMethod2[i].getParameterTypes();
	// // �޳�������������ȵ���
	// if (parameters[i].length == data.length) {
	// parameterAllow.add(parameters[i]);
	// }
	// }
	// // ƥ��������������ͣ�ȷ������
	// int selectMethod = 0;
	// int selectMethodFlag=0;//�����������ƥ�䣬+1.���������ڲ�������ʱ����ʾ������������ƥ�䡣
	// for (; selectMethod < parameterAllow.size(); selectMethod++) {
	// Class[] temp3 = parameterAllow.get(selectMethod);//
	//
	// int j = 0;//�����������
	// int z = 0;//����������ܵ����͸���
	// for (; j < temp3.length;)
	// {//��������ƥ�䷽����ÿ�����������ÿ���������ͺ��û���������ݵ�ÿ��������ͬ�����������Ҫ����ķ�����
	// if (temp3[j].equals(mClass[j][z])) {
	// selectMethodFlag++;
	// j++;
	// z=0;
	// }else{
	// if(z>=mClass[j].length-1){
	// break;
	// }
	// z++;
	// continue;
	// }
	// }
	// if(selectMethodFlag==temp3.length){
	// break;
	// }else{
	// continue;
	// }
	// }
	// Method mMethod;
	// // ȷ���˷���������ִ��
	// try {
	// mMethod =
	// temp.getDeclaredMethod(methodname,parameterAllow.get(selectMethod));
	// mMethod.setAccessible(true);
	// mMethod.invoke(itemview, data);
	// } catch (NoSuchMethodException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// } catch (IllegalAccessException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IllegalArgumentException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (InvocationTargetException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// return this;
	//
	// }

	/**
	 * ����class�ͷ��������ַ���������������ַ��������з���
	 * @param mclass ��������������
	 * @param methodname �������ַ���
	 * @return
	 */
	private Method[] getDeclaredMethods(Class mclass, String methodname) {
		// getDeclaredMethods�������еĹ���˽�з����ͽӿڷ������������̳з�����getMethods��������public�ķ�����������ͽӿ�

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

	/**
	 * ����class�ͷ��������ַ���������������ַ����ķ���������������ͬ(������)
	 * 
	 * @param mclass
	 * @param methodname
	 * @param data
	 * @return
	 */
	private Method getDeclaredMethod(Class mclass, String methodname,
			Object[] data) {

		// �х�������ͷŵ�list��
		Method mMethod = null;
		Class[] dataClass = new Class[data.length];
//		boolean[] isBaseType = new boolean[data.length];// duiying
		int ifBaseType = 0;// �Ƿ��л�������,
		for (int i = 0; i < data.length; i++) {//һһ�õ���������
			Class itemClass = data[i].getClass();
			dataClass[i] = itemClass;
			if (isBaseTypeObject(itemClass)) {
//				isBaseType[i] = true;
				 if(ifBaseType==-1){
				 ifBaseType=0;
				 }
				ifBaseType += Math.pow(2, i);
			} else {
//				isBaseType[i] = false;
			}
		}

		finding: while (ifBaseType >= 0) {//goto�ж϶�������ifBaseType����Щλ�����ǻ�������
			try {
				mMethod = mclass.getDeclaredMethod(methodname, dataClass);
			} catch (NoSuchMethodException e) {
				if (ifBaseType > 0) {//ifBaseTypeҪ����0
					int i = 0;
					if (ifBaseType % 2 == 1) {//��ifBaseType��2ȡ��Ϊ1ʱ����ʾ��λ��1�����ǿ��ܵĻ�������
						if (ifBaseType > 1)//��ifBaseType����1������sqrt����ǰ�Ƕ����Ƶĵڼ�λ
							i = (int) Math.sqrt(ifBaseType);
						else
							i=0;
					} else {

					}

					dataClass[i] = objectClassTobaseType(dataClass[i]);

					ifBaseType = ifBaseType >> 1;
					continue finding;
				} else {
					e.printStackTrace();
					break;
				}
			}
		}

		return mMethod;
	}

	/**
	 * ����object���󣬻�øö���ĸ��������ʵ�ֽӿ�
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
	 * ����object���󣬵�����øö�������и��������ʵ�ֽӿ�
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
	 * ����Class���󣬻�øö���ĸ���͵�ǰ������ʵ�ֽӿ�
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
		// ����ͽӿ�
		if (superClass != null) {
			list.add(superClass);
		}
		if (thisInterface != null && thisInterface.length > 0) {
			list.addAll(Arrays.asList(thisInterface));
		}
		return list;
	}

	/**
	 * ����Class���󣬵�����øö�������и��������ʵ�ֽӿ�
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
		// ��������ĸ���ͽӿ�
		if (superClass != null) {
			list.add(superClass);
			list.addAll(getAllInterfaceAndSuperClass(superClass));
		}
		// �����ӿڵĸ���ͽӿ�(�ӿ�û��implements,ֻ��extends)
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
	 * ����objectתclass
	 * 
	 * @param obj
	 * @return
	 */
	private Class ObjectToClass(Object obj) {
		return obj.getClass();
	}

	/**
	 * ���objectתclass
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

	// ���ͣ������ͨ��Id�Ͷ���find�ӿؼ�
	// public <T extends View> ItemView bindView(int id, T t) {
	// t = (T) view.findViewById(id);
	// return this;
	// }
	// //���ͣ������ͨ��Id�����ͣ�find�ӿؼ�
	// public ItemView bindView(int id, Class<View> mclass) {
	// Object obj;
	// try {
	// obj = mclass.newInstance();
	// } catch (InstantiationException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IllegalAccessException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// obj = view.findViewById(id);
	// return this;
	// }

}
