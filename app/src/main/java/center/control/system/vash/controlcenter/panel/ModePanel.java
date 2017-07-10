package center.control.system.vash.controlcenter.panel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.command.CommandEntity;
import center.control.system.vash.controlcenter.device.DeviceSQLite;
import center.control.system.vash.controlcenter.nlp.CurrentContext;
import center.control.system.vash.controlcenter.nlp.DetectFunctionEntity;
import center.control.system.vash.controlcenter.nlp.DetectIntentSQLite;
import center.control.system.vash.controlcenter.script.CommandAdapter;
import center.control.system.vash.controlcenter.script.ListScriptAdapter;
import center.control.system.vash.controlcenter.script.ScriptEntity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
import center.control.system.vash.controlcenter.script.TodayAdapter;
import center.control.system.vash.controlcenter.utils.BotUtils;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.SmartHouse;

public class ModePanel extends AppCompatActivity implements ListScriptAdapter.OnAdapterItemClickListener{
    private static final String TAG = "Mode panel ----";
    private ListScriptAdapter scriptAdapter;
    private CommandAdapter commandAdapter;
    private RecyclerView lstScriptDevice;
    private AlertDialog.Builder selectDeviceDiag;
    private AlertDialog.Builder selectAreaDiag;
    private TextView txtTimeSch;
    private TodayAdapter todayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_panel);
        ImageButton currentTab = (ImageButton) findViewById(R.id.tabBtnMode);
        currentTab.setImageResource(R.drawable.tab_mode_active);
        currentTab.setBackgroundColor(Color.WHITE);
        final SmartHouse house = SmartHouse.getInstance();

        TextView txtTodayTime = (TextView) findViewById(R.id.txtTodayTime);
        txtTodayTime.setText("Thu " +Calendar.getInstance().get(Calendar.DAY_OF_WEEK)+", "+
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"/"+
                (Calendar.getInstance().get(Calendar.MONTH)+1)+"/"+
                Calendar.getInstance().get(Calendar.YEAR));

        RecyclerView lstScriptMana = (RecyclerView) findViewById(R.id.listItemLeft);
        lstScriptMana.setHasFixedSize(true);
        LinearLayoutManager verticalLayout = new LinearLayoutManager(this);
        verticalLayout.setOrientation(LinearLayoutManager.VERTICAL);
        lstScriptMana.setLayoutManager(verticalLayout);

        RecyclerView lstToday = (RecyclerView) findViewById(R.id.listItemRight);
        lstToday.setHasFixedSize(true);
        LinearLayoutManager verticalLayout2 = new LinearLayoutManager(this);
        verticalLayout2.setOrientation(LinearLayoutManager.VERTICAL);
        lstToday.setLayoutManager(verticalLayout2);

        todayAdapter = new TodayAdapter(SmartHouse.getInstance().getTodayMode(),this);
        lstToday.setAdapter(todayAdapter);

        scriptAdapter = new ListScriptAdapter(house.getScripts(), this);
        lstScriptMana.setAdapter(scriptAdapter);

        final Dialog dialog = new Dialog(this);
