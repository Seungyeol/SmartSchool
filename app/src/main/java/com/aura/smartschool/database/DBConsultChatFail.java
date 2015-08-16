package com.aura.smartschool.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aura.smartschool.vo.ConsultVO;

import java.util.ArrayList;
import java.util.Date;

public class DBConsultChatFail extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db_consult_fail";

    private static final int DATABASE_VERSION = 1;

    private static final String COL_ID = "_id";
    private static final String COL_BODY = "body";
    private static final String COL_TIME = "time";

    private static final String CREATE_TABLE = "CREATE TABLE %s " +
                                                            " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                    COL_BODY + " TEXT, " +
                                                                    COL_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP)";


    private static final String DROP_TABLE = "DROP TABLE IF EXISTS '%s'";

    private static DBConsultChatFail INSTANCE;

    private DBConsultChatFail(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static DBConsultChatFail getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DBConsultChatFail.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DBConsultChatFail(context, DATABASE_NAME, null, DATABASE_VERSION);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (ConsultType type : ConsultType.values()) {
            db.execSQL(String.format(CREATE_TABLE, type.failTableName));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (ConsultType type : ConsultType.values()) {
            db.execSQL(String.format(DROP_TABLE, type.failTableName));
        }

        onCreate(db);
    }

    public ArrayList<ConsultVO> getAllFailMsg(ConsultType chatType) {
        ArrayList<ConsultVO> result = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(chatType.failTableName, new String[]{COL_ID, COL_BODY, COL_TIME}, null, null, null, null, COL_TIME + " ASC" );
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                ConsultVO consult = new ConsultVO();
                consult.consultId = cursor.getLong(0);
                consult.who = 0;
                consult.content = cursor.getString(1);
                consult.created = new Date(cursor.getLong(2));
                result.add(consult);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }

    public long insertMsg(ConsultType chatType, String msg) {
        long id = -1;
        getWritableDatabase().beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_BODY, msg);
            id = getWritableDatabase().insert(chatType.failTableName, null, values);
            getWritableDatabase().setTransactionSuccessful();
        } finally {
            getWritableDatabase().endTransaction();
        }
        return id;
    }

    public void removeMessage(ConsultType chatType, long id) {
        getWritableDatabase().execSQL("DELETE FROM "+ chatType.failTableName +
                " WHERE " + COL_ID + " = " + id);
    }
}
