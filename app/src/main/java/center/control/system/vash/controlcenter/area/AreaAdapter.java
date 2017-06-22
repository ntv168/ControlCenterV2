package center.control.system.vash.controlcenter.area;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.R;

/**
 * Created by Thuans on 4/7/2017.
 */

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.AreaHolder> {
    private static final String TAG = "AreaAdapter";
    private List<AreaEntity> areaEntities = new ArrayList<AreaEntity>();
    private AreaClickListener listener;
    View view;
    private int focusedItem = 0;

    public AreaAdapter(List<AreaEntity> areaEntities, AreaClickListener listener) {
        this.areaEntities = areaEntities;
        this.listener = listener;
        focusedItem = -1;
    }

    @Override
    public AreaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.area_item, parent, false);
        AreaHolder holder = new AreaHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final AreaHolder holder, final int position) {
        Log.d(TAG, areaEntities.get(position).getName() + " bind");
        Log.d(TAG, "Focus item: " + focusedItem);
        holder.name.setText(areaEntities.get(position).getName());
        if (position == focusedItem){
            holder.linArea.setBackgroundResource(R.drawable.background_area_active);
            holder.name.setTextColor(Color.WHITE);
        } else {
            holder.linArea.setBackgroundResource(R.drawable.background_none);
            holder.name.setTextColor(view.getResources().getColor(R.color.nGreen1));
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                resetBackgroundAllArea(holder);
//                holder.linArea.setBackgroundResource(R.drawable.background_area_active);
//                holder.name.setTextColor(Color.WHITE);
                listener.onAreaClick(areaEntities.get(position));
                focusedItem = position;
                notifyDataSetChanged();
            }
        });
    }

    public void resetBackgroundAllArea(AreaHolder holder) {
        for(AreaEntity areaEntity : areaEntities) {
            holder.linArea.setBackgroundResource(R.drawable.background_none);
            holder.name.setTextColor(view.getResources().getColor(R.color.nGreen1));
        }
    }

    @Override
    public int getItemCount() {
        return areaEntities.size();
    }

    public long getItemId(int position) {
        return position;
    }

    public interface AreaClickListener{
        public void onAreaClick(AreaEntity area);
    }
    public class AreaHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public View view;
        public LinearLayout linArea;
        public AreaEntity item;

        public AreaHolder(View rowView) {
            super(rowView);
            view = rowView;
            name = (TextView) rowView.findViewById(R.id.txtAreaName);
            linArea = (LinearLayout) rowView.findViewById(R.id.linArea);
        }
    }
}

