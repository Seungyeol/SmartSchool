package com.aura.smartschool.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aura.smartschool.vo.ConsultChatVO;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2015-07-05.
 */
public class DBConsultChat extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db_consult";
    private static final String TB_CONSULT = "tb_consult";
    private static final int DATABASE_VERSION = 1;

    private static final String COL_ID = "_id";
    private static final String COL_OWN_TYPE = "ownType";
    private static final String COL_BODY = "body";
    private static final String COL_TIME = "time";
    private static final String COL_SEND_RESULT = "result";

    private static final String CREATE_STEP_TABLE = "CREATE TABLE " + TB_CONSULT +
                                                            " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                    COL_OWN_TYPE + " INTEGER, " +
                                                                    COL_BODY + " TEXT, " +
                                                                    COL_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                                                                    COL_SEND_RESULT + " INTEGER)";

    private static final String DROP_CONSULT_TABLE = "DROP TABLE IF EXISTS '" + TB_CONSULT +"'";

    private static DBConsultChat INSTANCE;

    private DBConsultChat(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static DBConsultChat getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DBConsultChat.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DBConsultChat(context, DATABASE_NAME, null, DATABASE_VERSION);
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
        db.execSQL(DROP_CONSULT_TABLE);
        onCreate(db);
    }

    public ArrayList<ConsultChatVO> getAllMsg() {
        ArrayList<ConsultChatVO> result = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(TB_CONSULT, new String[]{COL_ID, COL_OWN_TYPE, COL_BODY, COL_TIME, COL_SEND_RESULT}, null, null, null, null, COL_TIME + " ASC" );
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                result.add(new ConsultChatVO(cursor.getInt(1), cursor.getString(2), new Date(cursor.getLong(3)), cursor.getInt(4)));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }

    public long insertMsg(int ownType, String msg, Date inputTime, int sendResult) {
        long id = -1;
        getWritableDatabase().beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_OWN_TYPE, ownType);
            values.put(COL_BODY, msg);
            values.put(COL_TIME, inputTime.getTime());
            values.put(COL_SEND_RESULT, sendResult);
            id = getWritableDatabase().insert(TB_CONSULT, null, values);
            getWritableDatabase().setTransactionSuccessful();
        } finally {
            getWritableDatabase().endTransaction();
        }
        return id;
    }

    public void updateSendResult(int id, int sendResult) {
        // 센서가 누적값을 올려주기 때문에 걸음걸이와 거리는 전달된 값으로 갱신한다.
        getWritableDatabase().execSQL("UPDATE " + TB_CONSULT +
                " SET " + COL_SEND_RESULT + " = " + sendResult +
                " WHERE " + COL_ID + " = " + id);
    }
}
