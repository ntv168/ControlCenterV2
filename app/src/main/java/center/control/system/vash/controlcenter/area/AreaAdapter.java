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

public class AreaAdapter extends RecyclerView.Adapter<AreaHolder> {
    private static final String TAG = "AreaAdapter";
    private List<AreaEntity> areaEntities = new ArrayList<AreaEntity>();
    private  AreaAttributeAdapter areaAttributeAdapter;

    public AreaAdapter(List<AreaEntity> areaEntities, AreaAttributeAdapter areaAttributeAdapter) {
        this.areaEntities = areaEntities;
        this.areaAttributeAdapter = areaAttributeAdapter;
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
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                areaAttributeAdapter.updateAttribute(areaEntities.get(position).generateValueArr());
//                Log.d(TAG," click ip");
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
}
