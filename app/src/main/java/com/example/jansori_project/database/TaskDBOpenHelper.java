package com.example.jansori_project.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jansori_project.dto.Task;

public class TaskDBOpenHelper {
    private static final String DATABASE_NAME = "taskDB";
    private static final int DATABASE_VERSION = 2;
    public static SQLiteDatabase myDB;
    private TaskDBHelper helper;
    private Context context;

    public TaskDBOpenHelper(Context context) {
        this.context = context;
    }

    public class TaskDBHelper extends SQLiteOpenHelper {

        public TaskDBHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }//constructor

        @Override
        public void onCreate(SQLiteDatabase db) {
            String tableSql = "CREATE TABLE tasks ("
                            + "_id INTEGER not null PRIMARY KEY autoincrement,"
                            + "title VARCHAR, "
                            + "color VARCHAR, "
                            + "daysofweek VARCHAR, "
                            + "mode INTEGER, "
                            + "hour INTEGER, "
                            + "minutes INTEGER, "
                            + "enabled INTEGER);";
            db.execSQL(tableSql);

        }//onCreate

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion == DATABASE_VERSION) {
                db.execSQL("drop table tasks");
                onCreate(db);
            }
        }//onUpgrade

    }//TaskDBHelper

    public TaskDBOpenHelper open() {
        helper = new TaskDBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        myDB = helper.getWritableDatabase();
        return this;
    }

    public void close() {
        myDB.close();
    }


    /*---------------------------------------------------------------------------------------*/
    // Insert DB
    public long insertColumn(Task t){
        ContentValues values = new ContentValues();
        values.put("title", t.getTitle());
        values.put("color", t.getColor());
        values.put("daysofweek", t.getDaysOfWeek());
        values.put("mode", t.getMode());
        values.put("hour", t.getHour());
        values.put("minutes", t.getMinute());
        values.put("enabled", t.getEnabled());

        return myDB.insert("tasks", null, values);
    }

    // restore DB
    public long restoreColumn(Task t){
        ContentValues values = new ContentValues();
        values.put("_id", t.getNo());
        values.put("title", t.getTitle());
        values.put("color", t.getColor());
        values.put("daysofweek", t.getDaysOfWeek());
        values.put("mode", t.getMode());
        values.put("hour", t.getHour());
        values.put("minutes", t.getMinute());
        values.put("enabled", t.getEnabled());

        return myDB.insert("tasks", null, values);
    }

    // Select All
    public Cursor selectAll(){
        return myDB.query("tasks", null, null, null, null, null, null);
    }//selectAll

    // Select Enabled
    public Cursor selectEnabled(){
        return myDB.query("tasks", null, "enabled="+1, null, null, null, null);
    }//selectEnabled

    // Delete DB
    public boolean deleteColumn(long id){
        return myDB.delete("tasks", "_id="+id, null) > 0;
    }//deleteColumn

    // Update DB
    public boolean updateColumn(Task t){
        ContentValues values = new ContentValues();
        values.put("title", t.getTitle());
        values.put("color", t.getColor());
        values.put("daysofweek", t.getDaysOfWeek());
        values.put("mode", t.getMode());
        values.put("hour", t.getHour());
        values.put("minutes", t.getMinute());
        values.put("enabled", t.getEnabled());
        return myDB.update("tasks", values, "_id="+t.getNo(), null) > 0;
    }//updateColumn

    // ID로 태스크 찾기
    public Cursor selectTaskById(long id){
        Cursor c = myDB.query("tasks", null, "_id="+id, null, null, null, null);

        if(c != null && c.getCount() != 0) {
            c.moveToFirst();
        }

        return c;
    }//selectTaskById

    // 이름으로 태스크 검색
    public Cursor selectTaskByTitle(String title){
        Cursor c = myDB.rawQuery( "select * from tasks where title LIKE" + "'%" + title + "%'" , null);

        return c;
    }//selectTaskByTitle

    //활성화 상태 변경
    public boolean updateEnabled(long id , int enabled){
        ContentValues values = new ContentValues();

        if(enabled == 0) {
            values.put("enabled", 1);
        } else {
            values.put("enabled", 0);
        }
        return myDB.update("tasks", values, "_id="+id, null) > 0;

    }//updateColumn

}//TaskDBOpenHelper
