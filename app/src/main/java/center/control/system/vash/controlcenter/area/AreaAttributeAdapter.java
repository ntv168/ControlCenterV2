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

public class AreaAttributeAdapter extends RecyclerView.Adapter<AreaAttributeHolder> {
    private static final String TAG = "AreaAdapter";
    private List<AreaAttribute> areaAttributes = new ArrayList<>();

    public void updateAttribute(String[] value) {
        for (int i = 0; i< AreaEntity.attrivutes.length; i++){
            this.areaAttributes.get(i).setValue(value[i]);
        }
        notifyDataSetChanged();
    }
    public void resetAttribute(){
        this.areaAttributes = new ArrayList<>();
        notifyDataSetChanged();
    }

    public AreaAttributeAdapter(List<AreaAttribute> areaAttributes) {
        this.areaAttributes = areaAttributes;
    }


    @Override
    public int getItemCount() {
        return areaAttributes.size();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public AreaAttributeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.area_attribute_item, parent, false);
        AreaAttributeHolder holder = new AreaAttributeHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AreaAttributeHolder holder, int position) {
        holder.name.setText(areaAttributes.get(position).getName());
        holder.value.setText(areaAttributes.get(position).getValue());
    }

}
