package center.control.system.vash.controlcenter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.area.AreaSQLite;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.DeviceSQLite;
import center.control.system.vash.controlcenter.nlp.DetectFunctionEntity;
import center.control.system.vash.controlcenter.nlp.DetectIntentSQLite;
import center.control.system.vash.controlcenter.nlp.DetectSocialEntity;
import center.control.system.vash.controlcenter.nlp.OwnerTrainEntity;
import center.control.system.vash.controlcenter.nlp.TermSQLite;
import center.control.system.vash.controlcenter.nlp.TrainVAActivity;
import center.control.system.vash.controlcenter.panel.UserSettingPanel;
import center.control.system.vash.controlcenter.script.ScriptEntity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
import center.control.system.vash.controlcenter.server.AssistantTypeDTO;
import center.control.system.vash.controlcenter.server.BotDataCentralDTO;
import center.control.system.vash.controlcenter.server.CloudApi;
import center.control.system.vash.controlcenter.server.FunctionIntentDTO;
import center.control.system.vash.controlcenter.server.LoginSmarthouseDTO;
import center.control.system.vash.controlcenter.server.RetroFitSingleton;
import center.control.system.vash.controlcenter.server.SocialIntentDTO;
import center.control.system.vash.controlcenter.server.VolleySingleton;
import center.control.system.vash.controlcenter.utils.BotUtils;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.MessageUtils;
import center.control.system.vash.controlcenter.utils.SmartHouse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInfoActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String TAG = "Personal Activity";
    private List<AssistantTypeDTO> typeDTOs;
    private AlertDialog.Builder inputDialog;
    private ArrayAdapter<String> botRoleAdapter;
    private EditText editOwnerName;
    private ArrayAdapter<String> ownerRoleAdapter;
    private AlertDialog.Builder botTypeDiag;
    private ArrayAdapter<String> botTypeAdapter;
    private int botTypeId;
    private String botType;
    private String botName;
    private String ownerName;
    private Spinner spinOwnerRole;
    private Spinner spinBotRole;
    private String botRole;
    private String ownerRole;
    TextView txtHouseOwnerName;
    TextView txtHouseOwnerID;
    TextView txtHouseOwnerPhone;
    TextView txtContractType;
    TextView txtHouseAddress;
    TextView txtContractID;
    TextView txtActiveDay;
    LoginSmarthouseDTO passDto;
    private ProgressDialog waitDialog;

    @Override
    protected void onResume() {
        super.onResume();
        botType = sharedPreferences.getString(ConstManager.BOT_TYPE,"");
        botRole = sharedPreferences.getString(ConstManager.BOT_ROLE,"");
        if (botType.length() < 2){
            MessageUtils.makeText(this,"Chưa chọn loại quản gia").show();
        }
        Log.d(TAG,botType + "  loai");
        botName = sharedPreferences.getString(ConstManager.BOT_NAME,"");
        if (botName.length() <2){
            MessageUtils.makeText(this,"Chưa có Tên quản gia").show();
        }
        Log.d(TAG,botName + "  ten");

        botTypeId = sharedPreferences.getInt(ConstManager.BOT_TYPE_ID,-1);
        ownerName = sharedPreferences.getString(ConstManager.OWNER_NAME,"");
        ownerRole = sharedPreferences.getString(ConstManager.OWNER_ROLE,"ông");
        if (botType.equals(ConstManager.BOT_TYPE_QUAN_GIA_GIA)){
            botRoleAdapter = new ArrayAdapter<String>(PersonalInfoActivity.this, android.R.layout.simple_spinner_dropdown_item,
                    ConstManager.QUAN_GIA_GIA_BOT_ROLE_ARR);
            spinBotRole.setAdapter(botRoleAdapter);
            ownerRoleAdapter = new ArrayAdapter<String>(PersonalInfoActivity.this, android.R.layout.simple_spinner_dropdown_item,
                    ConstManager.QUAN_GIA_GIA_OWNER_ROLE_ARR);
            spinOwnerRole.setAdapter(ownerRoleAdapter);
            spinOwnerRole.setSelection(ownerRoleAdapter.getPosition("ông"));
            spinBotRole.setSelection(ownerRoleAdapter.getPosition("tôi"));
        } else {
            Log.d(TAG,"bot tye chua ho tro : "+ botType);
            //truong hop bot type khac
        }

        // Get House Owner and Contract Info
        txtHouseOwnerName.setText(sharedPreferences.getString(ConstManager.OWNER_NAME,""));
        txtHouseOwnerID.setText(sharedPreferences.getString(ConstManager.OWNER_CMND,""));
        txtHouseOwnerPhone.setText(sharedPreferences.getString(ConstManager.OWNER_TEL,""));
//        txtContractType.setText(sharedPreferences.getString(ConstManager.OWNE,""));
        txtHouseAddress.setText(sharedPreferences.getString(ConstManager.OWNER_ADD,""));
        txtContractID.setText(sharedPreferences.getString(ConstManager.CONTRACT_CODE,""));
        txtActiveDay.setText(sharedPreferences.getString(ConstManager.ACTIVE_DAY,""));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal);
        sharedPreferences = getSharedPreferences(ConstManager.SHARED_PREF_NAME,MODE_PRIVATE);

        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle("Vui lòng đợi");
        waitDialog.setIndeterminate(true);
        waitDialog.setCancelable(false);

        LinearLayout lnBack = (LinearLayout) findViewById(R.id.lnBack);
        lnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtHouseOwnerName = (TextView) findViewById(R.id.txtHouseOwnerName);
        txtHouseOwnerID = (TextView) findViewById(R.id.txtHouseOwnerID);
        txtHouseOwnerPhone = (TextView) findViewById(R.id.txtHouseOwnerPhone);
        txtContractType = (TextView) findViewById(R.id.txtContractType);
        txtHouseAddress = (TextView) findViewById(R.id.txtHouseAddress);
        txtContractID = (TextView) findViewById(R.id.txtContractID);
        txtActiveDay = (TextView) findViewById(R.id.txtActiveDay);


        final Dialog vaSetDiag = new Dialog(this);
        vaSetDiag.requestWindowFeature(Window.FEATURE_NO_TITLE);
        vaSetDiag.setContentView(R.layout.va_setting_dialog);

        inputDialog = new AlertDialog.Builder(PersonalInfoActivity.this);
        refineNickNameTarget();

        final ProgressDialog waitDialog = new ProgressDialog(PersonalInfoActivity.this);
        waitDialog.setTitle("Vui lòng đợi");
        waitDialog.setIndeterminate(true);
        waitDialog.setCancelable(false);

        LinearLayout btnSetVA = (LinearLayout) findViewById(R.id.btnSetingVA);
        LinearLayout btnTrain = (LinearLayout) findViewById(R.id.btnTrainVA);

        spinOwnerRole = (Spinner) vaSetDiag.findViewById(R.id.spn_owner_role);
        spinBotRole = (Spinner) vaSetDiag.findViewById(R.id.spn_bot_role);

        final CloudApi botApi = RetroFitSingleton.getInstance().getCloudApi();
        btnSetVA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)vaSetDiag.findViewById(R.id.txtBotName)).setText(botName);
                ((TextView)vaSetDiag.findViewById(R.id.txtBotType)).setText(botType);
                editOwnerName = (EditText) vaSetDiag.findViewById(R.id.txtOwnerName);
                editOwnerName.setText(ownerName);
                vaSetDiag.show();
            }
        });

        btnTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DetectIntentSQLite.getAllFunction().size()>0) {
                    startActivity(new Intent(PersonalInfoActivity.this, TrainVAActivity.class));
                } else {
                    ((TextView)vaSetDiag.findViewById(R.id.txtBotName)).setText(botName);
                    ((TextView)vaSetDiag.findViewById(R.id.txtBotType)).setText(botType);
                    editOwnerName = (EditText) vaSetDiag.findViewById(R.id.txtOwnerName);
                    editOwnerName.setText(ownerName);
                    vaSetDiag.show();
                }
            }
        });

        LinearLayout btnChangePass = (LinearLayout) findViewById(R.id.btnChangePass);
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog passDiag = new Dialog(PersonalInfoActivity.this);
                passDiag.setTitle("Nhập mật khẩu cũ ");
                passDiag.requestWindowFeature(Window.FEATURE_NO_TITLE);
                passDiag.setContentView(R.layout.change_password_dialog);
                final EditText txtOldPass = (EditText) passDiag.findViewById(R.id.txtOldPass);
                final EditText txtNewPass = (EditText) passDiag.findViewById(R.id.txtNewPass);
                final EditText txtConf = (EditText) passDiag.findViewById(R.id.txtConfirmPass);

                ((Button)passDiag.findViewById(R.id.btnSave)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        passDiag.dismiss();
                        waitDialog.show();
                        if (txtConf.getText().toString().equals(txtNewPass.getText().toString())){
                            passDto = new LoginSmarthouseDTO();
                            passDto.setContractId(sharedPreferences.getString(ConstManager.CONTRACT_ID,""));
                            passDto.setNewPassword(txtNewPass.getText().toString());
                            passDto.setOldPassword(txtOldPass.getText().toString());
                            final CloudApi loginStaffApi = RetroFitSingleton.getInstance().getCloudApi();
                            loginStaffApi.changePass(passDto).enqueue(new Callback<LoginSmarthouseDTO>() {
                                @Override
                                public void onResponse(Call<LoginSmarthouseDTO> call, Response<LoginSmarthouseDTO> response) {
                                    if (response.body().getNewPassword().equals("success")){
                                        passDiag.dismiss();
                                    } else {
                                        MessageUtils.makeText(PersonalInfoActivity.this,response.body().getNewPassword()).show();
                                    }
                                    waitDialog.dismiss();
                                }

                                @Override
                                public void onFailure(Call<LoginSmarthouseDTO> call, Throwable t) {
                                    MessageUtils.makeText(PersonalInfoActivity.this,"Không kết nối được").show();
                                    waitDialog.dismiss();
                                }
                            });
                            waitDialog.show();
                        }else {
                            MessageUtils.makeText(PersonalInfoActivity.this,"Mật khẩu xác nhận không giống mật khẩu mới").show();
                        }

                    }
                });
                ((Button)passDiag.findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        passDiag.dismiss();
                    }
                });
                passDiag.show();
            }
        });

        Button btnLoadBot = (Button)vaSetDiag.findViewById(R.id.btnLoadBot);
        btnLoadBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                botRole = botRoleAdapter.getItem(spinBotRole.getSelectedItemPosition());
                ownerName = editOwnerName.getText().toString();
                ownerRole = ownerRoleAdapter.getItem(spinOwnerRole.getSelectedItemPosition());

                SmartHouse house  = SmartHouse.getInstance();
                house.setBotOwnerNameRole(botName,botRole,ownerName,ownerRole);
                botApi.getDataVA(botTypeId).enqueue(new Callback<BotDataCentralDTO>() {
                    @Override
                    public void onResponse(Call<BotDataCentralDTO> call, Response<BotDataCentralDTO> response) {
                        Log.d(TAG,call.request().url()+"");
                        if (response.body() != null) {
                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            edit.putString(ConstManager.BOT_ROLE, botRole);
                            edit.putString(ConstManager.OWNER_ROLE, ownerRole);
                            edit.putString(ConstManager.OWNER_NAME, ownerName);
                            edit.putInt(ConstManager.BOT_TYPE_ID, botTypeId);
                            edit.putString(ConstManager.BOT_TYPE, botType);
                            edit.putString(ConstManager.BOT_NAME, botName);
                            edit.commit();
                            TermSQLite sqLite = new TermSQLite();
                            List<OwnerTrainEntity> trained = sqLite.getOwnerTrain();
                            Log.d(TAG, trained.size() + "  trains");
                            Map<String, Map<String, Integer>> updatedFunct = BotUtils.updateFuncts(trained, response.body().getFunctionMap());
                            DetectIntentSQLite sqlDect = new DetectIntentSQLite();
                            sqLite.clearAll();
                            sqlDect.clearAll();

                            for (SocialIntentDTO soc : response.body().getSocials()) {
                                sqlDect.insertSocial(new DetectSocialEntity(soc.getId(),
                                        soc.getName(), soc.getQuestion(), soc.getReply()));
                            }
                            for (FunctionIntentDTO funct : response.body().getFunctions()) {
                                sqlDect.insertFunction(new DetectFunctionEntity(funct.getId(),
                                        funct.getName(), funct.getSuccess(), funct.getFail(), funct.getRemind()));
                            }
                            SmartHouse house = SmartHouse.getInstance();
                            BotUtils bot = new BotUtils();
                            bot.saveFunctionTFIDFTerm(updatedFunct);
                            bot.saveSocialTFIDFTerm(response.body().getSocialMap());
                            bot.saveDeviceTFIDFTerm(house.getDevices());
                            bot.saveAreaTFIDFTerm(house.getAreas());
                            bot.saveScriptTFIDFTerm(house.getScripts());
                            vaSetDiag.dismiss();
                            MessageUtils.makeText(PersonalInfoActivity.this, "Tải dữ liệu trợ lý " + botName + " thành công").show();
                        } else {
                            MessageUtils.makeText(PersonalInfoActivity.this, "Không kết nối được "+ VolleySingleton.SERVER_HOST).show();
                        }
                        waitDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<BotDataCentralDTO> call, Throwable t) {
                        Log.d(TAG,"down load bot data failed");
                        MessageUtils.makeText(PersonalInfoActivity.this,"Tải dữ liệu trợ lý "+botName+" thất bại").show();
                        waitDialog.dismiss();
                    }
                });
                waitDialog.show();
            }
        });
        if (botTypeId == -1){
            btnLoadBot.setEnabled(false);
        }

    }
    private void refineNickNameTarget(){
        final SmartHouse house = SmartHouse.getInstance();
        for (final DeviceEntity device: house.getDevices()){
            if (device.getNickName() == null || device.getNickName().equals("")){
                inputDialog.setTitle("Tên gọi khác cho thiết bị"+device.getName());
                final EditText input = new EditText(PersonalInfoActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                inputDialog.setView(input);
                inputDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        device.setNickName(input.getText().toString());
                        DeviceSQLite.upById(device.getId(),device);
                        house.updateDeviceById(device.getId(),device);
                        dialog.dismiss();
                    }
                });
                inputDialog.show();
            }
        }
        for (final AreaEntity area: house.getAreas()){
            if (area.getNickName().length()<2){
                inputDialog.setTitle("Tên gọi khác cho không gian "+area.getName());
                final EditText input = new EditText(PersonalInfoActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                inputDialog.setView(input);
                inputDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        area.setNickName(input.getText().toString());
                        AreaSQLite.upAddressAndNickById(area.getId(),area);
                        house.updateAreaById(area.getId(),area);
                        dialog.dismiss();
                    }
                });
                inputDialog.show();
            }
        }
        for (final ScriptEntity mode: house.getScripts()){
            if (mode.getNickName().length()<2){
                inputDialog.setTitle("Tên gọi khác cho chế độ "+mode.getName());
                final EditText input = new EditText(PersonalInfoActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                inputDialog.setView(input);
                inputDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mode.setNickName(input.getText().toString());
                        ScriptSQLite.upModeOnly(mode.getId(),mode);
                        house.updateModeById(mode.getId(),mode);
                        dialog.dismiss();
                    }
                });
                inputDialog.show();
            }
        }
    }
}
