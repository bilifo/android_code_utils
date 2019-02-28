package 数据库;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FSUtils {
	/**
	 * 通过R.layout的int，获得layout下的所有控件的class类型
	 */
	
	/**
	 * 通过class类的实例，得到其下的属性名称
	 */
	public List<String> getFieldName(Class mclass){
		List<String> fieldNames=new ArrayList<>();
//		String[] aa=mclass.ge
		Field[] field=mclass.getDeclaredFields();
		for(int i=0;i<field.length;i++){
			String mmname=field[i].getName();
			fieldNames.add(mmname);
		}
		return fieldNames;		
	}
	/**
	 * 通过class类的实例，得到其下的属性名称对应的值
	 */
	public Object getFieldValue(Object obj,String name){
//		Object obj=new Object();
		List<String> fieldNames=getFieldName(obj.getClass());
		List list =new ArrayList();
		
		return obj;
		
	}
	/**
	 * 通过class类的实例，得到其下的方法名称
	 */

	
    /**
     * 判断字段是否是字符类型
     */
    private boolean isCharType(Field field) {
        String type = field.getType().getName();
        return type.equals("char") || type.endsWith("Character");
    }
    /**
     * 得到对象的类型
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
}
