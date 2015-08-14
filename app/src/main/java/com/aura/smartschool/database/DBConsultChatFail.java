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

    public ArrayList<ConsultVO> getAllFailMsg(TYPE chatType) {
        ArrayList<ConsultVO> result = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(chatType.getTableName(), new String[]{COL_ID, COL_BODY, COL_TIME}, null, null, null, null, COL_TIME + " ASC" );
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

    public long insertMsg(TYPE chatType, String msg) {
        long id = -1;
        getWritableDatabase().beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_BODY, msg);
            id = getWritableDatabase().insert(chatType.getTableName(), null, values);
            getWritableDatabase().setTransactionSuccessful();
        } finally {
            getWritableDatabase().endTransaction();
        }
        return id;
    }

    public void removeMessage(TYPE chatType, long id) {
        getWritableDatabase().execSQL("DELETE FROM "+ chatType.getTableName() +
                " WHERE " + COL_ID + " = " + id);
    }

    public enum TYPE {
        //학교폭력, 친구관계, 가정문제, 성상담, 학업상담, 진로상담, 심리상담, 성장상담, 흡연상담.
        SCHOOL_VIOLENCE_CONSULT("tb_school_violence_fail", 1001),
        FRIEND_RELATIONSHIP_CONSULT("tb_friend_relationship_fail", 1002),
        FAMILY_CONSULT("tb_family_fail", 1003),
        SEXUAL_CONSULT("tb_sexual_fail", 1),
        ACADEMIC_CONSULT("tb_academic_fail", 2),
        CAREER_CONSULT("tb_career_fail", 3),
        PSYCHOLOGY_CONSULT("tb_psychology_fail", 4),
        GROWTH_CONSULT("tb_growth_fail", 5),
        SMOKING_CONSULT("tb_smoking_fail", 6);

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
