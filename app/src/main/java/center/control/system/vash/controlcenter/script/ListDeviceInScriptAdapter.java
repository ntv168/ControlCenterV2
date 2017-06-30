package center.control.system.vash.controlcenter.script;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.command.CommandEntity;
import center.control.system.vash.controlcenter.utils.SmartHouse;

public class ListDeviceInScriptAdapter extends RecyclerView.Adapter<ListDeviceInScriptAdapter.ViewHolder> {

    private List<CommandEntity> scriptDeviceEntities;

    public List<CommandEntity> getScriptDeviceEntities() {
        return scriptDeviceEntities;
    }

    public ListDeviceInScriptAdapter(List<CommandEntity> scriptDevices) {
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
        SmartHouse house = SmartHouse.getInstance();
        holder.name.setText(house.getDeviceById(scriptDeviceEntities.get(position).getDeviceId()).getName());
        if (holder.item.getDeviceState().equals("on") || holder.item.getDeviceState().equals("open")) {
            holder.state.setChecked(true);
        } else {
            holder.state.setChecked(false);
        }
        holder.state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.item.setDeviceState("on");
                } else {
                    holder.item.setDeviceState("off");
                }
            }
        });

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scriptDeviceEntities.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    public void addScripDev(CommandEntity commandEntity) {
        scriptDeviceEntities.add(commandEntity);
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

    public void setScriptEntities(List<CommandEntity> scriptEntities) {
        this.scriptDeviceEntities = scriptEntities;
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView name;
        public final ImageButton btnRemove;
        public final Switch state;
        public CommandEntity item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name = (TextView) view.findViewById(R.id.deviceName);
            state = (Switch) view.findViewById(R.id.deviceState);
            btnRemove = (ImageButton) view.findViewById(R.id.btnRemoveDevice);
        }

    }
}
