package center.control.system.vash.controlcenter.panel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Person;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import center.control.system.vash.controlcenter.App;
import center.control.system.vash.controlcenter.MainActivity;
import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.area.AreaSQLite;
import center.control.system.vash.controlcenter.database.SQLiteManager;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.DeviceSQLite;
import center.control.system.vash.controlcenter.device.ManageDeviceActivity;
import center.control.system.vash.controlcenter.helper.StorageHelper;
import center.control.system.vash.controlcenter.nlp.DetectFunctionEntity;
import center.control.system.vash.controlcenter.nlp.DetectIntentSQLite;
import center.control.system.vash.controlcenter.nlp.DetectSocialEntity;
import center.control.system.vash.controlcenter.nlp.TargetTernEntity;
import center.control.system.vash.controlcenter.nlp.TermEntity;
import center.control.system.vash.controlcenter.nlp.TermSQLite;
import center.control.system.vash.controlcenter.script.ScriptEntity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
import center.control.system.vash.controlcenter.server.AssistantTypeDTO;
import center.control.system.vash.controlcenter.server.BotDataCentralDTO;
import center.control.system.vash.controlcenter.server.CloudApi;
import center.control.system.vash.controlcenter.server.FunctionIntentDTO;
import center.control.system.vash.controlcenter.server.RetroFitSingleton;
import center.control.system.vash.controlcenter.server.SocialIntentDTO;
import center.control.system.vash.controlcenter.server.TermDTO;
import center.control.system.vash.controlcenter.service.WebServerService;
import center.control.system.vash.controlcenter.utils.BotUtils;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.SmartHouse;
import center.control.system.vash.controlcenter.utils.TFIDF;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingPanel extends AppCompatActivity {
    private static final String TAG = "Setting Panel";

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_panel);
        ImageButton currentTab = (ImageButton) findViewById(R.id.tabBtnSetting);
        currentTab.setImageResource(R.drawable.tab_setting_active);
        currentTab.setBackgroundColor(Color.WHITE);


        final Dialog dialog = new Dialog(SettingPanel.this);
        dialog.setContentView(R.layout.activate_diaglog);

        ImageButton btnActive = (ImageButton) findViewById(R.id.btnSetReset);
        btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button btnUpdatePerson = (Button) dialog.findViewById(R.id.btnUpdatePersons);
                btnUpdatePerson.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String Id = StorageHelper.getPersonGroupId("nguoinha",SettingPanel.this);
                        if (!StorageHelper.getAllPersonIds(Id, SettingPanel.this).isEmpty()) {
                            StorageHelper.clearPersonIds(Id,SettingPanel.this);
                        }
                        new GetPersonIdsTask().execute(Id);
                    }
                });

                Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        ImageButton btnDevice = (ImageButton) findViewById(R.id.btnSetDevice);
        btnDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingPanel.this, ManageDeviceActivity.class));
            }
        });

        ImageButton btnSetLogout = (ImageButton) findViewById(R.id.btnSetLogout);
        btnSetLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(ConstManager.SHARED_PREF_NAME, MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                SmartHouse.getInstance().removeAllAreaAndItsDevice();
                edit.putString(ConstManager.SYSTEM_ID,"");
                edit.commit();
                SQLiteManager.getInstance().clearAllData();
                startActivity(new Intent(SettingPanel.this, MainActivity.class));
            }
        });
    }

    public void clicktoControlPanel(View view) {
        startActivity(new Intent(this, ControlPanel.class));
    }

    public void clicktoModePanel(View view) {
        startActivity(new Intent(this, ModePanel.class));
    }

    public void clicktoSettingPanel(View view) {
        startActivity(new Intent(this, SettingPanel.class));
    }

    public void clicktoVAPanel(View view) {
        startActivity(new Intent(this, VAPanel.class));
    }


    class GetPersonIdsTask extends AsyncTask<String, String, Person[]> {

        String groupid = "";

        @Override
        protected Person[] doInBackground(String... params) {


            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = App.getFaceServiceClient();
            try{

                groupid = params[0];
                Log.d(TAG,groupid);
                return faceServiceClient.listPersons(params[0]);

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(Person[] result) {
            String message = "";
            Log.d(TAG,result.length+"");
            if (result != null) {
                for (Person person : result) {
                    try {
                        String name = URLDecoder.decode(person.name, "UTF-8");
                        StorageHelper.setPersonName(person.personId.toString(), name, groupid, SettingPanel.this);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
