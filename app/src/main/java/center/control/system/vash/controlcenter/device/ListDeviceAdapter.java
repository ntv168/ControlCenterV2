package center.control.system.vash.controlcenter.device;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.area.AreaEntity;

public class ListDeviceAdapter extends RecyclerView.Adapter<ListDeviceAdapter.ViewHolder> {

    private List<DeviceEntity> deviceEnts;
    private  OnAdapterItemClickListener mListener;

    public ListDeviceAdapter(List<DeviceEntity> items, OnAdapterItemClickListener listener) {
        deviceEnts = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_config_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = deviceEnts.get(position);
        holder.deviceName.setText(holder.item.getName());
        holder.devicePort.setText(holder.item.getPort());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    holder.deviceName.setBackgroundColor(Color.GREEN);
                   mListener.onDeviceClick(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceEnts.size();
    }


    public void setDevices(List<DeviceEntity> devices) {
        this.deviceEnts = devices;
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView deviceName;
        public final TextView devicePort;
        public DeviceEntity item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            deviceName = (TextView) view.findViewById(R.id.deviceName);
            devicePort = (TextView) view.findViewById(R.id.devicePort);
        }

    }
    public long getItemId(int position) {
        return position;
    }

    public interface OnAdapterItemClickListener {
        // TODO: Update argument type and name
        public void onDeviceClick(DeviceEntity areaEntity);
    }
}
