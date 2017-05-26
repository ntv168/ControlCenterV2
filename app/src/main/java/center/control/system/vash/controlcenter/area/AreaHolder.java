package center.control.system.vash.controlcenter.area;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import center.control.system.vash.controlcenter.R;


/**
 * Created by Sam on 4/13/2017.
 */

public class AreaHolder extends RecyclerView.ViewHolder{
    public TextView name;

    public AreaHolder(View rowView) {
        super(rowView);
        name = (TextView) rowView.findViewById(R.id.txtAreaName);
    }
}
