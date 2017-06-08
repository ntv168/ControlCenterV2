package center.control.system.vash.controlcenter.panel;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.area.AreaAdapter;
import center.control.system.vash.controlcenter.area.AreaAttribute;
import center.control.system.vash.controlcenter.area.AreaAttributeAdapter;
import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.utils.SharedPrefConstant;
import center.control.system.vash.controlcenter.utils.SmartHouse;

public class ControlPanel extends Activity {
    SharedPreferences sharedPreferences;
    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences(SharedPrefConstant.SMART_HOUSE_SHARED_PREF, MODE_PRIVATE);
        String tokenSaved = sharedPreferences.getString(SharedPrefConstant.HOUSE_ID,"");

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);
        ImageButton currentTab = (ImageButton) findViewById(R.id.tabBtnHome);
        currentTab.setBackgroundColor(Color.GREEN);

        RecyclerView lstAreaName = (RecyclerView) findViewById(R.id.lstAreaName);
        lstAreaName.setHasFixedSize(true);

        LinearLayoutManager horizonLayout = new LinearLayoutManager(this);
        horizonLayout.setOrientation(LinearLayoutManager.HORIZONTAL);

        lstAreaName.setLayoutManager(horizonLayout);
        LinearLayoutManager horizonLayout2 = new LinearLayoutManager(this);
        horizonLayout2.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView lstAreaAttribute = (RecyclerView) findViewById(R.id.lstAreaAttribute);
        lstAreaName.setHasFixedSize(true);

        lstAreaAttribute.setLayoutManager(horizonLayout2);
        List<AreaAttribute> listAttrbute =  new ArrayList<>();
        for (String name : AreaEntity.attrivutes){
            AreaAttribute attribute = new AreaAttribute();
            attribute.setName(name);
            attribute.setValue(" ");
            listAttrbute.add(attribute);
        }
        AreaAttributeAdapter areaAttributeAdapter = new AreaAttributeAdapter(listAttrbute);

        lstAreaAttribute.setAdapter(areaAttributeAdapter);
        SmartHouse house = SmartHouse.getInstance();
        AreaAdapter areaAdapter = new AreaAdapter(house.getAreas(),areaAttributeAdapter);
        lstAreaName.setAdapter(areaAdapter);

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
