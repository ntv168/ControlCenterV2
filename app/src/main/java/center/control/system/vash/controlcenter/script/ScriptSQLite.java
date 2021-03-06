package center.control.system.vash.controlcenter.script;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.command.CommandEntity;
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
    private static final String KEY_NICKNAME = "nick_name";
    private static final String KEY_HOUR = "scheduler_hour";
    private static final String KEY_MIN = "scheduler_min";
    private static final String KEY_WEEKSDAY = "week_days";

    public static final String KEY_GROUP_ID = "group_id";
    public static final String KEY_CONFIG_ID = "configuration_id";
    public static final String KEY_DEVICE_ID = "device_id";
    public static final String KEY_DEVICE_STATE = "device_state";
    private static final String KEY_STATE_ID = "state_id";

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
                + KEY_CONFIG_ID  + " INTEGER ,"
                + KEY_STATE_ID + " INTEGER , "
                +  KEY_DEVICE_ID+ "  INTEGER  ," +
                KEY_DEVICE_STATE + "  TEXT  ,"+
                "PRIMARY KEY ("+KEY_DEVICE_ID+","+ KEY_GROUP_ID+","+KEY_STATE_ID+")" +
                ")";
    }
    public static void insertScript(ScriptEntity script,List<CommandEntity> command) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();

        // Inserting Row
        int scriptId  = (int) db.insert(TABLE_SCRIPT, null, scriptToCV(script));
        script.setId(scriptId);
        for (CommandEntity device : command){
            db.insert(TABLE_SCRIPT_DEVICE, null, scriptDeviceToCV(
                    new CommandEntity(device.getDeviceId(),device.getDeviceState(),scriptId)));
        }
        SQLiteManager.getInstance().closeDatabase();
    }
    public static void insertStateCommand(int stateId, List<CommandEntity> command) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        for (CommandEntity device : command){
            db.insert(TABLE_SCRIPT_DEVICE, null, scriptDeviceToCV(
                    new CommandEntity(stateId,device.getDeviceId(),device.getDeviceState())));
        }
        SQLiteManager.getInstance().closeDatabase();
    }
    private static ContentValues scriptDeviceToCV(CommandEntity sde){
        ContentValues values;
        values = new ContentValues();
        values.put(KEY_DEVICE_ID, sde.getDeviceId() );
        values.put(KEY_GROUP_ID, sde.getGroupId());
        values.put(KEY_STATE_ID, sde.getStateId());
        values.put(KEY_CONFIG_ID, sde.getConfigurationId());
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

        Log.d("SQLItee",script.getName()+"  "+script.getWeekDay());
        return script;
    }
    public static ScriptEntity findById(int id){
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
    public static List<CommandEntity> getCommandByScriptId(int scriptId){
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_SCRIPT_DEVICE
                + " WHERE "+KEY_GROUP_ID+" = ? ";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{String.valueOf(scriptId)});
        // looping through all rows and adding to list
        List<CommandEntity> commands = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                CommandEntity cmd = new CommandEntity();
                cmd.setDeviceId(cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_ID)));
                cmd.setGroupId(cursor.getInt(cursor.getColumnIndex(KEY_GROUP_ID)));
                cmd.setStateId(cursor.getInt(cursor.getColumnIndex(KEY_STATE_ID)));
                cmd.setConfigurationId(cursor.getInt(cursor.getColumnIndex(KEY_CONFIG_ID)));
                cmd.setDeviceState(cursor.getString(cursor.getColumnIndex(KEY_DEVICE_STATE)));
                commands.add(cmd);
            } while (cursor.moveToNext());
        }
        return commands;
    }
    public static List<CommandEntity> getCommandByStateId(int state){
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_SCRIPT_DEVICE
                + " WHERE "+KEY_STATE_ID+" = ? ";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{String.valueOf(state)});
        // looping through all rows and adding to list
        List<CommandEntity> commands = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                CommandEntity cmd = new CommandEntity();
                cmd.setDeviceId(cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_ID)));
                cmd.setGroupId(cursor.getInt(cursor.getColumnIndex(KEY_GROUP_ID)));
                cmd.setStateId(cursor.getInt(cursor.getColumnIndex(KEY_STATE_ID)));
                cmd.setConfigurationId(cursor.getInt(cursor.getColumnIndex(KEY_CONFIG_ID)));
                cmd.setDeviceState(cursor.getString(cursor.getColumnIndex(KEY_DEVICE_STATE)));
                commands.add(cmd);
            } while (cursor.moveToNext());
        }
        return commands;
    }
    public static void upById(int id, ScriptEntity scriptEntity,List<CommandEntity> command) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.update(TABLE_SCRIPT, scriptToCV(scriptEntity), KEY_ID + " = "+id, null);
        db.delete(TABLE_SCRIPT_DEVICE, KEY_GROUP_ID+"="+id, null);
        for (CommandEntity device : command){
            db.insert(TABLE_SCRIPT_DEVICE, null, scriptDeviceToCV(
                    new CommandEntity(device.getDeviceId(),device.getDeviceState(),id)));
        }
        SQLiteManager.getInstance().closeDatabase();
    }
    public static void clearStateCmd( int id) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.delete(TABLE_SCRIPT_DEVICE, KEY_STATE_ID+"="+id,null);
        SQLiteManager.getInstance().closeDatabase();
    }

    public static void upModeOnly(int id, ScriptEntity mode) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.update(TABLE_SCRIPT, scriptToCV(mode), KEY_ID + " = "+id, null);
        SQLiteManager.getInstance().closeDatabase();
    }

    public static void deleteModeById(int modeId) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.delete(TABLE_SCRIPT, KEY_ID+"="+modeId,null);
        db.delete(TABLE_SCRIPT_DEVICE, KEY_GROUP_ID+"="+modeId,null);
        SQLiteManager.getInstance().closeDatabase();
    }
}
