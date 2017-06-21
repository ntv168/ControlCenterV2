package center.control.system.vash.controlcenter.script;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.database.SQLiteManager;
import center.control.system.vash.controlcenter.device.DeviceEntity;

/**
 * Created by Thuans on 5/29/2017.
 */

public class ScriptSQLite {

    public static final String TABLE_SCRIPT = "script";
    public static final String TABLE_SCRIPT_DEVICE = "script_device";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_NICKNAME = "nick_name";
    private static final String KEY_HOUR = "scheduler_hour";
    private static final String KEY_MIN = "scheduler_min";
    private static final String KEY_WEEKSDAY = "week_days";

    private static final String KEY_GROUP_ID = "group_id";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_DEVICE_STATE = "device_state";

    public static String createScriptTable(){
        return "CREATE TABLE " + TABLE_SCRIPT  + "("
                + KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT    ,"
                +  KEY_NAME+ "  TEXT ,"
                +  KEY_HOUR+ "  INTEGER ,"
                +  KEY_MIN+ "  INTEGER,"
                +  KEY_WEEKSDAY+ "  INTEGER," +
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

        // Inserting Row
        int scriptId  = (int) db.insert(TABLE_SCRIPT, null, scriptToCV(script));
        script.setId(scriptId);
        for (ScriptDeviceEntity device : command){
            db.insert(TABLE_SCRIPT_DEVICE, null, scriptDeviceToCV(
                    new ScriptDeviceEntity(device.getDeviceId(),device.getDeviceState(),scriptId)));
        }
        SQLiteManager.getInstance().closeDatabase();
    }
    private static ContentValues scriptDeviceToCV(ScriptDeviceEntity sde){
        ContentValues values;
        values = new ContentValues();
        values.put(KEY_DEVICE_ID, sde.getDeviceId() );
        values.put(KEY_GROUP_ID, sde.getGroupId());
        values.put(KEY_DEVICE_STATE, sde.getDeviceState());
        return values;
    }
    private static ContentValues scriptToCV(ScriptEntity script){
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, script.getName());
        values.put(KEY_NICKNAME, script.getNickName());
        values.put(KEY_HOUR, script.getHour());
        values.put(KEY_MIN, script.getMinute());
        values.put(KEY_WEEKSDAY, script.getWeekDay());
        return values;
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
                result.add(cursorToScript(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        SQLiteManager.getInstance().closeDatabase();

        return result;
    }
    private static ScriptEntity cursorToScript(Cursor cursor){
        ScriptEntity script = new ScriptEntity();
        script.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        script.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        script.setNickName(cursor.getString(cursor.getColumnIndex(KEY_NICKNAME)));
        script.setHour(cursor.getInt(cursor.getColumnIndex(KEY_HOUR)));
        script.setMinute(cursor.getInt(cursor.getColumnIndex(KEY_MIN)));
        script.setWeeksDay(cursor.getString(cursor.getColumnIndex(KEY_WEEKSDAY)));
        return script;
    }
    public ScriptEntity findById(String id){
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_SCRIPT
                + " WHERE "+KEY_ID+" = ? ";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{String.valueOf(id)});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            ScriptEntity script = cursorToScript(cursor);
            cursor.close();
            SQLiteManager.getInstance().closeDatabase();

            return script;
        } else return null;
    }
    public List<ScriptDeviceEntity> getCommandByScriptId(int scriptId){
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_SCRIPT_DEVICE
                + " WHERE "+KEY_GROUP_ID+" = ? ";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{String.valueOf(scriptId)});
        // looping through all rows and adding to list
        List<ScriptDeviceEntity> commands = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                ScriptDeviceEntity cmd = new ScriptDeviceEntity();
                cmd.setDeviceId(cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_ID)));
                cmd.setGroupId(cursor.getInt(cursor.getColumnIndex(KEY_GROUP_ID)));
                cmd.setDeviceState(cursor.getString(cursor.getColumnIndex(KEY_DEVICE_STATE)));
                commands.add(cmd);
            } while (cursor.moveToNext());
        }
        return commands;
    }
    public static void upById(int id, ScriptEntity scriptEntity,List<ScriptDeviceEntity> command) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.update(TABLE_SCRIPT, scriptToCV(scriptEntity), KEY_ID + " = "+id, null);
        db.delete(TABLE_SCRIPT_DEVICE, KEY_GROUP_ID+"="+id, null);
        for (ScriptDeviceEntity device : command){
            db.insert(TABLE_SCRIPT_DEVICE, null, scriptDeviceToCV(
                    new ScriptDeviceEntity(device.getDeviceId(),device.getDeviceState(),id)));
        }
        SQLiteManager.getInstance().closeDatabase();
    }
    public void delete( ) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.delete(TABLE_SCRIPT,null,null);
        SQLiteManager.getInstance().closeDatabase();
    }
}
