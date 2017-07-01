package center.control.system.vash.controlcenter.configuration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.command.CommandSQLite;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.trigger.MapDeviceTriggerActivity;
import center.control.system.vash.controlcenter.trigger.TriggerSQLite;

public class ConfigurationActivity extends AppCompatActivity {

    List<ConfigurationEntity> listConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);
        listConfiguration = new ArrayList<>();

        ConfigurationSQLite configurationSQLite = new ConfigurationSQLite();
        TriggerSQLite triggerSQLite = new TriggerSQLite();
        CommandSQLite commandSQLite = new CommandSQLite();


        triggerSQLite.cleardata();
        configurationSQLite.cleardata();
        commandSQLite.cleardata();

        String[] ListConfigurationNames = {"Cảnh báo trộm", "Cháy nổ", "Xì khí gas"};
        String[] ListTriggerNames = {"Cửa chính mở", "Phát hiện người lạ","Phạt hiện người quen",
                "Nhiệt độ quá nóng", "Nồng độ khí ga quá mức quy định", "Ánh sáng ở mức yêu",
                "Nhiệt độ quá lạnh", "Ánh sáng ở mức độ mạnh", "Âm thanh lớn quá mức", "Phát hiện có thiết bị điện chưa tắt",
                "Phát hiện cửa chính chưa đóng"};
        String[] ListCommands = {"Cửa phòng khách", "Cửa phòng khách 1", "Chuông báo động 1", "Chuông báo động 2",
                                "Cửa phòng ngủ 1", "Máy lạnh 1", "Đèn phòng khách", "Đèn sân"};

        for (String name : ListConfigurationNames) {
            configurationSQLite.insertConfiguration(name);
        }

        for (String name : ListTriggerNames) {
            triggerSQLite.insertTrigger(name);
        }

        int i = 0;
        for (String name : ListCommands) {
            i++;
            DeviceEntity entity = new DeviceEntity();
            entity.setName(name);
            entity.setId(i);
            commandSQLite.insertCommands(entity);
        }

        for (ConfigurationEntity entity : configurationSQLite.getAll()) {
            listConfiguration.add(entity);
            Log.d("--------------", "onCreate: " + listConfiguration.size());
        }





        ListConfigurationAdapter adapter = new ListConfigurationAdapter(ConfigurationActivity.this,listConfiguration);
        ListView lwConfiguration = (ListView) findViewById(R.id.lsConfig);
        lwConfiguration.setAdapter(adapter);

    }


    public void clicktoMapTrigger(View view) {
        startActivity(new Intent(ConfigurationActivity.this,MapDeviceTriggerActivity.class));
    }

    public void back(View view) {
        finish();
    }
}
