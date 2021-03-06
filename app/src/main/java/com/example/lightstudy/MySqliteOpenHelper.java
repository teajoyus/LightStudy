package com.example.lightstudy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MySqliteOpenHelper<T extends ReflectObject> extends SQLiteOpenHelper {
    private static final String TAG = "MySqliteOpenHelper";
    private static final int VERSION = 1;
    public SQLiteDatabase db;
    public String tableName;
    public Class entryClass;
    private Context context;
    private boolean hasDataBase = true;

    public MySqliteOpenHelper(Context context) {
        super(context, "sql_data.db", null, VERSION);
    }

    public MySqliteOpenHelper(Context context, Class<? extends ReflectObject> type) {
        super(context, "sql_data.db", null, VERSION);
        Log.i(TAG, "MySqliteOpenHelper db:" + getWritableDatabase());
        entryClass = type;
        tableName = entryClass.getSimpleName().toLowerCase();
        this.context = context;
        createTable(getWritableDatabase());
    }

    private void createTable(SQLiteDatabase db) {
        if (entryClass == null) {
            return;
        }
        try {
            ReflectObject o = (ReflectObject) entryClass.newInstance();
            db.execSQL(o.makeSQLTable());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate");
//        hasTable = false;
//        hasDataBase = false;
//        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onUpgrade");
    }

    /**
     * 返回所有的记录记录
     *
     * @param context
     * @return
     */
    public List<T> findAllData(Context context) {
        return findData(context,null,null,null,null,null,null);
    }

    /**
     * 返回所有的记录记录
     * @param context
     * @param columns  要查询的字段
     * @param selection  查询约束条件
     * @param selectionArgs 查询约束条件的参数替换
     * @param groupBy 分组
     * @param having
     * @param orderBy 排序
     * @return
     */
    public List<T> findData(Context context, String[] columns, String selection,
                               String[] selectionArgs, String groupBy, String having,
                               String orderBy) {
        // TODO Auto-generated method stub
        List<T> list = new ArrayList<T>();
        try {
            setDataBase(context);
            T object = null;
            Cursor c = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
            while (c.moveToNext()) {
                try {
                    object = (T) entryClass.newInstance();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                makeEntry(c, object);
                list.add(object);
            }
            c.close();
            db.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 插入一个记录
     *
     * @param data
     */
    public void insert(T data) {
        try {
            setDataBase(context);
            ContentValues cv = putContentValues(data);
            db.insert(tableName, null, cv);
            db.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 删除一个记录
     *
     * @param id
     */
    public void delete(int id) {
        try {
            setDataBase(context);
            db.delete(tableName, "id=?", new String[]{id + ""});
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 更新一个记录数据到数据库
     *
     * @param data
     * @return
     */
    public void update(T data) {
        try {
            setDataBase(context);
            ContentValues cv = putContentValues(data);
            db.update(tableName, cv, "id=?", new String[]{data.id + ""});
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 查找单个记录
     *
     * @param name
     * @return
     */
    public T findSimpleData(String name) {
        // TODO Auto-generated method stub
        T data = null;
        try {
            setDataBase(context);
            Cursor c = db.rawQuery("select * from " + tableName + " where name=?", new String[]{name + ""});
            if (c.moveToFirst()) {
                try {
                    data = (T) entryClass.newInstance();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                makeEntry(c, data);
            }
            c.close();
            db.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }

    public void setDataBase(Context context) throws IOException {
//        MySqliteOpenHelper helper = new MySqliteOpenHelper(context);
        db = getWritableDatabase();
    }

    private static ContentValues putContentValues(ReflectObject object) {
        return object.getContentValues();
    }

    private static void makeEntry(Cursor c, ReflectObject object) {
        object.fullEntry(c);
    }


}
