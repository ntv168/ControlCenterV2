package center.control.system.vash.controlcenter.configuration;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import center.control.system.vash.controlcenter.R;

/**
 * Created by Sam on 6/27/2017.
 */

public class ListCommandAdapter extends ArrayAdapter<CommandEntity> {
    List<CommandEntity> commandEntityList;

    public ListCommandAdapter(Context context, List<CommandEntity> items) {
        super(context, 0);
        this.commandEntityList = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CommandEntity command = commandEntityList.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.scenario_item, parent, false);
        }
        // Lookup view for data population
        TextView txtName = (TextView) convertView.findViewById(R.id.txtDeviceName);


        // Populate the data into the template view using the data object
        txtName.setText(command.getDeviceName());

        Log.d("---------", "getView: " + command.getDeviceName());
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public int getCount() {
        return commandEntityList.size();
    }

    public long getItemId(int position) {
        return position;
    }
}
