package center.control.system.vash.controlcenter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.area.AreaAdapter;
import center.control.system.vash.controlcenter.area.AreaAttribute;
import center.control.system.vash.controlcenter.area.AreaAttributeAdapter;
import center.control.system.vash.controlcenter.area.AreaEntity;

public class ControlPanel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);
        List<AreaEntity> areaEntities = new ArrayList<>();
        AreaEntity areaEntity = new AreaEntity();
        areaEntity.setName("Phòng khách");
        areaEntities.add(areaEntity);
        RecyclerView lstAreaName = (RecyclerView) findViewById(R.id.lstAreaName);
        lstAreaName.setHasFixedSize(true);

        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(this);
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        AreaAdapter areaAdapter = new AreaAdapter(areaEntities);
        lstAreaName.setAdapter(areaAdapter);

        RecyclerView lstAreaAttribute = (RecyclerView) findViewById(R.id.lstAreaAttribute);
        lstAreaName.setHasFixedSize(true);
        AreaAttributeAdapter areaAttributeAdapter = new AreaAttributeAdapter();
        lstAreaAttribute.setAdapter(areaAttributeAdapter);

    }
}
