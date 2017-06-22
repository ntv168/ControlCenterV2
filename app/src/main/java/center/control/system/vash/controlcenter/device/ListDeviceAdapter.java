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
    private int focused;

    public ListDeviceAdapter(List<DeviceEntity> items, OnAdapterItemClickListener listener) {
        deviceEnts = items;
        mListener = listener;
        focused = -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_config_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.item = deviceEnts.get(position);
        holder.deviceName.setText(holder.item.getName());
        holder.devicePort.setText(holder.item.getPort());
        if (position == focused){
            holder.view.setBackgroundColor(Color.GRAY);
        } else {
            holder.view.setBackgroundColor(Color.WHITE);
        }
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != mListener) {
                    mListener.onDeviceLongClick(holder.item);
                    deviceEnts.remove(holder.item);
                }
                return true;
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                   mListener.onDeviceClick(holder.item);
                }
                focused = position;
                notifyDataSetChanged();
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

    public void remove(DeviceEntity deviceEntity) {
        this.deviceEnts.remove(deviceEntity);
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
        public void onDeviceClick(DeviceEntity areaEntity);
        public void onDeviceLongClick(DeviceEntity deviceEntity);
    }
}
