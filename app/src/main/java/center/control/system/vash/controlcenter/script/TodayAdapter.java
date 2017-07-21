package center.control.system.vash.controlcenter.script;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.List;

import center.control.system.vash.controlcenter.R;

public class TodayAdapter extends RecyclerView.Adapter<TodayAdapter.ViewHolder> {

    private List<ScriptEntity> scriptEntities;
    private Context context;

    public TodayAdapter(List<ScriptEntity> items, Context context) {
        scriptEntities= items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.today_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        holder.item = scriptEntities.get(position);
        holder.name.setText(holder.item.getName());
        holder.time.setText(holder.item.getHour()+":"+holder.item.getMinute());

            holder.swtActive.setChecked(holder.item.isEnabled());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(holder.item, TodayAdapter.this);
            }
        });
        holder.swtActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                holder.item.setEnabled(isChecked);
                updateModeById(holder.item);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return scriptEntities.size();
    }

    public void setScriptEntities(List<ScriptEntity> scriptEntities) {
        this.scriptEntities = scriptEntities;
        this.notifyDataSetChanged();
    }

    public List<ScriptEntity> getScriptEntities() {
        return scriptEntities;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView name;
        public final TextView time;
        public final Switch swtActive;
        public ScriptEntity item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name = (TextView) view.findViewById(R.id.scriptName);
            time  = (TextView) view.findViewById(R.id.txtTime);
            swtActive = (Switch) view.findViewById(R.id.swtActive);
        }
    }
    public void showTimePickerDialog(final ScriptEntity mode, final TodayAdapter adapter)
    {
        TimePickerDialog.OnTimeSetListener callback=new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view,
                                  int hourOfDay, int minute) {
                mode.setHour(hourOfDay);
                mode.setMinute(minute);
                adapter.updateModeById(mode);
            }
        };

        int gio=mode.getHour();
        int phut=mode.getMinute();
        TimePickerDialog time=new TimePickerDialog(
                context,
                callback, gio, phut, true);
        time.setTitle("Hẹn giờ");
        time.show();
    }

    private void updateModeById(ScriptEntity mode) {
        for (ScriptEntity script: this.scriptEntities){
            if (script.getId() == mode.getId()){
                script.setEnabled(mode.isEnabled());
                script.setHour(mode.getHour());
                script.setMinute(mode.getMinute());

            }
        }
        notifyDataSetChanged();
    }
}
