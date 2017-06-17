package center.control.system.vash.controlcenter.area;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public AreaAdapter(List<AreaEntity> areaEntities, AreaClickListener listener) {
        this.areaEntities = areaEntities;
        this.listener = listener;
    }

    @Override
    public AreaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.area_item, parent, false);
        AreaHolder holder = new AreaHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AreaHolder holder, final int position) {
        holder.name.setText(areaEntities.get(position).getName());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAreaClick(areaEntities.get(position));
            }
        });
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

        public AreaHolder(View rowView) {
            super(rowView);
            view = rowView;
            name = (TextView) rowView.findViewById(R.id.txtAreaName);
        }
    }
}

