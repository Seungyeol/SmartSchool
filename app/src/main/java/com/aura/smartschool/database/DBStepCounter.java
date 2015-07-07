package com.aura.smartschool.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.aura.smartschool.vo.WalkingVO;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-07-05.
 */
public class DBStepCounter extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db_steps";
    private static final int DATABASE_VERSION = 1;

    private static final String COL_DATE = "date";
    private static final String COL_STEPS = "steps";
    private static final String COL_WALKING_TIME = "time";
    private static final String COL_SYNC = "sync";

    private static final String CREATE_STEP_TABLE = "CREATE TABLE " + DATABASE_NAME +
                                                            " (" + COL_DATE + " INTEGER, " +
                                                                COL_STEPS + " INTEGER, " +
                                                                COL_WALKING_TIME + " INTEGER, " +
                                                                COL_SYNC + " INTEGER)";

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

    public ArrayList<WalkingVO> getAllSteps() {
        ArrayList<WalkingVO> result = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(DATABASE_NAME, new String[]{COL_DATE, COL_STEPS, COL_WALKING_TIME}, null, null, null, null, COL_DATE + " DESC" );
        cursor.moveToFirst();
        do {
            result.add(new WalkingVO(cursor.getLong(0), cursor.getInt(1), cursor.getInt(2)));
        } while (cursor.moveToNext());
        cursor.close();
        return result;
    }

    public int getWalkingTime(long date) {
        int time;
        Cursor cursor = getReadableDatabase().query(DATABASE_NAME, new String[]{COL_WALKING_TIME}, COL_DATE + " = ?", new String[]{String.valueOf(date)}, null, null,null );
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            time = -1;
        } else {
            time = cursor.getInt(0);
        }
        cursor.close();
        return time;
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
                values.put(COL_WALKING_TIME, 0);
                values.put(COL_SYNC, -1);
                getWritableDatabase().insert(DATABASE_NAME, null, values);
            }
            c.close();
            getWritableDatabase().setTransactionSuccessful();
        } finally {
            getWritableDatabase().endTransaction();
        }
    }



    public void updateSteps(long date, int steps, int time) {
        getWritableDatabase().execSQL("UPDATE " + DATABASE_NAME +
                " SET " + COL_STEPS + " = " + steps + ", " +
                           COL_WALKING_TIME + " = " + COL_WALKING_TIME + " + " + time +
                " WHERE " + COL_DATE + " = " + date);
    }

    public void completeSync(long date) {
        getWritableDatabase().execSQL("UPDATE " + DATABASE_NAME +
                " SET " +  COL_SYNC + " = " + 0 +
                " WHERE " +  COL_DATE + " = " + date);
    }
}
