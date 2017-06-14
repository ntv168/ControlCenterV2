package center.control.system.vash.controlcenter.script;

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

public class ScriptSQLite {

    public static final String TABLE_SCRIPT = "script";
    public static final String TABLE_SCRIPT_DEVICE = "script_device";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_NICKNAME = "nickName";

    private static final String KEY_GROUP_ID = "group_id";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_DEVICE_STATE = "device_state";

    public static String createScriptTable(){
        return "CREATE TABLE " + TABLE_SCRIPT  + "("
                + KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT    ,"
                +  KEY_NAME+ "  TEXT ," +
                KEY_NICKNAME + "  TEXT  "+ ")";
    }
    public static String createScriptDeviceTable(){
        return "CREATE TABLE " + TABLE_SCRIPT_DEVICE  + "("
                + KEY_GROUP_ID  + " INTEGER ,"
                +  KEY_DEVICE_ID+ "  INTEGER  ," +
                KEY_DEVICE_STATE + "  TEXT  ,"+
                "PRIMARY KEY ("+KEY_DEVICE_ID+","+ KEY_GROUP_ID+")" +
                ")";
    }
    public static void insertScript(ScriptEntity script,List<ScriptDeviceEntity> command) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, script.getName());
        values.put(KEY_NICKNAME, script.getNickName());
        // Inserting Row
        int scriptId  = (int) db.insert(TABLE_SCRIPT, null, values);
        for (ScriptDeviceEntity device : command){
            values = new ContentValues();
            values.put(KEY_DEVICE_ID, device.getDeviceId());
            values.put(KEY_GROUP_ID, scriptId);
            values.put(KEY_DEVICE_STATE, device.getDeviceState());
            db.insert(TABLE_SCRIPT_DEVICE, null, values);
        }
        SQLiteManager.getInstance().closeDatabase();
    }
    public static List<ScriptEntity> getAll(){
        List<ScriptEntity> result = new ArrayList<>();

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_SCRIPT
                ;

        Log.d(TABLE_SCRIPT, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery,  new String[]{});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ScriptEntity script = new ScriptEntity();
                script.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                script.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                script.setNickName(cursor.getString(cursor.getColumnIndex(KEY_NICKNAME)));

                result.add(script);
            } while (cursor.moveToNext());
        }

        cursor.close();
        SQLiteManager.getInstance().closeDatabase();

        return result;
    }
    public ScriptEntity findById(String id){

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_SCRIPT
                + " WHERE "+KEY_ID+" = ? ";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{String.valueOf(id)});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            ScriptEntity script = new ScriptEntity();
            script.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            script.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            script.setNickName(cursor.getString(cursor.getColumnIndex(KEY_NICKNAME)));
            cursor.close();
            SQLiteManager.getInstance().closeDatabase();

            return script;
        } else return null;

    }

    public void delete( ) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.delete(TABLE_SCRIPT,null,null);
        SQLiteManager.getInstance().closeDatabase();
    }
}
