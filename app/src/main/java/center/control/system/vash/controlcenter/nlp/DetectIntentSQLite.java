package center.control.system.vash.controlcenter.nlp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.database.SQLiteManager;
import center.control.system.vash.controlcenter.device.DeviceEntity;

/**
 * Created by Thuans on 5/29/2017.
 */

public class DetectIntentSQLite {

    public static final String TABLE_SOCIAL_DETECT = "detect_social";
    public static final String TABLE_FUNCTION_DETECT = "detect_function";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_REPLY_PATTERN = "replyPattern";
    private static final String KEY_QUESTION_PATTERN = "questionPattern";
    private static final String KEY_FAIL_PATTERN = "failPattern";
    private static final String KEY_SUCCESS_PATTERN = "successPattern";
    private static final String KEY_REMIND_PATTERN = "remindPattern";


    public static String createFunction(){
        return "CREATE TABLE " + TABLE_FUNCTION_DETECT + "("
                + KEY_ID  + " INTEGER PRIMARY KEY ,"
                + KEY_NAME  + "  TEXT    , "
                + KEY_FAIL_PATTERN + " TEXT , "
                + KEY_SUCCESS_PATTERN + " TEXT , "
                + KEY_REMIND_PATTERN + " TEXT "+ ")";
    }

    public static String createSocial(){
        return "CREATE TABLE " + TABLE_SOCIAL_DETECT + "("
                + KEY_ID  + " INTEGER PRIMARY KEY ,"
                + KEY_NAME  + "  TEXT    , "
                + KEY_QUESTION_PATTERN + " TEXT , "
                + KEY_REPLY_PATTERN + " TEXT "+ ")";
    }

    public int insertSocial(DetectSocialEntity detect) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, detect.getId());
        values.put(KEY_NAME, detect.getName());
        values.put(KEY_QUESTION_PATTERN, detect.getQuestionPattern());
        values.put(KEY_REPLY_PATTERN, detect.getReplyPattern());

        // Inserting Row
        int newId=(int)db.insert(TABLE_SOCIAL_DETECT, null, values);
        SQLiteManager.getInstance().closeDatabase();

        return newId;
    }
    public int insertFunction(DetectFunctionEntity detect) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, detect.getId());
        values.put(KEY_NAME, detect.getFunctionName());
        values.put(KEY_FAIL_PATTERN, detect.getFailPattern());
        values.put(KEY_SUCCESS_PATTERN, detect.getSuccessPattern());
        values.put(KEY_REMIND_PATTERN, detect.getRemindPattern());

        // Inserting Row
        int newId=(int)db.insert(TABLE_FUNCTION_DETECT, null, values);
        SQLiteManager.getInstance().closeDatabase();

        return newId;
    }

    public static List<DetectFunctionEntity> getAllFunction(){
        List<DetectFunctionEntity> result = new ArrayList<>();

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_FUNCTION_DETECT  ;

//        Log.d(TABLE_DEVICE, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery,  new String[]{});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DetectFunctionEntity funct = new DetectFunctionEntity();
                funct.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                funct.setFailPattern(cursor.getString(cursor.getColumnIndex(KEY_FAIL_PATTERN)));
                funct.setSuccessPattern(cursor.getString(cursor.getColumnIndex(KEY_SUCCESS_PATTERN)));
                funct.setRemindPattern(cursor.getString(cursor.getColumnIndex(KEY_REMIND_PATTERN)));
                funct.setFunctionName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                result.add(funct);
            } while (cursor.moveToNext());
        }

        cursor.close();
        SQLiteManager.getInstance().closeDatabase();
        return result;
    }

    public void clearAll( ) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.delete(TABLE_FUNCTION_DETECT,null,null);
        db.delete(TABLE_SOCIAL_DETECT,null,null);
        SQLiteManager.getInstance().closeDatabase();
    }

    public static DetectFunctionEntity findFunctionById(int id) {

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_FUNCTION_DETECT
                + " WHERE "+KEY_ID+" = ? ";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{String.valueOf(id)});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            DetectFunctionEntity funct = new DetectFunctionEntity();
            funct.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            funct.setFailPattern(cursor.getString(cursor.getColumnIndex(KEY_FAIL_PATTERN)));
            funct.setSuccessPattern(cursor.getString(cursor.getColumnIndex(KEY_SUCCESS_PATTERN)));
            funct.setRemindPattern(cursor.getString(cursor.getColumnIndex(KEY_REMIND_PATTERN)));
            funct.setFunctionName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            cursor.close();
            SQLiteManager.getInstance().closeDatabase();

            return funct;
        } else return null;

    }

    public static DetectFunctionEntity findFunctionByName(String name) {

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_FUNCTION_DETECT
                + " WHERE "+KEY_NAME+" = ? ";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{name});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            DetectFunctionEntity funct = new DetectFunctionEntity();
            funct.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            funct.setFailPattern(cursor.getString(cursor.getColumnIndex(KEY_FAIL_PATTERN)));
            funct.setSuccessPattern(cursor.getString(cursor.getColumnIndex(KEY_SUCCESS_PATTERN)));
            funct.setRemindPattern(cursor.getString(cursor.getColumnIndex(KEY_REMIND_PATTERN)));
            funct.setFunctionName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            cursor.close();
            SQLiteManager.getInstance().closeDatabase();

            return funct;
        } else return null;

    }
    public static DetectSocialEntity findSocialById(int id) {

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_SOCIAL_DETECT
                + " WHERE "+KEY_ID+" = ? ";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{String.valueOf(id)});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            DetectSocialEntity soc = new DetectSocialEntity();
            soc.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            soc.setReplyPattern(cursor.getString(cursor.getColumnIndex(KEY_REPLY_PATTERN)));
            soc.setQuestionPattern(cursor.getString(cursor.getColumnIndex(KEY_QUESTION_PATTERN)));
            soc.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            cursor.close();
            SQLiteManager.getInstance().closeDatabase();

            return soc;
        } else return null;

    }

    public static DetectSocialEntity findSocialByName(String name) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_SOCIAL_DETECT
                + " WHERE "+KEY_NAME+" = ? ";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{name});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            DetectSocialEntity soc = new DetectSocialEntity();
            soc.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            soc.setReplyPattern(cursor.getString(cursor.getColumnIndex(KEY_REPLY_PATTERN)));
            soc.setQuestionPattern(cursor.getString(cursor.getColumnIndex(KEY_QUESTION_PATTERN)));
            soc.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            cursor.close();
            SQLiteManager.getInstance().closeDatabase();

            return soc;
        } else return null;
    }
}
