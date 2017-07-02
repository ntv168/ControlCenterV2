package center.control.system.vash.controlcenter.sensor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.R;

/**
 * Created by Sam on 6/30/2017.
 */

public class ListSensorAdapter extends ArrayAdapter<SensorEntity> {
    List<SensorEntity> sensorEntities;

    public ListSensorAdapter(Context context, List<SensorEntity> items) {
        super(context, 0);
        this.sensorEntities = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SensorEntity sensor = sensorEntities.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.sensor_item, parent, false);
        }
        // Lookup view for data population
        TextView txtName = (TextView) convertView.findViewById(R.id.txtSensorName);

        // Populate the data into the template view using the data object
        txtName.setText(sensor.getName());

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public int getCount() {
        return sensorEntities.size();
    }

    public long getItemId(int position) {
        return position;
    }
}
