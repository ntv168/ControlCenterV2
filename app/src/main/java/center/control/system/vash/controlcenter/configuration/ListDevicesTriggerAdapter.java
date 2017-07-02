package center.control.system.vash.controlcenter.configuration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.device.DeviceEntity;

/**
 * Created by Sam on 6/29/2017.
 */

public class ListDevicesTriggerAdapter extends ArrayAdapter<DeviceEntity> {
    List<DeviceEntity> deviceEntities;

    public ListDevicesTriggerAdapter(Context context, List<DeviceEntity> items) {
        super(context, 0);
        this.deviceEntities = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DeviceEntity trigger = deviceEntities.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.condition_device_item, parent, false);
        }
        // Lookup view for data population
        TextView txtName = (TextView) convertView.findViewById(R.id.deviceName);

        // Populate the data into the template view using the data object
        txtName.setText(trigger.getName());

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public int getCount() {
        return deviceEntities.size();
    }

    public long getItemId(int position) {
        return position;
    }
}
