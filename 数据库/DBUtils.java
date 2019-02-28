package 数据库;

import java.lang.reflect.Field;
import java.util.Locale;

import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @author PanJunLong
 *
 */
public class DBUtils {
	public static String getCreateTableSql(Class<?> clazz) {
		StringBuilder sb = new StringBuilder();
		// 将类名作为表名，如此时传入的user类class，那表名将会是user
		String tabName = DBUtils.getTableName(clazz);
		sb.append("create table ").append(tabName).append(" (");
		// 得到类中所有属性对象数组
		Field[] fields = clazz.getDeclaredFields();
		// getFields()只能获取public的字段，包括父类的。而getDeclaredFields()只能获取自己声明的各种字段，包括public，protected，private。
		for (Field fd : fields) {
//getName()和getType()方法，可以分别获取封装在Field里的类的字段的名称和类型。如user下的‘name’，会得到“name”“String”
			String fieldName = fd.getName();
			String fieldType = fd.getType().getName();
		// equals方法在比较的过程中严格区分大小写,而equalsIgnoreCase方不考虑大小写
			if ((fieldName.indexOf("_Id") != -1)) {
				// 由于需要设置主键的关系，这里定死了所有传入的类的主键id名称只能是“***_Id”的形式，continue;
				sb.append(fieldName).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
			} else {
				sb.append(fieldName).append(DBUtils.getColumnType(fieldType)).append(", ");
			}
		}
		int len = sb.length();
		sb.replace(len - 2, len, ")");
		Log.d(TAG, "the result is " + sb.toString());
		return sb.toString();
	}


//得到每一列字段的数据类型
    public static String getColumnType(String type) {
        String value = null;
        if (type.contains("String")) {
            value = " text ";
        } else if (type.contains("int")) {
            value = " integer ";
        } else if (type.contains("boolean")) {
            value = " boolean ";
        } else if (type.contains("float")) {
            value = " float ";
        } else if (type.contains("double")) {
            value = " double ";
        } else if (type.contains("char")) {
            value = " varchar ";
        } else if (type.contains("long")) {
            value = " long ";
        }
        return value;
}

//得到表名
    public static String getTableName(Class<?> clazz){
        return clazz.getSimpleName();
    }

    public static String capitalize(String string) {
        if (!TextUtils.isEmpty(string)) {//TextUtils是系统自带类
            return string.substring(0, 1).toUpperCase(Locale.US) + string.substring(1);
        }
        return string == null ? null : "";
    }
}
