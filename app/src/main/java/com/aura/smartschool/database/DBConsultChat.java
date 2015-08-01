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

    public static final int MSG_FROM_TEACHER = 1;
    public static final int MSG_FROM_ME = 0;

    private static final String DATABASE_NAME = "db_consult";

    private static final int DATABASE_VERSION = 2;

    private static final String COL_ID = "_id";
    private static final String COL_BODY = "body";
    private static final String COL_MSG_FROM = "msgFrom";
    private static final String COL_TIME = "time";
    private static final String COL_SEND_RESULT = "result";

    private static final String CREATE_TABLE = "CREATE TABLE %s " +
                                                            " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                    COL_MSG_FROM + " INTEGER, " +
                                                                    COL_BODY + " TEXT, " +
                                                                    COL_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                                                                    COL_SEND_RESULT + " INTEGER)";


    private static final String DROP_TABLE = "DROP TABLE IF EXISTS '%s'";

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
        for (TYPE type : TYPE.values()) {
            db.execSQL(String.format(CREATE_TABLE, type.getTableName()));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (TYPE type : TYPE.values()) {
            db.execSQL(String.format(DROP_TABLE, type.getTableName()));
        }

        onCreate(db);
    }

    public ArrayList<ConsultChatVO> getAllMsg(TYPE chatType) {
        ArrayList<ConsultChatVO> result = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(chatType.getTableName(), new String[]{COL_ID, COL_MSG_FROM, COL_BODY, COL_TIME, COL_SEND_RESULT}, null, null, null, null, COL_TIME + " ASC" );
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                result.add(new ConsultChatVO(cursor.getLong(0), cursor.getInt(1), cursor.getString(2), new Date(cursor.getLong(3)), cursor.getInt(4)));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }

    public long insertMsg(TYPE chatType, String msg, int msgFrom, Date inputTime, int sendResult) {
        long id = -1;
        getWritableDatabase().beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_BODY, msg);
            values.put(COL_MSG_FROM, msgFrom);
            values.put(COL_TIME, inputTime.getTime());
            values.put(COL_SEND_RESULT, sendResult);
            id = getWritableDatabase().insert(chatType.getTableName(), null, values);
            getWritableDatabase().setTransactionSuccessful();
        } finally {
            getWritableDatabase().endTransaction();
        }
        return id;
    }

    public void updateSendResult(TYPE chatType, long id, int sendResult) {
        getWritableDatabase().execSQL("UPDATE " + chatType.getTableName() +
                " SET " + COL_SEND_RESULT + " = " + sendResult +
                " WHERE " + COL_ID + " = " + id);
    }

    public void removeMessage(TYPE chatType, long id) {
        getWritableDatabase().execSQL("DELETE FROM "+ chatType.getTableName() +
                " WHERE " + COL_ID + " = " + id);
    }

    public enum TYPE {
        //학교폭력, 친구관계, 가정문제, 성상담, 학업상담, 진로상담, 심리상담, 성장상담, 흡연상담.
        SCHOOL_VIOLENCE_CONSULT("tb_school_violence", 1001),
        FRIEND_RELATIONSHIP_CONSULT("tb_friend_relationship", 1002),
        FAMILY_CONSULT("tb_family", 1003),
        SEXUAL_CONSULT("tb_sexual", 1),
        ACADEMIC_CONSULT("tb_academic", 2),
        CAREER_CONSULT("tb_career", 3),
        PSYCHOLOGY_CONSULT("tb_psychology", 4),
        GROWTH_CONSULT("tb_growth", 5),
        SMOKING_CONSULT("tb_smoking", 6);

        private String tbName;
        private int code;

        private TYPE (String tbName, int code) {
            this.tbName = tbName;
            this.code = code;
        }

        public String getTableName() {
            return this.tbName;
        }

        public int getCode() {
            return this.code;
        }

        public TYPE findConsultType(String tbName) {
            TYPE[] types = values();
            for (TYPE type:types) {
                if (type.getTableName().equalsIgnoreCase(tbName)) {
                    return type;
                }
            }
            return null;
        }

        public TYPE findConsultType(int code) {
            TYPE[] types = values();
            for (TYPE type:types) {
                if (type.getCode() == code) {
                    return type;
                }
            }
            return null;
        }
    }
}
