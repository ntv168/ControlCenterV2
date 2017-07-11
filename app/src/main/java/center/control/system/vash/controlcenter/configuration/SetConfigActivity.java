package center.control.system.vash.controlcenter.configuration;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class SetConfigActivity extends AppCompatActivity implements EventAdapter.EventListener{
    private static final String TAG = "Config Thuan Lammmm: ";
    private CommandAdapter cmdAdapter;
    private EventAdapter eventAdapter;
    private AlertDialog.Builder selectStateDiag;
    private List<StateEntity> stats;
    private AlertDialog.Builder selectDeviceDiag;
    private AlertDialog.Builder selectAreaDiag;
    private StateEntity currentState;
    private ProgressDialog waitDialog;


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_config);

        stats = SmartHouse.getInstance().getStates();
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


        RecyclerView lstEvent = (RecyclerView) findViewById(R.id.lstEvent);
        lstEvent.setHasFixedSize(true);
        LinearLayoutManager verticalLayout2 = new LinearLayoutManager(this);
        verticalLayout2.setOrientation(LinearLayoutManager.VERTICAL);
        lstEvent.setLayoutManager(verticalLayout2);

        eventAdapter = new EventAdapter(new ArrayList<EventEntity>(),this);
        lstEvent.setAdapter(eventAdapter);


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
                cmdAdapter.setScriptEntities(stats.get(which).getCommands());
                Log.d(TAG,stats.get(which).getEvents().size()+" s");
                eventAdapter.setScriptEntities(stats.get(which).getEvents());
                currentState = stats.get(which);
                dialog.dismiss();
            }
        });
        selectAreaDiag = new AlertDialog.Builder(SetConfigActivity.this);
        selectAreaDiag.setIcon(R.drawable.add);
        selectAreaDiag.setTitle("Chọn không gian:");

        Button btnAddCommand = (Button) findViewById(R.id.btnAddCommand);
        btnAddCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAreaDiag.setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final SmartHouse house = SmartHouse.getInstance();
                selectAreaDiag.setAdapter(house.getAreaNameAdapter(SetConfigActivity.this), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int areaId = house.getAreas().get(which).getId();
                        final List<CommandEntity> newScript = house.getDeviceScriptByAreaId(areaId);
                        ArrayAdapter<String> devNames = new ArrayAdapter<String>(SetConfigActivity.this,android.R.layout.select_dialog_singlechoice);

                        for (CommandEntity sde : newScript){
                            if (sde.getDeviceName() != null)
                                devNames.add(sde.getDeviceName());
                        }
                        selectDeviceDiag = new AlertDialog.Builder(SetConfigActivity.this);
                        selectDeviceDiag.setIcon(R.drawable.more);
                        selectDeviceDiag.setTitle("Chọn thiết bị:");
                        selectDeviceDiag.setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        selectDeviceDiag.setAdapter(devNames,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        cmdAdapter.addScripDev(newScript.get(which));
                                        dialog.dismiss();
                                    }
                                });
                        selectDeviceDiag.show();
                    }
                });
                selectAreaDiag.show();
            }
        });

        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle("Vui lòng đợi");
        waitDialog.setIndeterminate(true);
        waitDialog.setCancelable(false);

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitDialog.show();
                List<CommandEntity> listCommmand = cmdAdapter.getScriptDeviceEntities();
                Log.d(TAG,listCommmand.size()+" ");
                ScriptSQLite.insertStateCommand(currentState.getId(),listCommmand);
                for (EventEntity event : eventAdapter.getEventEntities()){
                    StateConfigurationSQL.updateEventById(event.getId(),event);
                }
                SmartHouse.getInstance().updateStateById(currentState.getId(),listCommmand, eventAdapter.getEventEntities());
                waitDialog.dismiss();
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

    @Override
    public void onClick(final int eventId) {
        final SmartHouse house = SmartHouse.getInstance();
        selectAreaDiag.setAdapter(house.getAreaNameAdapter(this), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eventAdapter.updateAreaId(eventId,house.getAreas().get(which).getId());
            }
        });
        selectAreaDiag.show();
    }
}
