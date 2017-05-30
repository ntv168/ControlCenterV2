package center.control.system.vash.controlcenter.panel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.area.AreaAdapter;
import center.control.system.vash.controlcenter.area.AreaAttributeAdapter;
import center.control.system.vash.controlcenter.area.AreaEntity;

public class ControlPanel extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);
        List<AreaEntity> areaEntities = new ArrayList<>();
        AreaEntity areaEntity = new AreaEntity();
        areaEntity.setName("Phòng khách");

//        areaEntities.add(areaEntity);
//        RecyclerView lstAreaName = (RecyclerView) findViewById(R.id.lstAreaName);
//        lstAreaName.setHasFixedSize(true);
//
//        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(this);
//        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//
//        lstAreaName.setLayoutManager(MyLayoutManager);
//
//        AreaAdapter areaAdapter = new AreaAdapter(areaEntities);
//        lstAreaName.setAdapter(areaAdapter);
//
//
//
//        LinearLayoutManager MyLayoutManager2 = new LinearLayoutManager(this);
//        MyLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
//        RecyclerView lstAreaAttribute = (RecyclerView) findViewById(R.id.lstAreaAttribute);
//        lstAreaName.setHasFixedSize(true);
//
//        lstAreaAttribute.setLayoutManager(MyLayoutManager2);
//        AreaAttributeAdapter areaAttributeAdapter = new AreaAttributeAdapter();
//
//        lstAreaAttribute.setAdapter(areaAttributeAdapter);

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
