package 数据库;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BaseDao {
	DBOpenHelper helper;
	SQLiteDatabase db;
	private static final String LOG = "BaseDao_Log";

	public BaseDao(Context context) {
		helper = new DBOpenHelper(context);
		db = helper.getWritableDatabase();
	}

	/**
	 * 插入一条数据
	 *
	 * @param 要插入的对象
	 * @return 返回-1代表插入数据库失败，否则成功，成功并返回新添记录的行号，与主键id无关
	 * @throws IllegalAccessException
	 */
	public long insert(Object obj) {
		Class<?> modeClass = obj.getClass();
		Field[] fields = modeClass.getDeclaredFields();
		ContentValues values = new ContentValues();// ContentValues是安卓用来向数据库传递数据的载体
		for (Field fd : fields) {
			fd.setAccessible(true);// 由于obj里有的数据可能是private型，需要设置访问权限才能读取到。
			String fieldName = fd.getName();
			// 剔除传入的obj对象的id值，由于框架默认设置主键id为主键自动增长
			if (fieldName.indexOf("_Id") != -1) {
				continue;
			}
			putValues(values, fd, obj);
		}
		return db.insert(DBUtils.getTableName(modeClass), null, values);
		// 这里我默认的是行号就是id号，但实际开发中可能不是。要么将在存入数据库前把id记下来，要么在return之前，通过行号查找一遍数据库，返回该行id。
	}

	/**
	 * 
	 * 将bean数据写入到ContentValues
	 * 
	 * @param values
	 *            ContentValues对象，是数据的载体，是船
	 * @param fd
	 *            obj对象的某一个field数据（对应的数据库中的某个字段名字），数据将会插入到对应字段中。如muser（user类的实例）
	 *            的“name”属性的值“张三”要存到数据库，putValues(values（这个就是我们最后得到的结果）,
	 *            封装成Field的“name”, muser（代表是哪个对象的name）)
	 * @param obj
	 *            要插入（保存于数据库中）的数据
	 */
	private void putValues(ContentValues values, Field fd, Object obj) {
		Class<?> clazz = values.getClass();
		try {
			// 获得传入数据的属性名称和属性值
			Object[] parameters = new Object[] { fd.getName(), fd.get(obj) };
			// 获得属性值的类型
			// fd.get(obj)方法，返回的是obj这个对象的fd这个属性的值。
			// 如Field f=User.class.getField("name");
			// User muserc=new User ();
			// String aaa=(String ) f.get(muserc);
			Class<?>[] parameterTypes = getParameterTypes(fd, fd.get(obj), parameters);
			Method method = clazz.getDeclaredMethod("put", parameterTypes);
			method.setAccessible(true);// Field 和 Method 都有setAccessible方法
			method.invoke(values, parameters);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到反射方法中的参数类型 传入参数field（属性），属性值，属性名称和属性值
	 */
	private Class<?>[] getParameterTypes(Field field, Object fieldValue, Object[] parameters) {
		Class<?>[] parameterTypes;
		if (isCharType(field)) {// 如果是字符类型
			parameters[1] = String.valueOf(fieldValue);
			parameterTypes = new Class[] { String.class, String.class };
		} else {
			if (field.getType().isPrimitive()) {// isPrimitive()用来判断指定的Class类是否为一个基本类型
				parameterTypes = new Class[] { String.class, getObjectType(field.getType()) };
			} else if ("java.util.Date".equals(field.getType().getName())) {
				parameterTypes = new Class[] { String.class, Long.class };
			} else {
				parameterTypes = new Class[] { String.class, field.getType() };
			}
		}
		return parameterTypes;
	}

	/**
	 * 判断字段是否是字符类型
	 */
	private boolean isCharType(Field field) {
		String type = field.getType().getName();
		return type.equals("char") || type.endsWith("Character");
	}

	/**
	 * 得到对象的类型，传入类型
	 */
	private Class<?> getObjectType(Class<?> primitiveType) {
		if (primitiveType != null) {
			if (primitiveType.isPrimitive()) {
				String basicTypeName = primitiveType.getName();
				if ("int".equals(basicTypeName)) {
					return Integer.class;
				} else if ("short".equals(basicTypeName)) {
					return Short.class;
				} else if ("long".equals(basicTypeName)) {
					return Long.class;
				} else if ("float".equals(basicTypeName)) {
					return Float.class;
				} else if ("double".equals(basicTypeName)) {
					return Double.class;
				} else if ("boolean".equals(basicTypeName)) {
					return Boolean.class;
				} else if ("char".equals(basicTypeName)) {
					return Character.class;
				}
			}
		}
		return null;
	}

	/**
	 * 查询数据库中所有的数据
	 *
	 * @param clazz
	 * @param <T>
	 *            以 List的形式返回数据库中所有数据
	 * @return 返回list集合
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	public <T> List<T> findAll(Class<T> clazz) {
		Cursor cursor = db.query(clazz.getSimpleName(), null, null, null, null, null, null);
		return getEntity(cursor, clazz);
	}

	/**
	 * 从数据库得到实体类
	 *
	 * @param cursor
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	private <T> List<T> getEntity(Cursor cursor, Class<T> clazz) {
		List<T> list = new ArrayList<>();
		try {
			if (cursor != null && cursor.moveToFirst()) {
				do {
					Field[] fields = clazz.getDeclaredFields();
					T modeClass = clazz.newInstance();
					for (Field field : fields) {
						Class<?> cursorClass = cursor.getClass();
						String columnMethodName = getColumnMethodName(field.getType());
						Method cursorMethod = cursorClass.getMethod(columnMethodName, int.class);
						Object value = cursorMethod.invoke(cursor, cursor.getColumnIndex(field.getName()));
						if (field.getType() == boolean.class || field.getType() == Boolean.class) {
							if ("0".equals(String.valueOf(value))) {
								value = false;
							} else if ("1".equals(String.valueOf(value))) {
								value = true;
							}
						} else if (field.getType() == char.class || field.getType() == Character.class) {
							value = ((String) value).charAt(0);
						} else if (field.getType() == Date.class) {
							long date = (Long) value;
							if (date <= 0) {
								value = null;
							} else {
								value = new Date(date);
							}
						}
						String methodName = makeSetterMethodName(field);
						// 通过方法名称和类型得到这个方法。
						Method method = clazz.getDeclaredMethod(methodName, field.getType());
						method.invoke(modeClass, value);// 动态调用类的方法,方法名.invoke（那个类,参数）
					}
					list.add(modeClass);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return list;
	}

	private String getColumnMethodName(Class<?> fieldType) {
		String typeName;
		if (fieldType.isPrimitive()) {
			typeName = DBUtils.capitalize(fieldType.getName());
		} else {
			typeName = fieldType.getSimpleName();
		}
		String methodName = "get" + typeName;
		if ("getBoolean".equals(methodName)) {
			methodName = "getInt";
		} else if ("getChar".equals(methodName) || "getCharacter".equals(methodName)) {
			methodName = "getString";
		} else if ("getDate".equals(methodName)) {
			methodName = "getLong";
		} else if ("getInteger".equals(methodName)) {
			methodName = "getInt";
		}
		return methodName;
	}

	// 制造方法名
	private String makeSetterMethodName(Field field) {
		String setterMethodName;
		String setterMethodPrefix = "set";
		if (isPrimitiveBooleanType(field) && field.getName().matches("^is[A-Z]{1}.*$")) {
			setterMethodName = setterMethodPrefix + field.getName().substring(2);
		} else if (field.getName().matches("^[a-z]{1}[A-Z]{1}.*")) {
			setterMethodName = setterMethodPrefix + field.getName();
		} else {
			setterMethodName = setterMethodPrefix + DBUtils.capitalize(field.getName());
		}
		return setterMethodName;
	}

	/**
	 * 根据指定条件返回满足条件的记录
	 *
	 * @param clazz
	 *            类
	 * @param select
	 *            条件语句 ：（"id>？"）
	 * @param selectArgs
	 *            条件(new String[]{"0"}) 查询id=0的记录
	 * @param <T>
	 *            类型
	 * @return 返回满足条件的list集合
	 */
	public <T> List<T> findByArgs(Class<T> clazz, String select, String[] selectArgs) {
		Cursor cursor = db.query(clazz.getSimpleName(), null, select, selectArgs, null, null, null);
		return getEntity(cursor, clazz);
	}

	/**
	 * 通过id查找制定数据
	 *
	 * @param clazz
	 *            指定类
	 * @param id
	 *            条件id
	 * @param <T>
	 *            类型
	 * @return 返回满足条件的对象
	 */
	public <T> T findById(Class<T> clazz, int id) {
		Cursor cursor = db.query(clazz.getSimpleName(), null, "" + getObject_IdName(clazz) + " = " + id, null, null,
				null, null);
		List<T> list;
		if (cursor == null) {
			return null;
		} else {
			list = getEntity(cursor, clazz);
			if (list == null) {
				return null;
			}
		}
		return list.get(0);
	}

	private String getObject_IdName(Class<?> primitiveType) {
		if (primitiveType != null) {
			Field[] fields = primitiveType.getDeclaredFields();
			for (Field mfield : fields) {
				if (mfield.getName().contains("_Id"))
					return mfield.getName();
			}
		}
		return null;
	}

	/**
	 * 判断字段是否是boolean类型
	 * 
	 * @param field
	 * @return
	 */
	private boolean isPrimitiveBooleanType(Field field) {
		Class<?> fieldType = field.getType();
		if ("boolean".equals(fieldType.getName())) {
			return true;
		}
		return false;
	}

	/**
	 * 把数据为id的从clazz表中删除
	 */
	public void deleteById(Class<?> clazz, long id) {// 删除这的id肯定要改，也要用到反射

		long _id = helper.getWritableDatabase().delete(DBUtils.getTableName(clazz),
				"" + getObject_IdName(clazz) + " = " + id, null);
		Log.w(LOG, "deleteById_id == " + _id);
	}

	/**
	 * 删除整个表
	 */
	public void deleteTable(Class<?> clazz) {
		helper.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + DBUtils.getTableName(clazz));
	}

	/**
	 * 根据id进行修改
	 */
	public void updateById(Class<?> clazz, ContentValues values, long id) {
		long _id = helper.getWritableDatabase().update(DBUtils.getTableName(clazz), values,
				"" + getObject_IdName(clazz) + " = " + id, null);
		Log.w(LOG, "updateById_id == " + _id);
	}

	public long update(Object obj, long id) {
		Class<?> modeClass = obj.getClass();
		Field[] fields = modeClass.getDeclaredFields();
		ContentValues values = new ContentValues();
		// int id = 0;
		for (Field fd : fields) {
			fd.setAccessible(true);
			String fieldName = fd.getName();
			// 剔除主键id值得保存，由于框架默认设置id为主键自动增长
			if (fieldName.indexOf("_Id") != -1) {
				try {
					// findByArgs(clazz, select, selectArgs)
					// id=fd.getInt(arg0);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}

			putValues(values, fd, obj);
		}
		return db.update(DBUtils.getTableName(modeClass), values, "" + getObject_IdName(modeClass) + " = " + id, null);
	}

}
