package center.control.system.vash.controlcenter.panel;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.device.ManageDeviceActivity;
import center.control.system.vash.controlcenter.service.WebServerService;

public class SettingPanel extends AppCompatActivity {

    public static final String SETTING_FILTER_RECEIVER = "central.start.localbroadcast.APPLICATION_MOBILE";
    private static final String TAG = "Setting Panel";
    private BroadcastReceiver receiver;

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter(SETTING_FILTER_RECEIVER));
        Log.d(TAG,"start broadcast");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_panel);
        ImageButton currentTab = (ImageButton) findViewById(R.id.tabBtnSetting);
        currentTab.setBackgroundColor(Color.GREEN);
        final Dialog dialog = new Dialog(SettingPanel.this);
        dialog.setContentView(R.layout.activate_diaglog);

        ImageButton btnActive = (ImageButton) findViewById(R.id.btnSetReset);
        btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button btnStartServ = (Button) dialog.findViewById(R.id.btnStartServer);
                btnStartServ.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent webService = new Intent(SettingPanel.this, WebServerService.class);
                        TextView text = (TextView) dialog.findViewById(R.id.txtServerResult);
                        text.setText("starting server ...");
                        startService(webService);
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
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String messageSource = intent.getStringExtra(WebServerService.RESULT);
                TextView text = (TextView) dialog.findViewById(R.id.txtServerResult);
                text.setText(messageSource);
            }
        };

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
}
