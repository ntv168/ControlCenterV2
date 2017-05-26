package center.control.system.vash.controlcenter.area;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.R;

/**
 * Created by Thuans on 4/7/2017.
 */

public class AreaAdapter extends RecyclerView.Adapter<AreaHolder> {
    private static final String TAG = "AreaAdapter";
    private List<AreaEntity> areaEntities = new ArrayList<AreaEntity>();
    private TextView twValue;

    public AreaAdapter(List<AreaEntity> areaEntities) {
        this.areaEntities = areaEntities;
    }

    @Override
    public AreaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.area_item, parent, false);
        AreaHolder holder = new AreaHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(AreaHolder holder, int position) {
        holder.name.setText(areaEntities.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return areaEntities.size();
    }

    public long getItemId(int position) {
        return position;
    }
}
