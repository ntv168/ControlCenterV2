package center.control.system.vash.controlcenter.script;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import center.control.system.vash.controlcenter.R;

public class ListScriptAdapter extends RecyclerView.Adapter<ListScriptAdapter.ViewHolder> {

    private List<ScriptEntity> scriptEntities;
    private  OnAdapterItemClickListener mListener;
    private int focusedItem = 0;

    public void addScrip(ScriptEntity script) {
        scriptEntities.add(script);
        this.notifyDataSetChanged();
    }

    public ListScriptAdapter(List<ScriptEntity> items, OnAdapterItemClickListener listener) {
        scriptEntities= items;
        mListener = listener;
        focusedItem = -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.script_config_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        holder.item = scriptEntities.get(position);
        holder.name.setText(holder.item.getName());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onScriptClick(holder.item);
                }
            }
        });
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null){
                    mListener.onLongScriptClick(holder.item);
                }
                return true;
            }
        });
      
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return scriptEntities.size();
    }

    public void setScriptEntities(List<ScriptEntity> scriptEntities) {
        this.scriptEntities = scriptEntities;
        this.notifyDataSetChanged();
    }

    public List<ScriptEntity> getScriptEntities() {
        return scriptEntities;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView name;
        public ScriptEntity item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name = (TextView) view.findViewById(R.id.scriptName);
        }

    }
    public interface OnAdapterItemClickListener {
        // TODO: Update argument type and name
        public void onScriptClick(ScriptEntity scriptEntity);
        public void onLongScriptClick(ScriptEntity scriptEntity);

        public void activaScript(ScriptEntity item);
    }
}
