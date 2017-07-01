package center.control.system.vash.controlcenter.configuration;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.device.DeviceEntity;

/**
 * Created by Sam on 6/29/2017.
 */

public class RecyclerViewDevicesTriggerAdapter extends RecyclerView.Adapter<RecyclerViewDevicesTriggerAdapter.ViewHolder>{
    private List<DeviceEntity> deviceEntities;
    private OnAdapterItemClickListener mListener;
    private int focused;
    View view;

    public RecyclerViewDevicesTriggerAdapter(List<DeviceEntity> items, OnAdapterItemClickListener listener) {
        this.deviceEntities = items;
        this.mListener = listener;
    }

    @Override
    public RecyclerViewDevicesTriggerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.condition_item, parent, false);
        return new RecyclerViewDevicesTriggerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewDevicesTriggerAdapter.ViewHolder holder, final int position) {
        holder.item = deviceEntities.get(position);
        holder.triggerName.setText(holder.item.getName());
        if (position == focused){
            holder.view.setBackgroundColor(view.getResources().getColor(R.color.nGreen2));

//            holder.view.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.view.setBackgroundResource(R.drawable.background_white);
        }
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
        return deviceEntities.size();
    }


    public void setDevices(List<DeviceEntity> devices) {
        this.deviceEntities = devices;
        this.notifyDataSetChanged();
    }

    public void remove(DeviceEntity deviceEntity) {
        this.deviceEntities.remove(deviceEntity);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView triggerName;
        public DeviceEntity item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            triggerName = (TextView) view.findViewById(R.id.txtConditionName);
        }

    }
    public long getItemId(int position) {
        return position;
    }

    public interface OnAdapterItemClickListener {
        public void onDeviceClick(DeviceEntity deviceEntity);
    }
}
