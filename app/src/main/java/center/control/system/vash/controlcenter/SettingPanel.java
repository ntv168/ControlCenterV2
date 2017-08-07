package center.control.system.vash.controlcenter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Person;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import center.control.system.vash.controlcenter.device.ManageDeviceActivity;
import center.control.system.vash.controlcenter.event.MapDeviceTriggerActivity;
import center.control.system.vash.controlcenter.helper.StorageHelper;
import center.control.system.vash.controlcenter.utils.MessageUtils;

public class SettingPanel extends AppCompatActivity {
    private static final String TAG = "Setting Panel";
    private ProgressDialog waitDiag;
    @Override
    protected void onResume() {
        super.onResume();

    }
    private void initStateMachine() {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_panel);

        final AlertDialog dialog;

        AlertDialog.Builder builer = new AlertDialog.Builder(this);

        builer.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builer.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builer.create();
        dialog.setTitle("Đặt lại thiết bị");
        dialog.setMessage("Đặt lại thiết bị sẽ xóa mọi kết nối và cấu hình trong trung tâm điều khiển?");
        dialog.setCancelable(false);


        waitDiag = new ProgressDialog(this);
        waitDiag.setTitle("Tải dữ liệu");
        waitDiag.setIndeterminate(true);
//        waitDialog.setCancelable(false);

        ImageButton btnActive = (ImageButton) findViewById(R.id.btnSetReset);
        btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.show();
            }
        });

        ImageButton btnDevice = (ImageButton) findViewById(R.id.btnSetDevice);
        btnDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingPanel.this, ManageDeviceActivity.class));
            }
        });

        ImageButton btnSetLogout = (ImageButton) findViewById(R.id.btnSetLogout);
        btnSetLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingPanel.this, MainActivity.class));
            }
        });

        ImageButton btnSetConfig = (ImageButton) findViewById(R.id.btnSetConfig);
        btnSetConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingPanel.this, MapDeviceTriggerActivity.class));
            }
        });

        String Id = StorageHelper.getPersonGroupId("nguoinha",SettingPanel.this);
        if (!StorageHelper.getAllPersonIds(Id, SettingPanel.this).isEmpty()) {
            StorageHelper.clearPersonIds(Id,SettingPanel.this);
        }
        waitDiag.show();
        new GetPersonIdsTask().execute(Id);
        initStateMachine();
    }

    class GetPersonIdsTask extends AsyncTask<String, String, Person[]> {

        String groupid = "";

        @Override
        protected Person[] doInBackground(String... params) {


            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = App.getFaceServiceClient();
            try{

                groupid = params[0];
                Log.d(TAG,groupid);
                return faceServiceClient.listPersons(params[0]);

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(Person[] result) {
            if (result != null) {
                Log.d(TAG, result.length + " S");
                if (result != null) {
                    for (Person person : result) {
                        try {
                            String name = URLDecoder.decode(person.name, "UTF-8");
                            Log.d(TAG, person.personId.toString() + "  " + name + "  " + groupid);
                            StorageHelper.setPersonName(person.personId.toString(), name, groupid, SettingPanel.this);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                MessageUtils.makeText(SettingPanel.this, "Không kết nối được dữ liệu nhận diện hình ảnh").show();
            }
            if (waitDiag.isShowing()) waitDiag.dismiss();
        }
    }

}
