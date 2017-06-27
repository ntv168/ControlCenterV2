package center.control.system.vash.controlcenter.nlp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.database.SQLiteManager;

/**
 * Created by Thuans on 5/29/2017.
 */

public class TermSQLite {

    public static final String TABLE_TARGET_TERM = "target_term";
    public static final String TABLE_HUMAN_TERM = "human_term";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_TFIDF_POINT = "tfidfPoint";
    private static final String DETECT_DEVICE_ID = "detectDeviceId";
    private static final String DETECT_AREA_ID = "detectAreaId";
    private static final String DETECT_SCRIPT_ID = "detectScriptId";
    private static final String DETECT_FUNCTION_ID = "detectFunctionId";
    private static final String DETECT_SOCIAL_ID = "detectSocialId";

    public static String createTargetTerm(){
        return "CREATE TABLE " + TABLE_TARGET_TERM  + "("
                + KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT    ,"
                + KEY_CONTENT  + "  TEXT    , "
                + DETECT_DEVICE_ID + " INTEGER , "
                + DETECT_AREA_ID + " INTEGER , "
                + DETECT_SCRIPT_ID + " INTEGER , "
                + KEY_TFIDF_POINT + " REAL "+ " ) ";
    }

    public static String createHumanTerm(){
        return "CREATE TABLE " + TABLE_HUMAN_TERM  + "("
                + KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT    ,"
                + KEY_CONTENT  + "  TEXT    , "
                + DETECT_FUNCTION_ID + " INTEGER , "
                + DETECT_SOCIAL_ID + " INTEGER , "
                + KEY_TFIDF_POINT + " REAL "+ ")";
    }


    public int insertTargetTerm(TargetTernEntity term) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, term.getContent());
        values.put(KEY_TFIDF_POINT, term.getTfidfPoint());
        values.put(DETECT_DEVICE_ID, term.getDetectDeviceId());
        values.put(DETECT_AREA_ID, term.getDetectAreaId());
        values.put(DETECT_SCRIPT_ID, term.getDetectScriptId());

        // Inserting Row
        int newId=(int)db.insert(TABLE_TARGET_TERM, null, values);
        SQLiteManager.getInstance().closeDatabase();

        return newId;
    }
    public int insertHumanTerm(TermEntity term) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, term.getContent());
        values.put(KEY_TFIDF_POINT, term.getTfidfPoint());
        values.put(DETECT_FUNCTION_ID, term.getDetectFunctionId());
        values.put(DETECT_SOCIAL_ID, term.getDetectSocialId());

        // Inserting Row
        int newId=(int)db.insert(TABLE_HUMAN_TERM, null, values);
        SQLiteManager.getInstance().closeDatabase();

        return newId;
    }


    public List<TargetTernEntity> getTargetInSentence(String sentence){
        List<TargetTernEntity> result = new ArrayList<>();

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_TARGET_TERM
//                + " WHERE ? MATCH '*' + "+KEY_CONTENT+" + '*' ";
                + " WHERE instr(?,"+KEY_CONTENT+") > 0";

        Log.d(TABLE_TARGET_TERM, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery,  new String[]{sentence});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TargetTernEntity term = new TargetTernEntity();
                term.setContent(cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
                term.setDetectAreaId(cursor.getInt(cursor.getColumnIndex(DETECT_AREA_ID)));
                term.setDetectDeviceId(cursor.getInt(cursor.getColumnIndex(DETECT_DEVICE_ID)));
                term.setDetectScriptId(cursor.getInt(cursor.getColumnIndex(DETECT_SCRIPT_ID)));
                term.setTfidfPoint(cursor.getDouble(cursor.getColumnIndex(KEY_TFIDF_POINT)));

//                Log.d(TABLE_TERM, term.getContent());
                result.add(term);
            } while (cursor.moveToNext());
        }

        cursor.close();
        SQLiteManager.getInstance().closeDatabase();

        return result;
    }
    public List<TermEntity> getHumanIntentInSentence(String sentence){
        List<TermEntity> result = new ArrayList<>();

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_HUMAN_TERM
//                + " WHERE ? MATCH '*' + "+KEY_CONTENT+" + '*' ";
                + " WHERE instr(?,"+KEY_CONTENT+") > 0";

        Log.d(TABLE_HUMAN_TERM, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery,  new String[]{sentence});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TermEntity term = new TermEntity();
                term.setContent(cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
                term.setDetectFunctionId(cursor.getInt(cursor.getColumnIndex(DETECT_FUNCTION_ID)));
                term.setDetectSocialId(cursor.getInt(cursor.getColumnIndex(DETECT_SOCIAL_ID)));
                term.setTfidfPoint(cursor.getDouble(cursor.getColumnIndex(KEY_TFIDF_POINT)));

//                Log.d(TABLE_TERM, term.getContent());
                result.add(term);
            } while (cursor.moveToNext());
        }

        cursor.close();
        SQLiteManager.getInstance().closeDatabase();

        return result;

    }

    public void clearAll( ) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.delete(TABLE_TARGET_TERM,null,null);
        db.delete(TABLE_HUMAN_TERM,null,null);
        SQLiteManager.getInstance().closeDatabase();
    }
}
