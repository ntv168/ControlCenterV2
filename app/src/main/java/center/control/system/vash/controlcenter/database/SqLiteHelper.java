package center.control.system.vash.controlcenter.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import center.control.system.vash.controlcenter.App;
import center.control.system.vash.controlcenter.area.AreaSQLite;
import center.control.system.vash.controlcenter.device.DeviceSQLite;
import center.control.system.vash.controlcenter.nlp.DetectIntentSQLite;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
import center.control.system.vash.controlcenter.nlp.TermSQLite;

/**
 * Created by Thuans on 4/27/2017.
 */

public class SqLiteHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 6;

    private static final String DATABASE_NAME = "ControlCenter";

    private static final String TAG = "SQLite Helper utils";

    public SqLiteHelper( ) {
        super(App.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"bat dau tao table");
                //All necessary tables you like to create will create here
        db.execSQL(DeviceSQLite.createTable());
        db.execSQL(AreaSQLite.createTable());
        db.execSQL(ScriptSQLite.createScriptDeviceTable());
        db.execSQL(ScriptSQLite.createScriptTable());
        db.execSQL(TermSQLite.createHumanTerm());
        db.execSQL(TermSQLite.createTargetTerm());
        db.execSQL(DetectIntentSQLite.createFunction());
        db.execSQL(DetectIntentSQLite.createSocial());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("SQLiteDatabase.onUpgrade(%d -> %d)", oldVersion, newVersion));

        // Drop table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + DeviceSQLite.TABLE_DEVICE);
        db.execSQL("DROP TABLE IF EXISTS " + AreaSQLite.TABLE_AREA);
        db.execSQL("DROP TABLE IF EXISTS " + ScriptSQLite.TABLE_SCRIPT);
        db.execSQL("DROP TABLE IF EXISTS " + ScriptSQLite.TABLE_SCRIPT_DEVICE);
        db.execSQL("DROP TABLE IF EXISTS " + TermSQLite.TABLE_HUMAN_TERM);
        db.execSQL("DROP TABLE IF EXISTS " + TermSQLite.TABLE_TARGET_TERM);
        db.execSQL("DROP TABLE IF EXISTS " + DetectIntentSQLite.TABLE_FUNCTION_DETECT);
        db.execSQL("DROP TABLE IF EXISTS " + DetectIntentSQLite.TABLE_SOCIAL_DETECT);
        onCreate(db);
    }

}
