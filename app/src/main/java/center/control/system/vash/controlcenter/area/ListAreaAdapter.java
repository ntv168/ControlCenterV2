package center.control.system.vash.controlcenter.area;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import center.control.system.vash.controlcenter.R;

import java.util.List;

public class ListAreaAdapter extends RecyclerView.Adapter<ListAreaAdapter.ViewHolder> {

    private final List<AreaEntity> areaEntities;
    private  OnAdapterItemClickListener mListener;

    public ListAreaAdapter(List<AreaEntity> items, OnAdapterItemClickListener listener) {
        areaEntities = items;
        mListener = listener;
    }
    public void addArea(AreaEntity areaEntity) {
        this.areaEntities.add(areaEntity);
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.area_config_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = areaEntities.get(position);
        holder.areaName.setText(holder.item.getName());
        holder.areaAddress.setText(holder.item.getConnectAddress());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                   mListener.onAreaClick(holder.item);
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return areaEntities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView areaName;
        public final TextView areaAddress;
        public AreaEntity item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            areaName = (TextView) view.findViewById(R.id.areaName);
            areaAddress = (TextView) view.findViewById(R.id.areaAddress);
        }

    }
    public interface OnAdapterItemClickListener {
        // TODO: Update argument type and name
        public void onAreaClick(AreaEntity areaEntity);
    }
}
