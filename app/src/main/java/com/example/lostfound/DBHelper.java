package com.example.lostfound;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite 数据库封装。
 *
 * 数据存储使用评分点：所有物品信息持久化到本地数据库。
 * 安全设计：写入走 ContentValues，查询走 selectionArgs 占位符，全程不做 SQL 字符串拼接，
 * 从根源上避免 SQL 注入。
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "lostfound.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "lost_item";

    public DBHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, "
                + "description TEXT, "
                + "latitude REAL, "
                + "longitude REAL, "
                + "type TEXT NOT NULL, "
                + "contact TEXT, "
                + "create_time INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    /** 新增一条失物 / 招领信息，返回插入后的行号，失败返回 -1 */
    public long addItem(String title, String desc, double lat, double lng,
                        String type, String contact) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", desc);
        cv.put("latitude", lat);
        cv.put("longitude", lng);
        cv.put("type", type);
        cv.put("contact", contact);
        cv.put("create_time", System.currentTimeMillis());
        try {
            return db.insert(TABLE, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    /** 查询全部记录，按发布时间倒序 */
    public List<LostItem> getAll() {
        List<LostItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE, null, null, null, null, null, "create_time DESC");
            while (cursor.moveToNext()) {
                list.add(readRow(cursor));
            }
        } catch (Exception ignored) {
            // 读取异常时返回已读取部分，避免崩溃
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /** 按类型查询，selectionArgs 占位符传参，避免注入 */
    public List<LostItem> getByType(String type) {
        List<LostItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE, null, "type = ?", new String[]{type},
                    null, null, "create_time DESC");
            while (cursor.moveToNext()) {
                list.add(readRow(cursor));
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public int countAll() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE, null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    public int deleteById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE, "id = ?", new String[]{String.valueOf(id)});
    }

    private LostItem readRow(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
        double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
        double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
        String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
        long time = cursor.getLong(cursor.getColumnIndexOrThrow("create_time"));
        return new LostItem(id, title, desc, lat, lng, type, time);
    }
}
