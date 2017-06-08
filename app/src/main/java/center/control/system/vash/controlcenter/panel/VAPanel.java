package center.control.system.vash.controlcenter.panel;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import center.control.system.vash.controlcenter.R;

public class VAPanel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vapanel);
        ImageButton currentTab = (ImageButton) findViewById(R.id.tabBtnVA);
        currentTab.setBackgroundColor(Color.GREEN);
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
}
