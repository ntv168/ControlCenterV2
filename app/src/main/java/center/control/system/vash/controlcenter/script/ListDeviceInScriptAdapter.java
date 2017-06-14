package center.control.system.vash.controlcenter.script;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.R;

public class ListDeviceInScriptAdapter extends RecyclerView.Adapter<ListDeviceInScriptAdapter.ViewHolder> {

    private List<ScriptDeviceEntity> scriptDeviceEntities;

    public List<ScriptDeviceEntity> getScriptDeviceEntities() {
        return scriptDeviceEntities;
    }

    public ListDeviceInScriptAdapter(List<ScriptDeviceEntity> scriptDevices) {
        scriptDeviceEntities= scriptDevices;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_script_config_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.item = scriptDeviceEntities.get(position);
        holder.name.setText(holder.item.getDeviceName());
        if (holder.item.getDeviceState().equals("on") || holder.item.getDeviceState().equals("open")) {
            holder.state.setChecked(true);
        } else
        if (holder.item.getDeviceState().equals("off") || holder.item.getDeviceState().equals("close")) {
            holder.state.setChecked(false);
        }

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeDevice(holder.item.getDeviceId());
            }
        });
    }

    private void removeDevice(int deviceId) {
        for (ScriptDeviceEntity device : scriptDeviceEntities){
            if (device.getDeviceId() == deviceId){
                scriptDeviceEntities.remove(device);
            }
        }
        this.notifyDataSetChanged();
    }
    public void addScripDev(ScriptDeviceEntity scriptDeviceEntity) {
        scriptDeviceEntities.add(scriptDeviceEntity);
        this.notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return scriptDeviceEntities.size();
    }

    public void setScriptEntities(List<ScriptDeviceEntity> scriptEntities) {
        this.scriptDeviceEntities = scriptEntities;
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView name;
        public final ImageButton btnRemove;
        public final Switch state;
        public ScriptDeviceEntity item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name = (TextView) view.findViewById(R.id.deviceName);
            state = (Switch) view.findViewById(R.id.deviceState);
            btnRemove = (ImageButton) view.findViewById(R.id.btnRemoveDevice);
        }

    }
}
