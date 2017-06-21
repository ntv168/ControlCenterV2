package center.control.system.vash.controlcenter.nlp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.R;

/**
 * Created by Thuans on 6/21/2017.
 */

public class ChatAdapter extends ArrayAdapter<ChatAdapter.ViewHolder> {
    private static final String TAG = "ChatArrayAdapter";

    private TextView chatText;
    private List<ViewHolder> chatMessageList = new ArrayList<>();
    @Override
    public void add(ChatAdapter.ViewHolder object) {
        chatMessageList.add(object);
        super.add(object);
    }
    public ChatAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ViewHolder getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.isLeft()) {
            row = inflater.inflate(R.layout.msg_left, parent, false);
        }else{
            row = inflater.inflate(R.layout.msg_right, parent, false);
        }
        chatText = (TextView) row.findViewById(R.id.msg);
        chatText.setText(chatMessageObj.getMessage());
        return row;
    }
    public static class ViewHolder {
        public  View view;
        private boolean left;
        private String message;

        public ViewHolder(boolean left, String message) {
            super();
            this.setLeft(left);
            this.setMessage(message);
        }

        public boolean isLeft() {
            return left;
        }

        public void setLeft(boolean left) {
            this.left = left;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}
