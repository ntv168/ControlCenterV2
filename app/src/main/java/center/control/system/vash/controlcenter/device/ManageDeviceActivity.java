package center.control.system.vash.controlcenter.device;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner; 
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.area.AreaSQLite;
import center.control.system.vash.controlcenter.area.ListAreaAdapter;
import center.control.system.vash.controlcenter.server.VolleySingleton;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.SmartHouse;

public class ManageDeviceActivity extends AppCompatActivity implements ListAreaAdapter.OnAdapterItemClickListener,
        ListDeviceAdapter.OnAdapterItemClickListener{
    private static final String TAG = "Manage Device Activity";
    private static final int PICK_PHOTO_FOR_DEVICE = 113;
    private ListDeviceAdapter devicesAdapter;
    private ListAreaAdapter areaAdapter;
    private DeviceEntity currentDevice;
    private AreaEntity currentArea;

    private Dialog deviceDialog;
    private EditText ipArea;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_device);
        RecyclerView lstAreaManage = (RecyclerView) findViewById(R.id.listItemLeft);
        lstAreaManage.setHasFixedSize(true);
        LinearLayoutManager verticalLayout = new LinearLayoutManager(this);
        verticalLayout.setOrientation(LinearLayoutManager.VERTICAL);
        lstAreaManage.setLayoutManager(verticalLayout);

        final SmartHouse house = SmartHouse.getInstance();
        areaAdapter = new ListAreaAdapter(house.getAreas(), this);
        lstAreaManage.setAdapter(areaAdapter);

        RecyclerView  lstDevice = (RecyclerView) findViewById(R.id.listItemRight);
        lstDevice.setHasFixedSize(true);
        LinearLayoutManager verticalLayout2 = new LinearLayoutManager(this);
        verticalLayout2.setOrientation(LinearLayoutManager.VERTICAL);
        lstDevice.setLayoutManager(verticalLayout2);
        devicesAdapter = new ListDeviceAdapter(new ArrayList<DeviceEntity>(), this);
        lstDevice.setAdapter(devicesAdapter);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_area_diaglog);

        deviceDialog = new Dialog(this);
        deviceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deviceDialog.setContentView(R.layout.edit_device_diaglog);

        Button btnAddArea = (Button) findViewById(R.id.btnAddArea);
        btnAddArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ipArea = (EditText) dialog.findViewById(R.id.txtAreaAddress);
                final EditText areaName = (EditText) dialog.findViewById(R.id.txtAreaName);
                final EditText areaNickName= (EditText) dialog.findViewById(R.id.txtAreaNickname);

                Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentArea = saveArea(areaName.getText().toString(),
                                areaNickName.getText().toString(),
                                ipArea.getText().toString());
                        StringRequest connectAreaIP = new StringRequest(Request.Method.GET,
                                "http://"+currentArea.getConnectAddress(), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                insertDeviceByPort(response, currentArea.getId());
                                dialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String response = "9,12,5,A2,A0";
                                insertDeviceByPort(response, currentArea.getId());
                                dialog.dismiss();
                            }
                        });
                        VolleySingleton.getInstance(ManageDeviceActivity.this).addToRequestQueue(connectAreaIP);
                    }

                    private AreaEntity saveArea(String name, String nickName, String addrress) {
                        AreaEntity area = new AreaEntity();
                        area.setName(name);
                        area.setNickName(nickName);
                        area.setConnectAddress(addrress);
                        AreaSQLite sqLite = new AreaSQLite();
                        int areaId = sqLite.insert(area);
                        area.setId(areaId);
                        house.getAreas().add(area);
                        areaAdapter.addArea(area);

                        return area;
                    }

                    private void insertDeviceByPort(String response, int areaId) {
                        String[] ports = response.split(",");
                        DeviceSQLite sqLite = new DeviceSQLite();
                        for (String port : ports){
                            DeviceEntity device = new DeviceEntity();
                            device.setPort(port);
                            device.setAreaId(areaId);
                            int deviceId =sqLite.insert(device);
                            device.setId(deviceId);
                            house.getDevices().add(device);
                        }
                        devicesAdapter.setDevicesByAreaId(areaId, house.getDevices());
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

    @Override
    public void onAreaClick(AreaEntity areaEntity) {
        SmartHouse house = SmartHouse.getInstance();
        currentArea = areaEntity;
        devicesAdapter.setDevicesByAreaId(areaEntity.getId(),house.getDevices());
    }
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_DEVICE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_DEVICE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(this,"Vui long chon hinh",Toast.LENGTH_SHORT);
                return;
            } else {
                Bitmap deviceIcon = null;
                try {
                    InputStream is  = this.getContentResolver().openInputStream(data.getData());
                    Bitmap tmp = BitmapFactory.decodeStream(is);
                    File myDir = new File(Environment.getExternalStorageDirectory() + ConstManager.ICON_FOLDER);
                    myDir.mkdirs();
                    File file = new File(myDir, currentDevice.getId() +".png");
                    FileOutputStream fos = new FileOutputStream(file);
                    deviceIcon = Bitmap.createScaledBitmap(tmp,100,100,true);
                    deviceIcon.compress(Bitmap.CompressFormat.PNG, 90, fos);
                    fos.close();
                    Log.d(TAG,"Saved icon"+file.getCanonicalPath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final ListView attCheckList = (ListView)deviceDialog.findViewById(R.id.lstDeviceAttribute);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (ManageDeviceActivity.this,
                                android.R.layout.simple_list_item_multiple_choice,
                                android.R.id.text1, AreaEntity.attrivutes);
                attCheckList.setAdapter(adapter);
                final Spinner spnDeviceType = (Spinner) deviceDialog.findViewById(R.id.spnDeviceType);
                ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, DeviceEntity.typeNames);
                typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnDeviceType.setAdapter(typeAdapter);
                Button btnSave = (Button) deviceDialog.findViewById(R.id.btnSave);
                final EditText txtName = (EditText) deviceDialog.findViewById(R.id.txtDeviceName);
                final EditText txtNickName = (EditText) deviceDialog.findViewById(R.id.txtDeviceNickname);
                ((ImageView) deviceDialog.findViewById(R.id.imgDeviceIcon)).setImageBitmap(deviceIcon);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SparseBooleanArray checkedType = attCheckList.getCheckedItemPositions();
                        String attributeType = "";
                        for (int i=0; i< checkedType.size(); i++){
                            if (checkedType.get(i)){
                                attributeType += AreaEntity.attrivutes[checkedType.keyAt(i)]+',';
                            }
                        }
                        DeviceEntity device = currentDevice;
                        device.setName(txtName.getText().toString());
                        device.setNickName(txtNickName.getText().toString());
                        device.setAttributeType(attributeType);
                        device.setType(DeviceEntity.types[spnDeviceType.getSelectedItemPosition()]);
                        device.setAreaId(currentArea.getId());
                        device.setIconId(currentDevice.getId() +".png");
                        SmartHouse house = SmartHouse.getInstance();
                        house.updateDeviceById(currentDevice.getId(),device);
                        DeviceSQLite sqLite = new DeviceSQLite();
                        sqLite.upById(device.getId(),device);
                        devicesAdapter.setDevicesByAreaId(currentArea.getId(),house.getDevices());
                        deviceDialog.dismiss();
                    }
                });
                Button btnCancel = (Button) deviceDialog.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deviceDialog.dismiss();
                    }
                });
                deviceDialog.show();
            }
        }
    }

    @Override
    public void onDeviceClick(DeviceEntity device) {
        currentDevice = device;
        pickImage();
    }
}
