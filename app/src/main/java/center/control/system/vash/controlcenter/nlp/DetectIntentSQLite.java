package center.control.system.vash.controlcenter.nlp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import center.control.system.vash.controlcenter.database.SQLiteManager;

/**
 * Created by Thuans on 5/29/2017.
 */

public class DetectIntentSQLite {

    public static final String TABLE_SOCIAL_DETECT = "detect_social";
    public static final String TABLE_FUNCTION_DETECT = "detect_function";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FUNCTION_NAME = "functionName";
    private static final String KEY_NAME = "name";
    private static final String KEY_REPLY_PATTERN = "replyPattern";
    private static final String KEY_QUESTION_PATTERN = "questionPattern";
    private static final String KEY_FAIL_PATTERN = "failPattern";
    private static final String KEY_SUCCESS_PATTERN = "successPattern";
    private static final String KEY_REMIND_PATTERN = "remindPattern";


    public static String createFunction(){
        return "CREATE TABLE " + TABLE_FUNCTION_DETECT + "("
                + KEY_ID  + " INTEGER PRIMARY KEY ,"
                + KEY_FUNCTION_NAME  + "  TEXT    , "
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
        values.put(KEY_FUNCTION_NAME, detect.getFunctionName());
        values.put(KEY_FAIL_PATTERN, detect.getFailPattern());
        values.put(KEY_SUCCESS_PATTERN, detect.getSuccessPattern());
        values.put(KEY_REMIND_PATTERN, detect.getRemindPattern());

        // Inserting Row
        int newId=(int)db.insert(TABLE_FUNCTION_DETECT, null, values);
        SQLiteManager.getInstance().closeDatabase();

        return newId;
    }



    public void delete( ) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.delete(TABLE_FUNCTION_DETECT,null,null);
        db.delete(TABLE_SOCIAL_DETECT,null,null);
        SQLiteManager.getInstance().closeDatabase();
    }
}
