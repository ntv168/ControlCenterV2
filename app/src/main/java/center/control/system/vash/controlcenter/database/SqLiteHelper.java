package center.control.system.vash.controlcenter.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import center.control.system.vash.controlcenter.App;
import center.control.system.vash.controlcenter.area.AreaSQLite;
import center.control.system.vash.controlcenter.command.CommandSQLite;
import center.control.system.vash.controlcenter.configuration.ConfigurationSQLite;
import center.control.system.vash.controlcenter.configuration.StateConfigurationSQL;
import center.control.system.vash.controlcenter.device.TriggerDeviceSQLite;
import center.control.system.vash.controlcenter.sensor.SensorSQLite;
import center.control.system.vash.controlcenter.trigger.TriggerSQLite;
import center.control.system.vash.controlcenter.device.DeviceSQLite;
import center.control.system.vash.controlcenter.nlp.DetectIntentSQLite;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
import center.control.system.vash.controlcenter.nlp.TermSQLite;

/**
 * Created by Thuans on 4/27/2017.
 */

public class SqLiteHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 9;

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
        db.execSQL(TermSQLite.createTrainTerm());
        db.execSQL(TermSQLite.createTargetTerm());
        db.execSQL(DetectIntentSQLite.createFunction());
        db.execSQL(DetectIntentSQLite.createSocial());

        //Create configuration
        db.execSQL(ConfigurationSQLite.createConfiguration());
        db.execSQL(CommandSQLite.createCommand());
        db.execSQL(TriggerSQLite.createTriggerConfigution());
        db.execSQL(SensorSQLite.createSensor());
        db.execSQL(TriggerDeviceSQLite.createTable());
        db.execSQL(StateConfigurationSQL.createEvent());
        db.execSQL(StateConfigurationSQL.createState());

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
        db.execSQL("DROP TABLE IF EXISTS " + TermSQLite.TABLE_OWNER_TRAIN_TERM);
        db.execSQL("DROP TABLE IF EXISTS " + DetectIntentSQLite.TABLE_FUNCTION_DETECT);
        db.execSQL("DROP TABLE IF EXISTS " + DetectIntentSQLite.TABLE_SOCIAL_DETECT);
//StateConfig
        db.execSQL("DROP TABLE IF EXISTS " + StateConfigurationSQL.TABLE_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + StateConfigurationSQL.TABLE_STATE);

        //Configuration
        db.execSQL("DROP TABLE IF EXISTS " + ConfigurationSQLite.TABLE_CONFIGURATION);
        db.execSQL("DROP TABLE IF EXISTS " + CommandSQLite.TABLE_COMMAND);
        db.execSQL("DROP TABLE IF EXISTS " + TriggerSQLite.TABLE_TRIGGER);
        db.execSQL("DROP TABLE IF EXISTS " + SensorSQLite.TABLE_SENSOR);
        db.execSQL("DROP TABLE IF EXISTS " + TriggerDeviceSQLite.TABLE_DEVICE);

        onCreate(db);
    }

}
