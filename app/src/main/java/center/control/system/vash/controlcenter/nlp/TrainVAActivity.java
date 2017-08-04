package center.control.system.vash.controlcenter.nlp;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.server.BotDataCentralDTO;
import center.control.system.vash.controlcenter.server.CloudApi;
import center.control.system.vash.controlcenter.server.FunctionIntentDTO;
import center.control.system.vash.controlcenter.server.RetroFitSingleton;
import center.control.system.vash.controlcenter.server.SocialIntentDTO;
import center.control.system.vash.controlcenter.utils.BotUtils;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.MessageUtils;
import center.control.system.vash.controlcenter.utils.SmartHouse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainVAActivity extends AppCompatActivity {
    private static final String TAG = "Train trợ lý :::";
    private EditText txtSenten;
    private RadioGroup rdoGr;
    ProgressDialog waitDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_va);
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle("Vui lòng đợi");
        waitDialog.setIndeterminate(true);
        waitDialog.setCancelable(false);


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
            String funcName = ConstManager.getVietnameseName(functs.get(i).getId());
            if (funcName.length()>2) {
                rdoBtn = new RadioButton(this);
                rdoBtn.setId(functs.get(i).getId());
                rdoBtn.setText(funcName);
                rdoBtn.setTextSize(18);
                group.addView(rdoBtn);
            }
        }
        btnTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtSenten.getText().toString().length()>0) {
                    OwnerTrainEntity train = new OwnerTrainEntity();
                    int selectedId = group.getCheckedRadioButtonId();

                    RadioButton selectedRdo = (RadioButton) findViewById(selectedId);
                    if (selectedRdo == null){
                        MessageUtils.makeText(TrainVAActivity.this,"Vui lòng chọn mục đích câu nói").show();
                    } else {
                        train.setName(DetectIntentSQLite.findFunctionById(selectedRdo.getId()).getFunctionName());
                        train.setWords(txtSenten.getText().toString()+" "+txtSenten.getText().toString()+" "+txtSenten.getText().toString()+" "
                        +txtSenten.getText().toString()+" "+txtSenten.getText().toString()+" "+txtSenten.getText().toString()+" "+
                                txtSenten.getText().toString()+" "+txtSenten.getText().toString()+" "+txtSenten.getText().toString()+" "
                                +txtSenten.getText().toString()+" "+txtSenten.getText().toString()+" "+txtSenten.getText().toString()+" ");
                        train.setType("function");
                        Log.d(TAG, train.getName() + "   " + train.getWords());
                        TermSQLite.insertOrUpdateTrain(train);
                        txtSenten.setText("");

                        final CloudApi botApi = RetroFitSingleton.getInstance().getCloudApi();
                        SharedPreferences sharedPreferences = getSharedPreferences(ConstManager.SHARED_PREF_NAME, MODE_PRIVATE);
                        botApi.getDataVA(sharedPreferences.getInt(ConstManager.BOT_TYPE_ID,-1)
                        ).enqueue(new Callback<BotDataCentralDTO>() {
                            @Override
                            public void onResponse(Call<BotDataCentralDTO> call, Response<BotDataCentralDTO> response) {
                                Log.d(TAG,call.request().url()+"");
                                if (response.body() != null) {
                                    TermSQLite sqLite = new TermSQLite();
                                    List<OwnerTrainEntity> trained = sqLite.getOwnerTrain();
                                    Map<String, Map<String, Integer>> updatedFunct = BotUtils.updateFuncts(trained, response.body().getFunctionMap());
                                    DetectIntentSQLite sqlDect = new DetectIntentSQLite();
                                    sqLite.clearAll();
                                    sqlDect.clearAll();

                                    for (SocialIntentDTO soc : response.body().getSocials()) {
                                        sqlDect.insertSocial(new DetectSocialEntity(soc.getId(),
                                                soc.getName(), soc.getQuestion(), soc.getReply()));
                                    }
                                    for (FunctionIntentDTO funct : response.body().getFunctions()) {
                                        sqlDect.insertFunction(new DetectFunctionEntity(funct.getId(),
                                                funct.getName(), funct.getSuccess(), funct.getFail(), funct.getRemind()));
                                    }
                                    SmartHouse house = SmartHouse.getInstance();
                                    BotUtils bot = new BotUtils();
                                    bot.saveFunctionTFIDFTerm(updatedFunct);
                                    bot.saveSocialTFIDFTerm(response.body().getSocialMap());
                                    bot.saveDeviceTFIDFTerm(house.getDevices());
                                    bot.saveAreaTFIDFTerm(house.getAreas());
                                    bot.saveScriptTFIDFTerm(house.getScripts());
                                    Log.d(TAG,"Day thanh cong");
                                    waitDialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Call<BotDataCentralDTO> call, Throwable t) {
                                Log.d(TAG,call.request().url()+"");
                                Log.d(TAG,"down load bot data failed");
                                waitDialog.dismiss();
                                MessageUtils.makeText(TrainVAActivity.this,"Dạy trợ lý thất bại");
                            }
                        });
                        waitDialog.show();
                    }
                } else {
                    MessageUtils.makeText(TrainVAActivity.this,"Vui lòng nhập câu").show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
