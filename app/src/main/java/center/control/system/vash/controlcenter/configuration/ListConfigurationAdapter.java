package center.control.system.vash.controlcenter.configuration;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.nfc.Tag;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;

import center.control.system.vash.controlcenter.R;


/**
 * Created by Sam on 6/27/2017.
 */

public class ListConfigurationAdapter extends ArrayAdapter<ConfigurationEntity> {
    List<ConfigurationEntity> configurationEntities;
    Dialog dialog;
    TriggerSQLite triggerSQLite = new TriggerSQLite();
    CommandSQLite commandSQLite = new CommandSQLite();

    public ListConfigurationAdapter(Context context, List<ConfigurationEntity> items) {
        super(context, 0);
        this.configurationEntities = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ConfigurationEntity entity = configurationEntities.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.configuration_item, parent, false);
        }
        // Lookup view for data population
        TextView txtName = (TextView) convertView.findViewById(R.id.config_name);
        Button btnCommand = (Button) convertView.findViewById(R.id.btnCommands);
        Button btnTrigger = (Button) convertView.findViewById(R.id.btnTrigger);
        // Populate the data into the template view using the data object
        txtName.setText(entity.getName());

        btnCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.condition_dialog);

                List<TriggerEntity> listTrigger = triggerSQLite.getAll();

                ListTriggersAdapter triggerAdapter = new ListTriggersAdapter(getContext(), listTrigger);
                ListView lwTrigger = (ListView) dialog.findViewById(R.id.lsCondition);
                lwTrigger.setAdapter(triggerAdapter);
                triggerAdapter.notifyDataSetChanged();
                Log.d("-------------------", "onClick: " + "-----------" );
                dialog.show();
            }
        });

        btnTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.scenario_dialog);

                List<CommandEntity> listCommand = commandSQLite.getAll();

                ListCommandAdapter commandAdapter = new ListCommandAdapter(getContext(), listCommand);
                ListView lwCommand = (ListView) dialog.findViewById(R.id.lsDevice);
                lwCommand.setAdapter(commandAdapter);
                dialog.show();
            }
        });



        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public int getCount() {
        return configurationEntities.size();
    }

    public long getItemId(int position) {
        return position;
    }
}
