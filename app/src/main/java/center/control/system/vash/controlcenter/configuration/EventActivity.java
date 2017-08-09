package center.control.system.vash.controlcenter.configuration;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import center.control.system.vash.controlcenter.R;

public class EventActivity extends AppCompatActivity {
//    private ListEventsAdapter eventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.condition_dialog);
        super.onCreate(savedInstanceState);


        ((Button)findViewById(R.id.btnSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                for (EventEntity event : eventsAdapter.getEventEntities()){
//                    StateConfigurationSQL.updateEventById(event.getId(),event);
//                }
            }
        });

    }
}
