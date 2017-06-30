package center.control.system.vash.controlcenter.device;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.area.AreaSQLite;
import center.control.system.vash.controlcenter.area.ListAreaAdapter;
import center.control.system.vash.controlcenter.panel.VAPanel;
import center.control.system.vash.controlcenter.script.ScriptEntity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
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
    private android.app.AlertDialog.Builder editNickNameDiag;
    private Dialog deviceDialog;
    private EditText ipArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_device);
        RecyclerView lstAreaManage = (RecyclerView) findViewById(R.id.listItemLeft);
        lstAreaManage.setHasFixedSize(true);
        editNickNameDiag = new android.app.AlertDialog.Builder(ManageDeviceActivity.this);
        editNickNameDiag.setCancelable(false);
        LinearLayoutManager verticalLayout = new LinearLayoutManager(this);
        verticalLayout.setOrientation(LinearLayoutManager.VERTICAL);
        lstAreaManage.setLayoutManager(verticalLayout);
        TextView txtBack = (TextView) findViewById(R.id.txtBack);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
        deviceDialog.setContentView(R.layout.add_device_diaglog);

        Button btnAddArea = (Button) findViewById(R.id.btnAddArea);
        btnAddArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ipArea = (EditText) dialog.findViewById(R.id.txtAreaAddress);
                ipArea.setText("192.168.");
                final EditText areaName = (EditText) dialog.findViewById(R.id.txtAreaName);
                final EditText areaNickName= (EditText) dialog.findViewById(R.id.txtAreaNickname);
                final TextView txtErr  = (TextView) dialog.findViewById(R.id.txtError);

                Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (areaNickName.getText().length() > 2) {
                            final ProgressDialog progressDialog = new ProgressDialog(ManageDeviceActivity.this);
                            progressDialog.setTitle("Dò tìm thiết bị khu vực " + ipArea.getText().toString());
                            progressDialog.setMessage("Đợi tý nha");

                            final String url = "http://" + ipArea.getText().toString().trim() + "/get";
                            Log.d(TAG, url);

                            StringRequest connectAreaIP = new StringRequest(Request.Method.GET,
                                    url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            String encodedResp = VolleySingleton.fixEncodingUnicode(response);
                                            currentArea = saveArea(areaName.getText().toString(),
                                                    areaNickName.getText().toString(),
                                                    ipArea.getText().toString());
                                            insertDeviceByPort(encodedResp, currentArea.getId());
                                            progressDialog.dismiss();
                                            dialog.dismiss();
                                            refineNickNameTarget();
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    txtErr.setText("Không kết nối được thiết bị");
                                    if (url.equals("http://1.1.1.1:80/get")) {
                                        String encodedResp = "đèn bàn=db";
                                        currentArea = saveArea(areaName.getText().toString(),
                                                areaNickName.getText().toString(),
                                                ipArea.getText().toString());
                                        insertDeviceByPort(encodedResp, currentArea.getId());
                                        progressDialog.dismiss();
                                        dialog.dismiss();
                                    } else
                                        progressDialog.dismiss();
                                    refineNickNameTarget();
                                }
                            });
                            VolleySingleton.getInstance(ManageDeviceActivity.this).addToRequestQueue(connectAreaIP);
                            progressDialog.show();
                        } else {
                            txtErr.setText("Tên phụ phải nhiều hơn 2 ký tự");
                        }
                    }

                    private AreaEntity saveArea(String name, String nickName, String addrress) {
                        AreaEntity area = new AreaEntity();
                        area.setName(name);
                        area.setNickName(nickName);
                        area.setConnectAddress(addrress);
                        AreaSQLite sqLite = new AreaSQLite();
                        int areaId = sqLite.insert(area);
                        area.setId(areaId);
                        house.addArea(area);
                        areaAdapter.setAreas(house.getAreas());

                        return area;
                    }

                    private void insertDeviceByPort(String response, int areaId) {
                        String[] pairs = response.split(",");
                        DeviceSQLite sqLite = new DeviceSQLite();
                        for (String pair : pairs){
                            if (pair.length()>1) {
                                String[] ele = pair.split("=");
                                DeviceEntity device = new DeviceEntity();
                                device.setPort(ele[1]);
                                device.setName(ele[0]);
                                device.setAreaId(areaId);
                                int deviceId = sqLite.insert(device);
                                device.setId(deviceId);
                                house.addDevice(device);
                            }
                        }
                        devicesAdapter.setDevices(house.getDevicesByAreaId(areaId));
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
        devicesAdapter.setDevices(house.getDevicesByAreaId(areaEntity.getId()));
    }
    @Override
    public void onLongAreaClick(final AreaEntity areaEntity) {
        currentArea = areaEntity;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tùy chỉnh không gian "+areaEntity.getName());
        View areaUpdateview = this.getLayoutInflater().inflate(R.layout.dialog_area_edit, null);
        final EditText nickName = (EditText) areaUpdateview.findViewById(R.id.txtAreaNickname);
        final EditText areaAddress = (EditText) areaUpdateview.findViewById(R.id.txtAreaAddress);
        nickName.setText(currentArea.getNickName());
        areaAddress.setText(currentArea.getConnectAddress());

        builder.setView(areaUpdateview)
                // Add action buttons
                .setPositiveButton("Cập nhật", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int id) {

                        final ProgressDialog progressDialog = new ProgressDialog(ManageDeviceActivity.this);
                        progressDialog.setTitle("Dò tìm thiết bị khu vực "+areaAddress.getText().toString());
                        progressDialog.setMessage("Đợi tý nha");
                        String url = "http://"+areaAddress.getText().toString()+"/get";
                        Log.d(TAG,url);
                        StringRequest connectAreaIP = new StringRequest(Request.Method.GET,
                            url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                updateArea(areaEntity,nickName.getText().toString(),
                                        areaAddress.getText().toString());
                                renewDeviceByPort(response, currentArea.getId());
                                progressDialog.dismiss();
                                dialog.dismiss();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG,error.getMessage());
                            progressDialog.dismiss();
                            }
                        });
                        VolleySingleton.getInstance(ManageDeviceActivity.this).addToRequestQueue(connectAreaIP);
                        progressDialog.show();
                    }

                    private void renewDeviceByPort(String response, int id) {
                        String[] ports = response.split(",");
                        DeviceSQLite sqLite = new DeviceSQLite();
                        sqLite.deleteByAreaId(currentArea.getId());
                        SmartHouse house = SmartHouse.getInstance();
                        house.removeDeviceByArea(currentArea.getId());
                        for (String port : ports){
                            DeviceEntity device = new DeviceEntity();
                            device.setPort(port);
                            device.setAreaId(currentArea.getId());
                            device.setState("off");
                            int deviceId =sqLite.insert(device);
                            device.setId(deviceId);
                            house.addDevice(device);
                        }
                        devicesAdapter.setDevices(house.getDevicesByAreaId(currentArea.getId()));
                    }

                    private void updateArea(AreaEntity area, String nick, String addre) {
                        area.setNickName(nick);
                        area.setConnectAddress(addre);
                        AreaSQLite sqLite = new AreaSQLite();
                        sqLite.upAddressAndNickById(area.getId(),area);
                        SmartHouse house = SmartHouse.getInstance();
                        house.updateAreaById(area.getId(),area);
                        areaAdapter.setAreas(house.getAreas());
                    }
                })
                .setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SmartHouse house = SmartHouse.getInstance();
                        house.removeAreaAndItsDevice(currentArea.getId());
                        currentArea = null;
                        devicesAdapter.setDevices(new ArrayList<DeviceEntity>());
                        areaAdapter.setAreas(house.getAreas());
                    }
                });
        builder.show();
    }




    @Override
    public void onDeviceClick(DeviceEntity device) {
        currentDevice = device;
//        final ListView attCheckList = (ListView)deviceDialog.findViewById(R.id.lstDeviceAttribute);
        final GridView attCheckList = (GridView) deviceDialog.findViewById(R.id.lstDeviceAttribute);
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
        final TextView txtErr = (TextView) deviceDialog.findViewById(R.id.txtError);
        txtName.setText(currentDevice.getName()+"");
        txtNickName.setText(currentDevice.getNickName()+"");
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtNickName.getText().length()>2) {
                    SparseBooleanArray checkedType = attCheckList.getCheckedItemPositions();
                    String attributeType = "";
                    for (int i = 0; i < attCheckList.getCount(); i++) {
                        if (checkedType.get(i)) {
                            attributeType += AreaEntity.attrivutesValues[i] + ',';
                        }
                    }
                    Log.d(TAG, attributeType);
                    currentDevice.setName(txtName.getText().toString());
                    currentDevice.setNickName(txtNickName.getText().toString());
                    currentDevice.setAttributeType(attributeType);
                    currentDevice.setType(DeviceEntity.types[spnDeviceType.getSelectedItemPosition()]);
                    currentDevice.setAreaId(currentArea.getId());
                    currentDevice.setState("off");
                    SmartHouse house = SmartHouse.getInstance();
                    house.updateDeviceById(currentDevice.getId(), currentDevice);
                    DeviceSQLite.upById(currentDevice.getId(), currentDevice);
                    devicesAdapter.setDevices(house.getDevicesByAreaId(currentArea.getId()));
                    deviceDialog.dismiss();
                } else {
                    txtErr.setText("Tên phụ phải nhiều hơn 2 ký tự");
                }
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
//        pickImage();
    }

    @Override
    public void onDeviceLongClick(DeviceEntity deviceEntity) {
        DeviceSQLite.deleteByDevId(deviceEntity.getId());
        devicesAdapter.remove(deviceEntity);
    }
    private void refineNickNameTarget(){
        final SmartHouse house = SmartHouse.getInstance();
        for (final DeviceEntity device: house.getDevices()){
            if (device.getNickName() == null || device.getNickName().equals("")){
                editNickNameDiag.setTitle("Tên gọi khác cho thiết bị : "+device.getName());
                final EditText input = new EditText(ManageDeviceActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(device.getName()+" ");
                editNickNameDiag.setView(input);
                editNickNameDiag.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        device.setNickName(input.getText().toString());
                        DeviceSQLite.upById(device.getId(),device);
                        house.updateDeviceById(device.getId(),device);
                        dialog.dismiss();
                    }
                });
                editNickNameDiag.show();
            }
        }
    }
}
