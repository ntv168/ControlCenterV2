package center.control.system.vash.controlcenter.configuration;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import center.control.system.vash.controlcenter.R;

/**
 * Created by Sam on 6/28/2017.
 */

public class RecyclerViewStateAdapter extends RecyclerView.Adapter<RecyclerViewStateAdapter.ViewHolder>{
    private List<StateEntity> stateEntities;
    private OnAdapterItemClickListener mListener;
    private int focused;
    View view;

    public RecyclerViewStateAdapter(List<StateEntity> items, OnAdapterItemClickListener listener) {
        this.stateEntities = items;
        this.mListener = listener;
    }

    @Override
    public RecyclerViewStateAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.condition_item, parent, false);
        return new RecyclerViewStateAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewStateAdapter.ViewHolder holder, final int position) {
        holder.item = stateEntities.get(position);
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
                    mListener.onStateClick(holder.item);
                }
                focused = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return stateEntities.size();
    }


    public void setDevices(List<StateEntity> devices) {
        this.stateEntities = devices;
        this.notifyDataSetChanged();
    }

    public void remove(StateEntity deviceEntity) {
        this.stateEntities.remove(deviceEntity);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView triggerName;
        public StateEntity item;

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
        public void onStateClick(StateEntity triggerEntity);
    }
}
