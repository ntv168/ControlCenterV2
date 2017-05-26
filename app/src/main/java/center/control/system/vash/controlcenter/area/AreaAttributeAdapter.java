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

    public AreaAttributeAdapter() {
        this.areaAttributes = new ArrayList<>();
        for (String name : AreaEntity.attrivutes){
            AreaAttribute attribute = new AreaAttribute();
            attribute.setName(name);
            attribute.setValue("cập nhật...");
            this.areaAttributes.add(attribute);
        }
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
    }

    @Override
    public int getItemCount() {
        return areaAttributes.size();
    }

    public long getItemId(int position) {
        return position;
    }
}
