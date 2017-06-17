package center.control.system.vash.controlcenter.device;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.area.AreaAttributeAdapter;

/**
 * Created by Thuans on 4/7/2017.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    private static final String TAG = "DeviceAdapter";
    private List<DeviceEntity> deviceEntities = new ArrayList<DeviceEntity>();
    private DeviceItemClickListener listener;

    public DeviceAdapter(List<DeviceEntity> deviceEntities, DeviceItemClickListener listener) {
        this.deviceEntities = deviceEntities;
        this.listener = listener;
    }

    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DeviceAdapter.ViewHolder holder, final int position) {
        holder.deviceName.setText(deviceEntities.get(position).getName());
        holder.deviceStatus.setText(deviceEntities.get(position).getState());


        if (deviceEntities.get(position).getState().equals("Bật") ||
                deviceEntities.get(position).getState().equals("Mở")) {

            switch (deviceEntities.get(position).getType()) {
                case "light":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_light_active);
                    break;
                case "bell":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_bell_active);
                    break;
                case "door":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_door_active);
                    break;
                case "fan":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_fan_active);
                    break;
                case "camera":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_camera_active);
                    break;
                case "tivi":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_tivi_active);
                    break;
                case "airCondition":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_air_condition_active);
                    break;
                case "cooker":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_cooker_active);
                    break;
                case "curtain":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_curtain_active);
                    break;
                default:
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_default_active);
            }
        } else {
            switch (deviceEntities.get(position).getType()) {
                case "light":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_light);
                    break;
                case "bell":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_bell);
                    break;
                case "door":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_door);
                    break;
                case "fan":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_fan);
                    break;
                case "camera":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_camera);
                    break;
                case "tivi":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_tivi);
                    break;
                case "airCondition":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_air_condition);
                    break;
                case "cooker":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_cooker);
                    break;
                case "curtain":
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_curtain);
                    break;
                default:
                    holder.deviceIcon.setImageResource(R.drawable.ic_device_default);
            }
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeviceClick(deviceEntities.get(position));
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView deviceName;
        public final TextView deviceStatus;
        public final ImageView deviceIcon;
        public DeviceEntity item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            deviceName = (TextView) view.findViewById(R.id.deviceName);
            deviceStatus = (TextView) view.findViewById(R.id.deviceStatus);
            deviceIcon = (ImageView) view.findViewById(R.id.deviceIcon);
        }
    }

    public interface DeviceItemClickListener {
        public void onDeviceClick(DeviceEntity areaEntity);
    }

    @Override
    public int getItemCount() {
        return deviceEntities.size();
    }

    public long getItemId(int position) {
        return position;
    }
}
