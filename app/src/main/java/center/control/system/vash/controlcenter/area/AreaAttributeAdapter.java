package center.control.system.vash.controlcenter.area;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.R;

/**
 * Created by Thuans on 4/7/2017.
 */

public class AreaAttributeAdapter extends RecyclerView.Adapter<AreaAttributeAdapter.AreaAttributeHolder> {
    private static final String TAG = "AreaAdapter";
    private List<AreaAttribute> areaAttributes = new ArrayList<>();
    private AttributeClickListener listener;

    public void updateAttribute(String[] value) {
        Log.d(TAG,"update attriute "+value[0]);
        for (int i = 0; i< value.length; i++){
            this.areaAttributes.get(i).setValue(value[i]);
        }
        this.notifyDataSetChanged();
    }
    public AreaAttributeAdapter(List<AreaAttribute> areaAttributes, AttributeClickListener listener) {
        this.areaAttributes = areaAttributes;
        this.listener = listener;
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
        final AreaAttributeHolder holder = new AreaAttributeHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final AreaAttributeHolder holder, final int position) {
        for (int i = 0; i< AreaEntity.attrivutesValues.length; i++){
            if (AreaEntity.attrivutesValues[i].equals(areaAttributes.get(position).getName())){
                holder.name.setText(AreaEntity.attrivutes[i]);
                holder.icon.setImageResource(AreaEntity.attributeIcon[i]);
                break;
            }
        }
        if (areaAttributes.get(position).getValue() != null) {
            holder.value.setText(areaAttributes.get(position).getValue());
        }else  {
            holder.value.setText("Không khả dụng");
        }
    }
    public interface AttributeClickListener{
        public void onAttributeClick(AreaAttribute areaAttribute,int areaId);
    }
    public class AreaAttributeHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView value;
        public ImageView icon;
        public View view;
        public LinearLayout lin;

        public AreaAttributeHolder(View rowView) {
            super(rowView);
            this.view = rowView;
            value = (TextView) rowView.findViewById(R.id.txtAttributeValue);
            name = (TextView) rowView.findViewById(R.id.txtAttributeName);
            icon = (ImageView) rowView.findViewById(R.id.imgAttribute);
            lin = (LinearLayout) rowView.findViewById(R.id.linAreaAttribute);
        }
    }
}
