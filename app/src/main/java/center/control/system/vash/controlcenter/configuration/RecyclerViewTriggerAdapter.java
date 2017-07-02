package center.control.system.vash.controlcenter.configuration;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.trigger.TriggerEntity;

/**
 * Created by Sam on 6/28/2017.
 */

public class RecyclerViewTriggerAdapter extends RecyclerView.Adapter<RecyclerViewTriggerAdapter.ViewHolder>{
    private List<TriggerEntity> triggerEntities;
    private OnAdapterItemClickListener mListener;
    private int focused;
    View view;

    public RecyclerViewTriggerAdapter(List<TriggerEntity> items, OnAdapterItemClickListener listener) {
        this.triggerEntities = items;
        this.mListener = listener;
    }

    @Override
    public RecyclerViewTriggerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.condition_item, parent, false);
        return new RecyclerViewTriggerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewTriggerAdapter.ViewHolder holder, final int position) {
        holder.item = triggerEntities.get(position);
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
                    mListener.onTriggerClick(holder.item);
                }
                focused = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return triggerEntities.size();
    }


    public void setDevices(List<TriggerEntity> devices) {
        this.triggerEntities = devices;
        this.notifyDataSetChanged();
    }

    public void remove(TriggerEntity deviceEntity) {
        this.triggerEntities.remove(deviceEntity);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView triggerName;
        public TriggerEntity item;

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
        public void onTriggerClick(TriggerEntity triggerEntity);
    }
}
