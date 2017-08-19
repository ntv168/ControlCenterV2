package center.control.system.vash.controlcenter.panel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.text.SimpleDateFormat;
import java.util.Date;

import center.control.system.vash.controlcenter.MainActivity;
import center.control.system.vash.controlcenter.PersonalInfoActivity;
import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.nlp.CurrentContext;
import center.control.system.vash.controlcenter.nlp.TermSQLite;
import center.control.system.vash.controlcenter.server.CloudApi;
import center.control.system.vash.controlcenter.server.RetroFitSingleton;
import center.control.system.vash.controlcenter.server.SmartHouseRequestDTO;
import center.control.system.vash.controlcenter.utils.BotUtils;
import center.control.system.vash.controlcenter.utils.ConstManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSettingPanel extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String TAG = "User seting panel";
    ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting_panel);
        sharedPreferences = getSharedPreferences(ConstManager.SHARED_PREF_NAME, MODE_PRIVATE);
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle("Vui lòng đợi");
        waitDialog.setIndeterminate(true);
        waitDialog.setCancelable(false);
        ImageButton currentTab = (ImageButton) findViewById(R.id.tabBtnSetting);
        currentTab.setImageResource(R.drawable.tab_setting_active);
        currentTab.setBackgroundColor(Color.WHITE);

        ImageButton btnSetLogout = (ImageButton) findViewById(R.id.btnSetLogout);
        btnSetLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(ConstManager.SHARED_PREF_NAME, MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString(ConstManager.CONTRACT_ID,"");
                edit.commit();

                TermSQLite.clearTrain();
                startActivity(new Intent(UserSettingPanel.this, MainActivity.class));
            }
        });
        ImageButton btnPerson = (ImageButton) findViewById(R.id.btnPerson);
        btnPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserSettingPanel.this, PersonalInfoActivity.class));
            }
        });
    }
    public void sendClaim(){
        String contractId = sharedPreferences.getString(ConstManager.CONTRACT_ID,"");
        Log.d(TAG,"contractId Id " + contractId);
        final CloudApi botApi = RetroFitSingleton.getInstance().getCloudApi();
        SmartHouseRequestDTO request = new SmartHouseRequestDTO();
        request.setContractId(contractId);
        request.setRequestContent("Yêu cầu nhân viên cập nhật cấu hình");
        request.setRequestDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        botApi.sendRequest(request).enqueue(new Callback<SmartHouseRequestDTO>() {
            @Override
            public void onResponse(Call<SmartHouseRequestDTO> call, Response<SmartHouseRequestDTO> response) {
                waitDialog.dismiss();
                Log.d(TAG,call.request().url()+"");
            }

            @Override
            public void onFailure(Call<SmartHouseRequestDTO> call, Throwable t) {
                waitDialog.dismiss();
                Log.d(TAG,call.request().url()+"");
            }
        });
        waitDialog.show();
    }
    public void clicktoSettingPanel(View view) {}
    public void clicktoModePanel(View view) {
        startActivity(new Intent(this, ModePanel.class));
    }

    public void clicktoControlPanel(View view) {
        startActivity(new Intent(this, ControlPanel.class));
    }
    public void clicktoVAPanel(View view) {
        startActivity(new Intent(this, VAPanel.class));
    }
}
