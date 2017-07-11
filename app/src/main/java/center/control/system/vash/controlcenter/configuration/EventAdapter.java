package center.control.system.vash.controlcenter.configuration;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.utils.SmartHouse;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<EventEntity> eventEntities;
    private EventListener eventListener;


    public List<EventEntity> getEventEntities() {
        return eventEntities;
    }

    public EventAdapter(List<EventEntity> eventEntities,EventListener eventListener) {
        this.eventEntities= eventEntities;
        this.eventListener = eventListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_config_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.item = eventEntities.get(position);
        Log.d("adapter event",eventEntities.size()+" "+position+" "+eventEntities.get(0));
        SmartHouse house = SmartHouse.getInstance();
        holder.nextState.setText(house.getStateById(holder.item.getNextStateId()).getName());
        holder.senVal.setText(holder.item.getSenValue());
        holder.senName.setText(holder.item.getSenName());
        holder.priorirty.setText(holder.item.getPriority()+"");
        holder.btnAreaId.setText(holder.item.getAreaId()+"");
        holder.btnAreaId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventListener!=null){
                    eventListener.onClick(holder.item.getId());
                }
            }
        });

    }

    public void updateAreaId(int eventId, int areaId) {
        for (EventEntity ev: eventEntities){
            if (ev.getId() == eventId){
                ev.setAreaId(areaId);
                this.notifyDataSetChanged();
                return;
            }
        }
    }

    public interface EventListener{
        public void onClick(int id);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return eventEntities.size();
    }

    public void setScriptEntities(List<EventEntity> scriptEntities) {
        this.eventEntities = scriptEntities;
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView senName;
        public final TextView senVal;
        public final TextView nextState;
        public final TextView priorirty;
        public final Button btnAreaId;
        public EventEntity item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            senName = (TextView) view.findViewById(R.id.txtSenName);
            senVal = (TextView) view.findViewById(R.id.txtSenValue);
            priorirty = (TextView) view.findViewById(R.id.txtPriority);
            nextState = (TextView) view.findViewById(R.id.txtNextStateName);
            btnAreaId = (Button) view.findViewById(R.id.btnAreaId);
        }

    }
}
