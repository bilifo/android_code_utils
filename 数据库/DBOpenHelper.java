package 数据库;

import java.lang.reflect.Field;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import bean.Application;
import bean.Dept;
import bean.Users;
import utils.DBUtils;

public class DBOpenHelper extends SQLiteOpenHelper {
	private Context mcontext;
	public static final String DATABASE_NAME = "db_cross";
	public static final int DB_VERSION = 1;
	public static final String INFO_TABLE_NAME = "tb_info";
	// 创建user表
	public static final String CREATE_USERS = "create table users(userId integer primary key autoincrement,userName varchar(10),passwd varchar(10),power varchar(10),age varchar(10),tel varchar(10),addr varchar(10),education varchar(10),deptId integer,job varchar(10),wage varchar(10),others varchar(10))";
	// 创建 部门表
	public static final String CREATE_DEPT = "create table dept(deptId integer primary key autoincrement,deptName varchar(10),manager varchar(10))";
	// 创建出差、加班、请假表
	public static final String TAG = "SqliteHelper";
	public static final String CREATE_APPLICATION = "create table application(application_Id integer primary key autoincrement, type varchar(10),userId integer,userName varchar(10),reason varchar(10),term varchar(10),date varchar(10),check varchar(10),finishDate varchar(10))";

	public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		mcontext=context;
	}

	public DBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
		mcontext=context;
	}

	public void openDB() {
		DBOpenHelper helper = new DBOpenHelper(mcontext);
		SQLiteDatabase db = helper.getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DBUtils.getCreateTableSql(Users.class));
		db.execSQL(DBUtils.getCreateTableSql(Application.class));
		db.execSQL(DBUtils.getCreateTableSql(Dept.class));
//		BaseDao dao=new BaseDao(mcontext);
		ContentValues values1=new ContentValues();
		values1.put("userName","小新");
		values1.put("power","1");
		values1.put("passwd","110");
		ContentValues values2=new ContentValues();
		values2.put("name","人事部");
		values2.put("manager","张三");		
		ContentValues values3=new ContentValues();
		
		db.insert("Users", null, values1);
		db.insert("Application", null, values2);
		db.insert("Dept", null, values3);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int arg2) {
		switch (oldVersion) {
		case 1:
			// db.execSQL(CREATE_APPLICATION);
			// db.execSQL(CREATE_DEPT);
			// db.execSQL(CREATE_USERS);
//			db.execSQL(getCreateTableSql(Users.class));
//			db.execSQL(getCreateTableSql(Application.class));
//			db.execSQL(getCreateTableSql(Dept.class));
		case 2:
		default:

		}
	}

	/**
	 * 利用反射创建一个数据库表语句
	 * @param clazz
	 * @return
	 */
//	private String getCreateTableSql(Class<?> clazz) {
//		StringBuilder sb = new StringBuilder();
//		// 将类名作为表名
//		String tabName = DBUtils.getTableName(clazz);
//		// sb.append("create table ").append(tabName).append(" (id INTEGER
//		// PRIMARY KEY AUTOINCREMENT, ");
//		sb.append("create table ").append(tabName).append(" (");
//		// 得到类中所有属性对象数组
//		Field[] fields = clazz.getDeclaredFields();
//		// getFields()只能获取public的字段，包括父类的。而getDeclaredFields()只能获取自己声明的各种字段，包括public，protected，private。
//		for (Field fd : fields) {
//			String fieldName = fd.getName();
//			String fieldType = fd.getType().getName();
//			// equals方法在比较的过程中严格区分大小写,而equalsIgnoreCase方不考虑大小写
//			if ((fieldName.indexOf("_Id") != -1)) {
//				// continue;
//				sb.append(fieldName).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
//			} else {
//				sb.append(fieldName).append(DBUtils.getColumnType(fieldType)).append(", ");
//			}
//		}
//		int len = sb.length();
//		sb.replace(len - 2, len, ")");
//		Log.d(TAG, "the result is " + sb.toString());
//		return sb.toString();
//	}

}
