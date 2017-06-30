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
    public static final String TABLE_OWNER_TRAIN_TERM = "owner_train_term";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_NAME = "name";
    private static final String KEY_TFIDF_POINT = "tfidfPoint";
    private static final String DETECT_DEVICE_ID = "detectDeviceId";
    private static final String DETECT_AREA_ID = "detectAreaId";
    private static final String DETECT_SCRIPT_ID = "detectScriptId";
    private static final String DETECT_FUNCTION_ID = "detectFunctionId";
    private static final String DETECT_SOCIAL_ID = "detectSocialId";
    private static final String KEY_TYPE  = "type";
    private static final String KEY_WORDS = "words";

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

    public static String createTrainTerm(){
        return "CREATE TABLE " + TABLE_OWNER_TRAIN_TERM  + "("
                + KEY_NAME  + " TEXT PRIMARY KEY  ,"
                + KEY_TYPE + " TEXT , "
                + KEY_WORDS + " TEXT "+ ")";
    }

    public static String insertOrUpdateTrain(OwnerTrainEntity train) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_OWNER_TRAIN_TERM
                + " WHERE "+KEY_NAME+" = ? ";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{train.getName()});
        if (cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(KEY_CONTENT, train.getWords()+" "+cursor.getString(cursor.getColumnIndex(KEY_WORDS)));
            db.update(TABLE_OWNER_TRAIN_TERM, values, KEY_NAME + " = "+train.getName(), null);
            return train.getName();
        } else {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, train.getName());
            values.put(KEY_TYPE, train.getType());
            values.put(KEY_WORDS, train.getWords());
            db.insert(TABLE_OWNER_TRAIN_TERM, null, values);
        };


        SQLiteManager.getInstance().closeDatabase();

        return train.getName();
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


    public static List<TargetTernEntity> getTargetInSentence(String sentence){
        List<TargetTernEntity> result = new ArrayList<>();

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_TARGET_TERM
                + " WHERE instr(?,"+KEY_CONTENT+") > 0";

//        Log.d(TABLE_TARGET_TERM, selectQuery);
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

//        Log.d(TABLE_HUMAN_TERM, selectQuery);
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
    public List<OwnerTrainEntity> getOwnerTrain(){
        List<OwnerTrainEntity> result = new ArrayList<>();

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_OWNER_TRAIN_TERM;
        Cursor cursor = db.rawQuery(selectQuery,  new String[]{});
        if (cursor.moveToFirst()) {
            do {
                OwnerTrainEntity term = new OwnerTrainEntity();
                term.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                term.setWords(cursor.getString(cursor.getColumnIndex(KEY_WORDS)));
                term.setType(cursor.getString(cursor.getColumnIndex(KEY_TYPE)));

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
