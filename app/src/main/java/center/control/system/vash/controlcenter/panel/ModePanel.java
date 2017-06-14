package center.control.system.vash.controlcenter.panel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.script.ListDeviceInScriptAdapter;
import center.control.system.vash.controlcenter.script.ListScriptAdapter;
import center.control.system.vash.controlcenter.script.ScriptDeviceEntity;
import center.control.system.vash.controlcenter.script.ScriptEntity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
import center.control.system.vash.controlcenter.utils.SmartHouse;

public class ModePanel extends AppCompatActivity implements ListScriptAdapter.OnAdapterItemClickListener{
    private static final String TAG = "Mode panel ----";
    private ListScriptAdapter scriptAdapter;
    private ListDeviceInScriptAdapter listDeviceInScriptAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_panel);
        ImageButton currentTab = (ImageButton) findViewById(R.id.tabBtnMode);
        currentTab.setBackgroundColor(Color.GREEN);
        final SmartHouse house = SmartHouse.getInstance();

        RecyclerView lstScriptMana = (RecyclerView) findViewById(R.id.listItemLeft);
        lstScriptMana.setHasFixedSize(true);
        LinearLayoutManager verticalLayout = new LinearLayoutManager(this);
        verticalLayout.setOrientation(LinearLayoutManager.VERTICAL);
        lstScriptMana.setLayoutManager(verticalLayout);

        scriptAdapter = new ListScriptAdapter(house.getScripts(), this);
        lstScriptMana.setAdapter(scriptAdapter);

        final Dialog dialog = new Dialog(this);
        dialog.setTitle("THÊM CHẾ ĐỘ");
        dialog.setContentView(R.layout.add_script_dialog);
        
        final AlertDialog.Builder selectAreaDiag = new AlertDialog.Builder(ModePanel.this);
        selectAreaDiag.setIcon(R.drawable.add);
        selectAreaDiag.setTitle("Chọn không gian:");

        final AlertDialog.Builder selectDeviceDiag = new AlertDialog.Builder(ModePanel.this);
        selectDeviceDiag.setIcon(R.drawable.add);
        selectDeviceDiag.setTitle("Chọn thiết bị:");
        selectDeviceDiag.setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        selectAreaDiag.setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        selectAreaDiag.setAdapter(house.getAreaNameAdapter(this), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int areaId = house.getAreas().get(which).getId();
                final List<ScriptDeviceEntity> newScript = house.getDeviceScriptByAreaId(areaId);
                ArrayAdapter<String> devNames = new ArrayAdapter<String>(ModePanel.this,android.R.layout.select_dialog_singlechoice);

                for (ScriptDeviceEntity sde : newScript){
                    if (sde.getDeviceName() != null)
                    devNames.add(sde.getDeviceName());
                }

                selectDeviceDiag.setAdapter(devNames,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newScript.get(which).setDeviceState("on");
                        listDeviceInScriptAdapter.addScripDev(newScript.get(which));
                        dialog.dismiss();
                    }
                });
                selectDeviceDiag.show();
            }
        });



        Button btnAddMode = (Button) findViewById(R.id.btnAddMode);
        btnAddMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText scriptName = (EditText) dialog.findViewById(R.id.txtScriptName);
                final EditText scriptNickName= (EditText) dialog.findViewById(R.id.txtScriptNickname);
                List<ScriptDeviceEntity> scriptDeviceEntities = new ArrayList<ScriptDeviceEntity>();

                RecyclerView lstScriptDevice = (RecyclerView) dialog.findViewById(R.id.lstDeviceScript);
                lstScriptDevice.setHasFixedSize(true);
                LinearLayoutManager verticalLayout = new LinearLayoutManager(getApplicationContext());
                verticalLayout.setOrientation(LinearLayoutManager.VERTICAL);
                lstScriptDevice.setLayoutManager(verticalLayout);
                listDeviceInScriptAdapter = new ListDeviceInScriptAdapter(scriptDeviceEntities);
                lstScriptDevice.setAdapter(listDeviceInScriptAdapter);

                Button btnAddDevice = (Button) dialog.findViewById(R.id.btnAddDeviceScript);
                btnAddDevice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectAreaDiag.show();
                    }
                });
                Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ScriptEntity script =new ScriptEntity();
                        script.setName(scriptName.getText().toString());
                        script.setNickName(scriptNickName.getText().toString());
                        List<ScriptDeviceEntity> listCommmand = listDeviceInScriptAdapter.getScriptDeviceEntities();
                        ScriptSQLite.insertScript(script,listCommmand);
                        scriptAdapter.addScrip(script);
                        dialog.dismiss();
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
    public void onScriptClick(ScriptEntity scriptEntity) {

    }

    @Override
    public void onLongScriptClick(ScriptEntity scriptEntity) {

    }
}
