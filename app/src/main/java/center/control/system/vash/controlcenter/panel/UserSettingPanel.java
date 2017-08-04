package center.control.system.vash.controlcenter.panel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import center.control.system.vash.controlcenter.MainActivity;
import center.control.system.vash.controlcenter.PersonalInfoActivity;
import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.utils.ConstManager;

public class UserSettingPanel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting_panel);
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
