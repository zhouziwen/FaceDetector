package com.aiyouwei.drk.shelf.admin.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import static android.text.TextUtils.isEmpty;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "aiyouwei.db";
    private static final int DB_VERSION = 1;

    private static final String TB_EMPLOYEE = "employee";

    private static final String USER_ID = "user_id";
    private static final String NAME = "name";
    private static final String PICK_UP = "pick_up";
    private static final String DOOR_OPEN = "door_open";
    private static final String FACE_INFO = "face_info";
    private static final String PHOTO = "photo";
    private static final String RECT_INFO = "rect_info";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";

    private static SQLiteDatabase db;
    private static DatabaseHelper mInstance;

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new DatabaseHelper(context);
            db = mInstance.getWritableDatabase();
        }

        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TB_EMPLOYEE + " (" +
                USER_ID + " INTEGER PRIMARY KEY," +
                NAME + " TEXT UNIQUE," +
                PICK_UP + " INTEGER, " +
                DOOR_OPEN + " INTEGER, " +
                FACE_INFO + " TEXT, " +
                PHOTO + " TEXT, " +
                RECT_INFO + " TEXT, " +
                CREATE_TIME + " INTEGER, " +
                UPDATE_TIME + " INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TB_EMPLOYEE);
        onCreate(sqLiteDatabase);
    }

    public void batchInsert(List<Employee> list) {
        db.beginTransaction();

        try {
            for (Employee e : list) {
                if (!isEmpty(e.userid)) {
                    boolean exist = isEmployeeExist(e.userid);
                    if (!exist) {
                        insert(e);
                    } else {
                        update(e);
                    }
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }
    }

    public long insert(Employee e) {
        if (null == e || isEmpty(e.userid)) return -1;

        ContentValues cv = new ContentValues();
        cv.put(USER_ID, e.userid);
        cv.put(NAME, e.username);
        cv.put(PICK_UP, e.pickup);
        cv.put(DOOR_OPEN, e.door);
        cv.put(FACE_INFO, e.facedata);
        cv.put(PHOTO, e.photo);
        cv.put(RECT_INFO, e.rect);
        cv.put(CREATE_TIME, System.currentTimeMillis());
      return   db.insert(TB_EMPLOYEE, null, cv);
    }

    public void update(Employee e) {
        ContentValues cv = new ContentValues();
        if (!isEmpty(e.username)) {
            cv.put(NAME, e.username);
        }
        cv.put(PICK_UP, e.pickup);
        cv.put(DOOR_OPEN, e.door);
        if (!isEmpty(e.facedata)) {
            cv.put(FACE_INFO, e.facedata);
        }

        if (!isEmpty(e.photo)) {
            cv.put(PHOTO, e.photo);
        }

        if (!isEmpty(e.rect)) {
            cv.put(RECT_INFO, e.rect);
        }

        cv.put(UPDATE_TIME, System.currentTimeMillis());
        String where = USER_ID + "='" + e.userid + "'";
        db.update(TB_EMPLOYEE, cv, where, null);
    }

    public boolean deleteEmployee(String userId) {
        if (isEmpty(userId)) return false;

        String where = USER_ID + "='" + userId + "'";
        Log.i("test", "#delete: " + where);
        return db.delete(TB_EMPLOYEE, where, null) > 0;
    }

    public Employee queryEmployee(String userId) {
        if (isEmpty(userId)) return null;

        Cursor cursor = null;

        String sql = "SELECT * FROM " + TB_EMPLOYEE + " WHERE " + USER_ID + "='" + userId + "'";

        Employee e = null;
        try {
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                if (null == e) {
                    e = new Employee();
                }
                e.userid = cursor.getString(0);
                e.username = cursor.getString(1);
                e.pickup = cursor.getInt(2);
                e.door = cursor.getInt(3);
                e.facedata = cursor.getString(4);
                e.photo = cursor.getString(5);
                e.rect = cursor.getString(6);
            }
        } finally {
            if (null != cursor) cursor.close();
        }

        return e;
    }

//    public List<Employee> getEmployeeList() {
//        Cursor cursor = null;
//
//        List<Employee> list = new ArrayList<>();
//        String sql = "SELECT * FROM " + TB_EMPLOYEE;
//
//        try {
//            cursor = db.rawQuery(sql, null);
//            while (cursor.moveToNext()) {
//                Employee e = new Employee();
//                e.id = cursor.getString(0);
//                e.name = cursor.getString(1);
//                e.canPickUp = cursor.getInt(2) == 1;
//                e.canOpen = cursor.getInt(3) == 1;
//                e.faceInfo = cursor.getBlob(4);
//                e.photo = cursor.getBlob(5);
//                list.add(e);
//            }
//        } finally {
//            if (null != cursor) cursor.close();
//        }
//
//        return list;
//    }

    public boolean isEmployeeExist(String id) {
        String sql = "SELECT * FROM " + TB_EMPLOYEE + " WHERE " + USER_ID + "='" + id + "'";
        Cursor cursor = null;
        boolean b = false;

        try {
            cursor = db.rawQuery(sql, null);
            b = cursor.moveToNext();
        } catch (Exception e) {
        } finally {
            if (null != cursor) cursor.close();
        }
        return b;
    }
}
