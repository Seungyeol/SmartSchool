package com.aura.smartschool.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.WalkingVO;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-07-05.
 */
public class DBStepCounter extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db_steps";
    private static final int DATABASE_VERSION = 3;

    private static final String COL_DATE = "date";
    private static final String COL_STEPS = "steps";
    private static final String COL_CALORIES = "calories";
    private static final String COL_DISTANCE = "distance";
    private static final String COL_ACTIVE_TIME = "active_time";
    private static final String COL_SYNC = "sync";

    private static final String CREATE_STEP_TABLE = "CREATE TABLE " + DATABASE_NAME +
                                                            " (" + COL_DATE + " INTEGER, " +
                                                                COL_STEPS + " INTEGER, " +
                                                                COL_CALORIES + " INTEGER, " +
                                                                COL_DISTANCE + " INTEGER, " +
                                                                COL_ACTIVE_TIME + " INTEGER, " +
                                                                COL_SYNC + " INTEGER)";

    private static final String DROP_STEP_TABLE = "DROP TABLE IF EXISTS '" + DATABASE_NAME +"'";

    private static DBStepCounter INSTANCE;

    private DBStepCounter(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static DBStepCounter getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DBStepCounter.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DBStepCounter(context, DATABASE_NAME, null, DATABASE_VERSION);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STEP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_STEP_TABLE);
        onCreate(db);
    }

    public int getSteps(long date) {
        int steps;
        Cursor cursor = getReadableDatabase().query(DATABASE_NAME, new String[]{COL_STEPS}, COL_DATE + " = ?", new String[]{String.valueOf(date)}, null, null,null );
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            steps = -1;
        } else {
            steps = cursor.getInt(0);
        }
        cursor.close();
        return steps;
    }

    public int getWalkingTime(long date) {
        int time;
        Cursor cursor = getReadableDatabase().query(DATABASE_NAME, new String[]{COL_ACTIVE_TIME}, COL_DATE + " = ?", new String[]{String.valueOf(date)}, null, null,null );
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            time = -1;
        } else {
            time = cursor.getInt(0);
        }
        cursor.close();
        return time;
    }

    public ArrayList<WalkingVO> getAllSteps() {
        ArrayList<WalkingVO> result = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(DATABASE_NAME, new String[]{COL_DATE, COL_STEPS, COL_CALORIES, COL_DISTANCE, COL_ACTIVE_TIME}, null, null, null, null, COL_DATE + " DESC" );
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                result.add(new WalkingVO(cursor.getLong(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4)));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }

    public void insertNewDate(long date) {
        getWritableDatabase().beginTransaction();
        try {
            Cursor c = getReadableDatabase().query(DATABASE_NAME, new String[]{"date"}, "date = ?",
                    new String[]{String.valueOf(date)}, null, null, null);
            if (c.getCount() == 0 ) {
                ContentValues values = new ContentValues();
                values.put(COL_DATE, date);
                values.put(COL_STEPS, 0);
                values.put(COL_CALORIES, 0);
                values.put(COL_DISTANCE, 0);
                values.put(COL_ACTIVE_TIME, 0);
                values.put(COL_SYNC, -1);
                getWritableDatabase().insert(DATABASE_NAME, null, values);
            }
            c.close();
            getWritableDatabase().setTransactionSuccessful();
        } finally {
            getWritableDatabase().endTransaction();
        }
    }

    public void updateSteps(long date, int steps, int calories, int distance, int activeTime) {
        // 센서가 누적값을 올려주기 때문에 걸음걸이와 거리는 전달된 값으로 갱신한다.
        getWritableDatabase().execSQL("UPDATE " + DATABASE_NAME +
                " SET " + COL_STEPS + " = " + steps + ", " +
                COL_CALORIES + " = " + COL_CALORIES + " + " + calories + ", " +
                COL_DISTANCE + " = " + distance + ", " +
                COL_ACTIVE_TIME + " = " + COL_ACTIVE_TIME + " + " + activeTime +
                " WHERE " + COL_DATE + " = " + date);
    }

    public WalkingVO getWalkingDataForUpdate() {
        WalkingVO result = null;
        Cursor cursor = getReadableDatabase().query(DATABASE_NAME, new String[]{COL_DATE, COL_STEPS, COL_CALORIES, COL_DISTANCE, COL_ACTIVE_TIME}, "sync = ? AND date != ?",
                new String[]{String.valueOf(-1), String.valueOf(Util.getTodayTimeInMillis())}, null, null, COL_DATE + " ASC" );
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = new WalkingVO(cursor.getLong(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4));
            cursor.close();
        }
        return result;
    }

    public void completeSync(long date) {
        getWritableDatabase().execSQL("UPDATE " + DATABASE_NAME +
                " SET " +  COL_SYNC + " = " + 0 +
                " WHERE " +  COL_DATE + " = " + date);
    }
}
