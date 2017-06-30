package center.control.system.vash.controlcenter.nlp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

import center.control.system.vash.controlcenter.R;

public class TrainVAActivity extends AppCompatActivity {
    private static final String TAG = "Train trợ lý :::";
    private EditText txtSenten;
    private RadioGroup rdoGr;
       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_va);
        final Button btnTrain = (Button) findViewById(R.id.btnTrain);
        txtSenten = (EditText) findViewById(R.id.txt_sentence);
        final RadioGroup group = (RadioGroup) findViewById(R.id.rdoIntent);
        RadioButton rdoBtn;
        LinearLayout lnBack = (LinearLayout) findViewById(R.id.lnBack);
        lnBack.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish();
           }
        });
        List<DetectFunctionEntity> functs =DetectIntentSQLite.getAllFunction();
        for(int i = 0; i < functs.size(); i++) {
            rdoBtn = new RadioButton(this);
            rdoBtn.setText(functs.get(i).getFunctionName());
            group.addView(rdoBtn);
        }
        btnTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OwnerTrainEntity train = new OwnerTrainEntity();
                int selectedId = group.getCheckedRadioButtonId();

                RadioButton selectedRdo = (RadioButton) findViewById(selectedId);

                train.setName(selectedRdo.getText().toString());
                train.setWords(txtSenten.getText().toString());
                Log.d(TAG,train.getName()+ "   "+train.getWords());
//                TermSQLite.insertOrUpdateTrain()
            }
        });
    }
}
