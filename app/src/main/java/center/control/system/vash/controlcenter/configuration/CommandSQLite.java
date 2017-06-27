package center.control.system.vash.controlcenter.configuration;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.database.SQLiteManager;
import center.control.system.vash.controlcenter.script.ScriptSQLite;

/**
 * Created by Sam on 6/27/2017.
 */

public class CommandSQLite {

    public static final String TABLE_COMMAND = "command";
    private static final String TAG = "Command SQLite" ;

    // Contacts Table Columns names
    private static final String KEY_CONFIG_ID = ScriptSQLite.KEY_CONFIG_ID;
    private static final String KEY_DEVICE_ID = ScriptSQLite.KEY_DEVICE_ID;
    private static final String KEY_DEVICE_STATE = ScriptSQLite.KEY_DEVICE_STATE;

    public static String createCommand(){
        return "CREATE TABLE " + TABLE_COMMAND  + "("
                + KEY_CONFIG_ID + "INTEGER"
                + KEY_DEVICE_ID + "INTEGER"
                + KEY_DEVICE_STATE + "TEXT"
                + ")";
    }


    public int insertConfiguration(CommandEntity entity) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CONFIG_ID, entity.getConfigurationId());
        values.put(KEY_DEVICE_ID, entity.getDeviceId());
        values.put(KEY_DEVICE_STATE, entity.getDeviceState());

        // Inserting Row
        int newId  = (int) db.insert(TABLE_COMMAND, null, values);
        SQLiteManager.getInstance().closeDatabase();

        return newId;
    }

    public static List<CommandEntity> getAll(){
        List<CommandEntity> result = new ArrayList<>();

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_COMMAND;
        Cursor cursor = db.rawQuery(selectQuery,  new String[]{});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CommandEntity command = cursorToEnt(cursor);
                Log.d(TAG, command.getDeviceId()+"");
                result.add(command);
            } while (cursor.moveToNext());
        }

        cursor.close();
        SQLiteManager.getInstance().closeDatabase();

        return result;
    }

    static CommandEntity cursorToEnt(Cursor cursor){
        CommandEntity command = new CommandEntity();
        command.setConfigurationId(cursor.getInt(cursor.getColumnIndex(KEY_CONFIG_ID)));
        command.setDeviceId(cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_ID)));
        command.setDeviceState(cursor.getString(cursor.getColumnIndex(KEY_DEVICE_STATE)));


        return command;
    }
}
