package center.control.system.vash.controlcenter.configuration;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.command.CommandEntity;
import center.control.system.vash.controlcenter.panel.ModePanel;
import center.control.system.vash.controlcenter.utils.SmartHouse;

public class SetConfigActivity extends AppCompatActivity {

    private AlertDialog.Builder selectStateDiag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_config);

        selectStateDiag= new AlertDialog.Builder(SetConfigActivity.this);
        selectStateDiag.setIcon(R.drawable.add);
        selectStateDiag.setTitle("Chọn trạng thái:");

        final Button btnSltState = (Button)  findViewById(R.id.btnSelectState);
        btnSltState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        selectStateDiag.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        selectStateDiag.setAdapter(SmartHouse.getInstance().getStateAdapter(this), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btnSltState.setText(SmartHouse.getInstance().getStates().get(which).getName());
            }
        });

    }
}