//        dialog.setTitle("THÊM CHẾ ĐỘ");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_script_dialog);
        
        selectAreaDiag = new AlertDialog.Builder(ModePanel.this);
        selectAreaDiag.setIcon(R.drawable.add);
        selectAreaDiag.setTitle("Chọn không gian:");

        selectDeviceDiag = new AlertDialog.Builder(ModePanel.this);
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
                final List<CommandEntity> newScript = house.getDeviceScriptByAreaId(areaId);
                ArrayAdapter<String> devNames = new ArrayAdapter<String>(ModePanel.this,android.R.layout.select_dialog_singlechoice);

                for (CommandEntity sde : newScript){
                    if (sde.getDeviceName() != null)
                    devNames.add(sde.getDeviceName());
                }

                selectDeviceDiag.setAdapter(devNames,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newScript.get(which).setDeviceState("on");
                        commandAdapter.addScripDev(newScript.get(which));
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
                List<CommandEntity> scriptDeviceEntities = new ArrayList<CommandEntity>();

                lstScriptDevice = (RecyclerView) dialog.findViewById(R.id.lstDeviceScript);
                lstScriptDevice.setHasFixedSize(true);
                LinearLayoutManager verticalLayout = new LinearLayoutManager(getApplicationContext());
                verticalLayout.setOrientation(LinearLayoutManager.VERTICAL);
                lstScriptDevice.setLayoutManager(verticalLayout);
                commandAdapter = new CommandAdapter(scriptDeviceEntities);
                lstScriptDevice.setAdapter(commandAdapter);

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
                        script.setHour(-1);
                        script.setMinute(-1);
                        List<CommandEntity> listCommmand = commandAdapter.getScriptDeviceEntities();
                        Log.d(TAG,listCommmand.size()+" ");
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

    public void clicktoSettingPanel(View view) {
        startActivity(new Intent(this, UserSettingPanel.class));
    }

    public void clicktoVAPanel(View view) {
        startActivity(new Intent(this, VAPanel.class));
    }

    @Override
    public void onScriptClick(final ScriptEntity scriptEntity) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("CHẾ ĐỘ "+scriptEntity.getName());
        dialog.setContentView(R.layout.dialog_script_detail);
        final GridView weekCheckList = (GridView)dialog.findViewById(R.id.lstWeekDay);

        ScriptSQLite sqLite = new ScriptSQLite();

        lstScriptDevice = (RecyclerView) dialog.findViewById(R.id.lstDeviceScript);
        lstScriptDevice.setHasFixedSize(true);
        LinearLayoutManager verticalLayout = new LinearLayoutManager(getApplicationContext());
        verticalLayout.setOrientation(LinearLayoutManager.VERTICAL);
        lstScriptDevice.setLayoutManager(verticalLayout);
        Log.d(TAG,"Script id  "+scriptEntity.getHour()+"  "+scriptEntity.getMinute());
        commandAdapter = new CommandAdapter(sqLite.getCommandByScriptId(scriptEntity.getId()));
        lstScriptDevice.setAdapter(commandAdapter);


        txtTimeSch = (TextView) dialog.findViewById(R.id.txtSchedulerTime);

        txtTimeSch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        Button btnAddDevice = (Button) dialog.findViewById(R.id.btnAddDeviceScript);
        btnAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAreaDiag.show();
            }
        });
        final RadioGroup rgr = (RadioGroup) dialog.findViewById(R.id.rdoGrSchedulerType);
        rgr.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId ==  R.id.rdoRepeat){
                    ArrayAdapter<String> weekDayAdapter = new ArrayAdapter<String>
                            (ModePanel.this,
                                    android.R.layout.simple_list_item_multiple_choice,
                                    ScriptEntity.weekDays);
                    weekCheckList.setAdapter(weekDayAdapter);
                    weekCheckList.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
//                    Log.d(TAG,scriptEntity.getWeeksDay().size()+" s "+scriptEntity.getWeekDay());
                    for (int i = 0;i<ScriptEntity.weekDayValue.length;i++) {
                        if (scriptEntity.getWeekDay()!= null
                         && scriptEntity.getWeekDay().contains(ScriptEntity.weekDayValue[i]+"")) {
                            weekCheckList.setItemChecked(i, true);
                        }

                    }
                }
            }
        });

        final Switch swtSched = (Switch) dialog.findViewById(R.id.swtSetSchedule);
        swtSched.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (scriptEntity.getHour() != -1){
                        int hourOfDay = scriptEntity.getHour();
                        int minute = scriptEntity.getMinute();
                        txtTimeSch.setText((hourOfDay<10?"0":"")+ hourOfDay +":"+(minute<10?"0":"")+minute);
                    } else {
                        txtTimeSch.setText("06:00");
                    }

                    txtTimeSch.setEnabled(true);
                    (dialog.findViewById(R.id.rdoTomorrow)).setEnabled(true);
                    (dialog.findViewById(R.id.rdoRepeat)).setEnabled(true);
                    if (scriptEntity.isOnlyOneTime()){
                        ((RadioButton) dialog.findViewById(R.id.rdoTomorrow)).setChecked(true);
                    } else {
                        ((RadioButton) dialog.findViewById(R.id.rdoRepeat)).setChecked(true);
                        weekCheckList.setEnabled(true);
                    }
                } else {
                    txtTimeSch.setEnabled(false);
                    (dialog.findViewById(R.id.rdoTomorrow)).setEnabled(false);
                    (dialog.findViewById(R.id.rdoRepeat)).setEnabled(false);
                    weekCheckList.setEnabled(false);
                }
            }
        });

        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sltedRdoId = rgr.getCheckedRadioButtonId();
                List<CommandEntity> listCommmand = commandAdapter.getScriptDeviceEntities();
                if (swtSched.isChecked()) {
                    String time[] = txtTimeSch.getText().toString().split(":");
                    scriptEntity.setHour(Integer.parseInt(time[0]));
                    scriptEntity.setMinute(Integer.parseInt(time[1]));
                    Log.d(TAG,scriptEntity.getHour()+"  "+scriptEntity.getMinute());
                    if (sltedRdoId == R.id.rdoTomorrow) {
                        Calendar calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_WEEK)+1;
                        if (day == 8) day =1;
                        scriptEntity.setWeeksDay(day+"");
                        scriptEntity.setOnlyOneTime(true);
                    } else {
                        SparseBooleanArray checkedType = weekCheckList.getCheckedItemPositions();
                        String weeksDay = "";
                        for (int i = 0; i < weekCheckList.getCount(); i++) {
                            if (checkedType.get(i)) {
                                weeksDay += ScriptEntity.weekDayValue[i] + ",";
                            }
                        }
                        Log.d(TAG,weeksDay);
                        scriptEntity.setOnlyOneTime(false);
                        scriptEntity.setWeeksDay(weeksDay);
                    }
                } else {
                    scriptEntity.setHour(-1);
                    scriptEntity.setMinute(-1);
                }
                ScriptSQLite.upById(scriptEntity.getId(), scriptEntity, listCommmand);
                SmartHouse.getInstance().updateModeById(scriptEntity.getId(),scriptEntity);
                scriptAdapter.setScriptEntities(SmartHouse.getInstance().getScripts());
                todayAdapter.setScriptEntities(SmartHouse.getInstance().getTodayMode());
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

        if (scriptEntity.getHour() > -1){
            swtSched.setChecked(true);
        } else swtSched.setChecked(false);
        dialog.show();
    }

    @Override
    public void onLongScriptClick(final ScriptEntity scriptEntity) {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Xóa thiết bị")
                .setMessage("Bạn có muốn xóa che do "+scriptEntity.getName()+" không?")
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ScriptSQLite.deleteModeById(scriptEntity.getId());
                        SmartHouse.getInstance().removeModeById(scriptEntity.getId());
                        scriptAdapter.setScriptEntities(SmartHouse.getInstance().getScripts());
                        todayAdapter.setScriptEntities(SmartHouse.getInstance().getTodayMode());
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void activaScript(ScriptEntity item) {
        DetectFunctionEntity funct = DetectIntentSQLite.findFunctionById(ConstManager.FUNCTION_START_MODE);
        CurrentContext.getInstance().setDetectedFunction(funct);
        CurrentContext.getInstance().setScript(item);
        BotUtils.implementCommand(funct,null,item);
        Log.d(TAG,"Script acted");
    }

    public void clicktoModePanel(View view) {

    }

    public void showTimePickerDialog()
    {
        TimePickerDialog.OnTimeSetListener callback=new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view,
                                  int hourOfDay, int minute) {
                String s= (hourOfDay<10?"0":"")+ hourOfDay +":"+(minute<10?"0":"")+minute;
                txtTimeSch.setText(s);
            }
        };
        String s=txtTimeSch.getText().toString();
        String strArr[]=s.split(":");
        int gio=Integer.parseInt(strArr[0]);
        int phut=Integer.parseInt(strArr[1]);
        TimePickerDialog time=new TimePickerDialog(
                ModePanel.this,
                callback, gio, phut, true);
        time.setTitle("Hẹn giờ");
        time.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SmartHouse.getInstance().setRunToday(todayAdapter.getScriptEntities());
    }
}
