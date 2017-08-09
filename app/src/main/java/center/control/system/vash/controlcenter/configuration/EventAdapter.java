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
import center.control.system.vash.controlcenter.area.AreaEntity;
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
//        Log.d("adapter event",eventEntities.size()+" "+position+" "+eventEntities.get(0));
        SmartHouse house = SmartHouse.getInstance();
        switch (holder.item.getSenName()){
            case "sec":
                holder.senName.setText("an ninh : "); break;
            case "cam":
                holder.senName.setText("camera : "); break;
            case "tem":
                holder.senName.setText("nhiệt độ : ");break;
        }
        switch (holder.item.getSenValue()){
            case AreaEntity.DETECT_AQUAINTANCE:
                holder.senVal.setText("người thân");break;
            case AreaEntity.DETECT_STRANGE:
                holder.senVal.setText("người lạ ");break;
            case AreaEntity.DOOR_CLOSE:
                holder.senVal.setText("cửa đóng");break;
            case AreaEntity.DETECT_BAD_GUY:
                holder.senVal.setText("kẻ xấu");break;
            case AreaEntity.FUME:
                holder.senVal.setText("có khói");break;
            case AreaEntity.DOOR_OPEN:
                holder.senVal.setText("cửa mở");break;
        }
        if (SmartHouse.getAreaById(holder.item.getAreaId()) != null)
        holder.btnAreaId.setText("ở " + SmartHouse.getAreaById(holder.item.getAreaId()).getName()); else
            holder.btnAreaId.setText("chọn không gian");
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
        public final Button btnAreaId;
        public EventEntity item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            senName = (TextView) view.findViewById(R.id.txtSenName);
            senVal = (TextView) view.findViewById(R.id.txtSenValue);
            btnAreaId = (Button) view.findViewById(R.id.btnAreaId);
        }

    }
}
