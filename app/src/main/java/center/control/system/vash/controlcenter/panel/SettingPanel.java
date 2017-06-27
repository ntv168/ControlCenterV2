package center.control.system.vash.controlcenter.panel;

import android.app.AlertDialog;
import android.app.Dialog;
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
import java.util.List;

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
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.SmartHouse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingPanel extends AppCompatActivity {
    private AlertDialog.Builder botTypeDiag;
    private ArrayAdapter<String> botTypeAdapter;
    private static final String TAG = "Setting Panel";
    private List<AssistantTypeDTO> typeDTOs;
    private AlertDialog.Builder editNickNameDiag;
    private SharedPreferences sharedPreferences;
    private int botTypeId;
    private String botType;
    private String botName;
    private String ownerName;
    private ArrayAdapter<String> botRoleAdapter;
    private EditText editOwnerName;
    private ArrayAdapter<String> ownerRoleAdapter;

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

        sharedPreferences = getSharedPreferences(ConstManager.SHARED_PREF_NAME,MODE_PRIVATE);
        botType = sharedPreferences.getString(ConstManager.BOT_TYPE,"");
        if (botType.length() < 2){
            Log.d(TAG,botType + "  loai");
            Toast.makeText(this,"Chưa chọn loại quản gia",Toast.LENGTH_SHORT);
        }
        botName = sharedPreferences.getString(ConstManager.BOT_NAME,"");
        if (botName.equals("")){
            Toast.makeText(this,"Chưa Tên quản gia",Toast.LENGTH_SHORT);
        }
        botTypeId = sharedPreferences.getInt(ConstManager.BOT_TYPE_ID,-1);
        ownerName = sharedPreferences.getString(ConstManager.OWNER_NAME,"");

        final Dialog dialog = new Dialog(SettingPanel.this);
        dialog.setContentView(R.layout.activate_diaglog);

        final CloudApi botApi = RetroFitSingleton.getInstance().getCloudApi();
        ImageButton btnActive = (ImageButton) findViewById(R.id.btnSetReset);
        btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOwnerName = (EditText) dialog.findViewById(R.id.txtOwnerName);
                editOwnerName.setText(ownerName);
                final Spinner spinOwnerRole = (Spinner) dialog.findViewById(R.id.spn_owner_role);
                final Spinner spinBotRole = (Spinner) dialog.findViewById(R.id.spn_bot_role);
                ((TextView)dialog.findViewById(R.id.txtBotName)).setText(botName);
                ((TextView)dialog.findViewById(R.id.txtBotType)).setText(botType);
                editNickNameDiag = new AlertDialog.Builder(SettingPanel.this);

                if (botType.equals(ConstManager.BOT_TYPE_QUAN_GIA_GIA)){
                    String[] botRole = ConstManager.QUAN_GIA_GIA_BOT_ROLE_ARR;
                    botRoleAdapter = new ArrayAdapter<String>(SettingPanel.this, android.R.layout.simple_spinner_dropdown_item, botRole);
                    spinBotRole.setAdapter(botRoleAdapter);
                    String[] ownerRole = ConstManager.QUAN_GIA_GIA_OWNER_ROLE_ARR;
                    ownerRoleAdapter = new ArrayAdapter<String>(SettingPanel.this, android.R.layout.simple_spinner_dropdown_item, ownerRole);
                    spinOwnerRole.setAdapter(ownerRoleAdapter);
                } else {
                    Log.d(TAG,"bot tye chua ho tro : "+ botType);
                    //truong hop bot type khac
                }
                spinOwnerRole.setSelection(ownerRoleAdapter.getPosition("ông"));
                spinBotRole.setSelection(ownerRoleAdapter.getPosition("tôi"));

                Button btnLoadBot = (Button) dialog.findViewById(R.id.btnLoadBot);
                btnLoadBot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String botRole = botRoleAdapter.getItem(spinBotRole.getSelectedItemPosition());
                        ownerName = editOwnerName.getText().toString();
                        String ownerRole = ownerRoleAdapter.getItem(spinOwnerRole.getSelectedItemPosition());
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString(ConstManager.BOT_ROLE,botRole);
                        edit.putString(ConstManager.BOT_NAME,botName);
                        edit.putString(ConstManager.OWNER_ROLE,ownerRole);
                        edit.putString(ConstManager.BOT_TYPE,botType);
                        edit.putString(ConstManager.OWNER_NAME,ownerName);
                        edit.commit();
                        botApi.getDataVA(botTypeId).enqueue(new Callback<BotDataCentralDTO>() {
                            @Override
                            public void onResponse(Call<BotDataCentralDTO> call, Response<BotDataCentralDTO> response) {
                                Log.d(TAG,call.request().url()+"");
                                TermSQLite sqLite = new TermSQLite();
                                DetectIntentSQLite sqlDect = new DetectIntentSQLite();
                                sqLite.clearAll();
                                sqlDect.clearAll();
                                Log.d(TAG,"term ;"+response.body().getTermDTOS().size());
                                Log.d(TAG,"funct ;"+response.body().getFunctions().size());
                                Log.d(TAG,"soc ;"+response.body().getSocials().size());
                                for (TermDTO term : response.body().getTermDTOS()){
                                    sqLite.insertHumanTerm(new TermEntity(term.getVal()
                                    ,term.getTfidf(),term.getSocialId(),term.getFunctId()));
                                }
                                for (SocialIntentDTO soc : response.body().getSocials()){
                                    sqlDect.insertSocial(new DetectSocialEntity(soc.getId(),
                                            soc.getName(),soc.getQuestion(),soc.getReply()));
                                }
                                for (FunctionIntentDTO funct : response.body().getFunctions()){
                                    sqlDect.insertFunction(new DetectFunctionEntity(funct.getId(),
                                            funct.getName(),funct.getSuccess(),funct.getFail(),funct.getRemind()));
                                }
                                final SmartHouse house = SmartHouse.getInstance();
                                for (final DeviceEntity device: house.getDevices()){
                                    if (device.getNickName().length()<2){
                                        editNickNameDiag.setTitle("Tên gọi khác cho thiết bị"+device.getName());
                                        final EditText input = new EditText(SettingPanel.this);
                                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                                        editNickNameDiag.setView(input);
                                        editNickNameDiag.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                device.setNickName(input.getText().toString());
                                                DeviceSQLite.upById(device.getId(),device);
                                                house.updateDeviceById(device.getId(),device);
                                                dialog.dismiss();
                                            }
                                        });
                                        editNickNameDiag.show();
                                    }
                                }
                                for (final AreaEntity area: house.getAreas()){
                                    if (area.getNickName().length()<2){
                                        editNickNameDiag.setTitle("Tên gọi khác cho không gian "+area.getName());
                                        final EditText input = new EditText(SettingPanel.this);
                                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                                        editNickNameDiag.setView(input);
                                        editNickNameDiag.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                area.setNickName(input.getText().toString());
                                                AreaSQLite.upAddressAndNickById(area.getId(),area);
                                                house.updateAreaById(area.getId(),area);
                                                dialog.dismiss();
                                            }
                                        });
                                        editNickNameDiag.show();
                                    }
                                }
                                for (final ScriptEntity mode: house.getScripts()){
                                    if (mode.getNickName().length()<2){
                                        editNickNameDiag.setTitle("Tên gọi khác cho chế độ "+mode.getName());
                                        final EditText input = new EditText(SettingPanel.this);
                                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                                        editNickNameDiag.setView(input);
                                        editNickNameDiag.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mode.setNickName(input.getText().toString());
                                                ScriptSQLite.upModeOnly(mode.getId(),mode);
                                                house.updateModeById(mode.getId(),mode);
                                                dialog.dismiss();
                                            }
                                        });
                                        editNickNameDiag.show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<BotDataCentralDTO> call, Throwable t) {
                                Log.d(TAG,"down load bot data failed");
                            }
                        });
                    }
                });
                if (botTypeId == -1){
                    btnLoadBot.setEnabled(false);
                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getApplicationContext(),WebServerService.class));
        Log.d(TAG," destroy ");
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
