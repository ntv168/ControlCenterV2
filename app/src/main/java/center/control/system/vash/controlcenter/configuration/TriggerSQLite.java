package center.control.system.vash.controlcenter.configuration;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.database.SQLiteManager;
import center.control.system.vash.controlcenter.script.ScriptSQLite;

/**
 * Created by Sam on 6/27/2017.
 */

public class TriggerSQLite {
    public static final String TABLE_TRIGGER = "trigger_condition";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    private static final String KEY_CONFIG_ID = ScriptSQLite.KEY_CONFIG_ID;
    private static final String KEY_DEVICE_ID = ScriptSQLite.KEY_DEVICE_ID;
    private static final String KEY_DEVICE_STATE = ScriptSQLite.KEY_DEVICE_STATE;
    private static final String TAG = "Trigger SQLite";


    public static String createTriggerConfigution(){
        return "CREATE TABLE " + TABLE_TRIGGER  + "("
                + KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT    ,"
                + KEY_NAME+ "  TEXT "
                + KEY_CONFIG_ID + "INTEGER"
                + KEY_DEVICE_ID + "INTEGER"
                + KEY_DEVICE_STATE + "TEXT"
                + ")";
    }

    public int insertTrigger(TriggerEntity entity) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, entity.getId());
        values.put(KEY_NAME, entity.getName());
        values.put(KEY_CONFIG_ID, entity.getConfigurationId());
        values.put(KEY_DEVICE_ID, entity.getDeviceId());
        values.put(KEY_DEVICE_STATE, entity.getDeviceState());

        // Inserting Row
        int newId  = (int) db.insert(TABLE_TRIGGER, null, values);
        SQLiteManager.getInstance().closeDatabase();

        return newId;
    }

    public static List<TriggerEntity> getAll(){
        List<TriggerEntity> result = new ArrayList<>();

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_TRIGGER;
        Cursor cursor = db.rawQuery(selectQuery,  new String[]{});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TriggerEntity configuration = cursorToEnt(cursor);
                Log.d(TAG, configuration.getId()+"");
                result.add(configuration);
            } while (cursor.moveToNext());
        }

        cursor.close();
        SQLiteManager.getInstance().closeDatabase();

        return result;
    }

    public TriggerEntity findById(int id){

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_TRIGGER
                + " WHERE "+KEY_ID+" = ? ";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{String.valueOf(id)});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {

            TriggerEntity trigger = new TriggerEntity();
            trigger.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            trigger.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            trigger.setConfigurationId(cursor.getInt(cursor.getColumnIndex(KEY_CONFIG_ID)));
            trigger.setDeviceId(cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_ID)));
            trigger.setDeviceState(cursor.getString(cursor.getColumnIndex(KEY_DEVICE_STATE)));
            cursor.close();
            SQLiteManager.getInstance().closeDatabase();

            return trigger;
        } else return null;

    }

    static TriggerEntity cursorToEnt(Cursor cursor){
        TriggerEntity trigger = new TriggerEntity();
        trigger.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        trigger.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        trigger.setConfigurationId(cursor.getInt(cursor.getColumnIndex(KEY_CONFIG_ID)));
        trigger.setDeviceId(cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_ID)));
        trigger.setDeviceState(cursor.getString(cursor.getColumnIndex(KEY_DEVICE_STATE)));

        return trigger;
    }
}
