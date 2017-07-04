package center.control.system.vash.controlcenter.configuration;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.command.CommandEntity;
import center.control.system.vash.controlcenter.script.CommandAdapter;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
import center.control.system.vash.controlcenter.utils.SmartHouse;

public class SetConfigActivity extends AppCompatActivity{
    private CommandAdapter cmdAdapter;
    private AlertDialog.Builder selectStateDiag;
    private List<StateEntity> stats;
    private AlertDialog.Builder selectDeviceDiag;
    private AlertDialog.Builder selectAreaDiag;
    private StateEntity currentState;

    @Override
    protected void onResume() {
        super.onResume();
        stats = SmartHouse.getInstance().getStates();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_config);

        selectStateDiag= new AlertDialog.Builder(SetConfigActivity.this);
        selectStateDiag.setIcon(R.drawable.add);
        selectStateDiag.setTitle("Chọn trạng thái:");

        RecyclerView lstCmd = (RecyclerView) findViewById(R.id.lstCmd);
        lstCmd .setHasFixedSize(true);
        LinearLayoutManager verticalLayout = new LinearLayoutManager(this);
        verticalLayout.setOrientation(LinearLayoutManager.VERTICAL);
        lstCmd .setLayoutManager(verticalLayout);

        cmdAdapter = new CommandAdapter(new ArrayList<CommandEntity>());
        lstCmd .setAdapter(cmdAdapter);

        final Button btnSltState = (Button)  findViewById(R.id.btnSelectState);
        btnSltState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStateDiag.show();
            }
        });
        selectStateDiag.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        selectStateDiag.setAdapter(getStateAdapter(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btnSltState.setText(stats.get(which).getName());
                cmdAdapter = new CommandAdapter(SmartHouse.getInstance().getCommandInState(
                        stats.get(which).getId()));
                cmdAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        Button btnAddCommand = (Button) findViewById(R.id.btnAddCommand);
        btnAddCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAreaDiag.show();
            }
        });
        selectAreaDiag = new AlertDialog.Builder(SetConfigActivity.this);
        selectAreaDiag.setIcon(R.drawable.add);
        selectAreaDiag.setTitle("Chọn không gian:");

        selectDeviceDiag = new AlertDialog.Builder(SetConfigActivity.this);
        selectDeviceDiag.setIcon(R.drawable.more);
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
        final SmartHouse house = SmartHouse.getInstance();
        selectAreaDiag.setAdapter(house.getAreaNameAdapter(this), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int areaId = house.getAreas().get(which).getId();
                final List<CommandEntity> newScript = house.getDeviceScriptByAreaId(areaId);
                ArrayAdapter<String> devNames = new ArrayAdapter<String>(SetConfigActivity.this,android.R.layout.select_dialog_singlechoice);

                for (CommandEntity sde : newScript){
                    if (sde.getDeviceName() != null)
                        devNames.add(sde.getDeviceName());
                }

                selectDeviceDiag.setAdapter(devNames,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                newScript.get(which).setDeviceState("on");
                                cmdAdapter.addScripDev(newScript.get(which));
                                dialog.dismiss();
                            }
                        });
                selectDeviceDiag.show();
            }
        });

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ScriptEntity script =new ScriptEntity();
//                script.setName(scriptName.getText().toString());
//                script.setNickName(scriptNickName.getText().toString());
//                script.setHour(-1);
//                script.setMinute(-1);
//                List<CommandEntity> listCommmand = commandAdapter.getScriptDeviceEntities();
//                Log.d(TAG,listCommmand.size()+" ");
//                ScriptSQLite.insertStateCommand(stats.,listCommmand);
//                scriptAdapter.addScrip(script);
//                dialog.dismiss();
            }
        });


    }

    private ListAdapter getStateAdapter() {
        ArrayAdapter<String> stateNameAdapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_singlechoice);
        for (StateEntity state: stats){
            stateNameAdapter.add(state.getName());
        }
        return stateNameAdapter;
    }
}
