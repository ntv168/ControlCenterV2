package center.control.system.vash.controlcenter.configuration;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.command.CommandEntity;
import center.control.system.vash.controlcenter.database.SQLiteManager;
import center.control.system.vash.controlcenter.nlp.OwnerTrainEntity;
import center.control.system.vash.controlcenter.nlp.TargetTernEntity;
import center.control.system.vash.controlcenter.trigger.TriggerEntity;

/**
 * Created by Thuans on 7/2/2017.
 */

public class StateConfigurationSQL {


    public static final String TABLE_STATE = "state_tbl";
    public static final String TABLE_EVENT = "event_tbl";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DELAY = "delay";
    private static final String KEY_NAME = "name";
    private static final String KEY_NOTIFY = "notify";
    private static final String KEY_NEXT_EVENT = "next_events";
    private static final String KEY_PRIORITY = "priority";
    private static final String KEY_DURING = "during";
    private static final String KEY_SEN_NAME = "sensor_name";
    private static final String KEY_SEN_VAL = "sensor_value";
    private static final String KEY_AREA_ID = "area_id";
    private static final String KEY_NEXT_STATE = "state_id";
    private static final String TAG = "state SQLite";

    public static String createState(){
        return "CREATE TABLE " + TABLE_STATE  + "("
                + KEY_ID  + " INTEGER PRIMARY KEY ,"
                + KEY_DELAY + "  INTEGER    , "
                + KEY_NAME + " TEXT , "
                + KEY_NEXT_EVENT + " TEXT , "
                + KEY_DURING + " INTEGER , "
                + KEY_NOTIFY + " TEXT  ) ";
    }

    public static String createEvent(){
        return "CREATE TABLE " + TABLE_EVENT+ "("
                + KEY_ID  + " INTEGER PRIMARY KEY  ,"
                + KEY_SEN_NAME + "  TEXT    , "
                + KEY_SEN_VAL + " TEXT , "
                + KEY_PRIORITY + " INTEGER , "
                + KEY_AREA_ID + " INTEGER , "
                + KEY_NEXT_STATE + " INTEGER "+ ")";
    }
    public static void  insertEvent(EventEntity event) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_ID, event.getId());
            values.put(KEY_AREA_ID, event.getAreaId());
            values.put(KEY_SEN_NAME, event.getSenName());
            values.put(KEY_PRIORITY, event.getPriority());
            values.put(KEY_SEN_VAL, event.getSenValue());
            values.put(KEY_NEXT_STATE, event.getNextStateId());
            db.insert(TABLE_EVENT, null, values);

        SQLiteManager.getInstance().closeDatabase();
    }

    public static int insertState(StateEntity state) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, state.getId());
        values.put(KEY_NAME, state.getName());
        values.put(KEY_DURING, state.getDuringSec());
        values.put(KEY_NEXT_EVENT, state.getNextEvIds());
        values.put(KEY_DELAY, state.getDelaySec());
        values.put(KEY_NOTIFY, state.getNoticePattern());

        int id = (int)db.insert(TABLE_STATE, null, values);
        SQLiteManager.getInstance().closeDatabase();
        return id;
    }

    public static void removeAll() {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.delete(TABLE_EVENT,null,null);
        db.delete(TABLE_STATE,null,null);
        SQLiteManager.getInstance().closeDatabase();
    }
    public static EventEntity findEventById(int id){

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_EVENT
                + " WHERE "+KEY_ID+" = ? ";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{String.valueOf(id)});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            EventEntity event= cursorToEvent(cursor);
            cursor.close();
            SQLiteManager.getInstance().closeDatabase();
            return event;
        } else return null;

    }

    private static EventEntity cursorToEvent(Cursor cursor) {
        EventEntity event= new EventEntity();
        event.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        event.setSenName(cursor.getString(cursor.getColumnIndex(KEY_SEN_NAME)));
        event.setSenValue(cursor.getString(cursor.getColumnIndex(KEY_SEN_VAL)));
        event.setNextStateId(cursor.getInt(cursor.getColumnIndex(KEY_NEXT_STATE)));
        event.setPriority(cursor.getInt(cursor.getColumnIndex(KEY_PRIORITY)));
        event.setAreaId(cursor.getInt(cursor.getColumnIndex(KEY_AREA_ID)));
        Log.d(TAG,event.getAreaId()+"  s");

        return event;
    }

    public static List<StateEntity> getAll() {
        List<StateEntity> result = new ArrayList<>();

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_STATE;
        Cursor cursor = db.rawQuery(selectQuery,  new String[]{});
        if (cursor.moveToFirst()) {
            do {
                StateEntity state = cursorToEnt(cursor);
                result.add(state);
            } while (cursor.moveToNext());
        }

        cursor.close();
        SQLiteManager.getInstance().closeDatabase();

        return result;
    }
    static StateEntity cursorToEnt(Cursor cursor){
        StateEntity state= new StateEntity();
        state.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        state.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        state.setDelaySec(cursor.getInt(cursor.getColumnIndex(KEY_DELAY)));
        state.setDuringSec(cursor.getInt(cursor.getColumnIndex(KEY_DURING)));
        state.setNextEvIds(cursor.getString(cursor.getColumnIndex(KEY_NEXT_EVENT)));
        state.setNoticePattern(cursor.getString(cursor.getColumnIndex(KEY_NOTIFY)));
        return state;
    }

    public static void updateEventById(int id, EventEntity event) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_AREA_ID,event.getAreaId());
        Log.d(TAG,event.getAreaId()+"  ida " + id);
        db.update(TABLE_EVENT, cv, KEY_ID + " = "+id, null);
        SQLiteManager.getInstance().closeDatabase();
    }
}
