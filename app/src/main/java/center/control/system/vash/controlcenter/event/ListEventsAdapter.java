package center.control.system.vash.controlcenter.event;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.configuration.EventEntity;
import center.control.system.vash.controlcenter.utils.SmartHouse;

/**
 * Created by Sam on 6/27/2017.
 */

public class ListEventsAdapter extends ArrayAdapter<EventEntity> {
    private static final String TAG = "ListEventsAdapter: ";
    private List<EventEntity> eventEntities;
    private List<AreaEntity> areas;
    private SmartHouse house = SmartHouse.getInstance();
    private Dialog dialog;

    public ListEventsAdapter(Context context, List<EventEntity> items) {
        super(context, 0);
        this.eventEntities = items;
    }

    public List<EventEntity> getEventEntities() {
        return eventEntities;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final EventEntity event = eventEntities.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.condition_item, parent, false);
        }
        // Lookup view for data population
        TextView txtName = (TextView) convertView.findViewById(R.id.txtConditionName);
        final Spinner listArea = (Spinner) convertView.findViewById(R.id.spin_arena);

        // Populate the data into the template view using the data object
        txtName.setText(event.getName());
        listArea.setAdapter(house.getAreaNameAdapter(getContext()));
        if (event.getAreaId() != 0) {
            listArea.setSelection(event.getAreaId());
        }

        listArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                event.setAreaId(house.getAreas().get(position).getId());
                Toast.makeText(getContext(), house.getAreas().get(position).getId() + "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public int getCount() {
        return eventEntities.size();
    }

    public long getItemId(int position) {
        return position;
    }
}
