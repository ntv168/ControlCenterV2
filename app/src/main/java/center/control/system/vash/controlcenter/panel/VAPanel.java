package center.control.system.vash.controlcenter.panel;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.nlp.ChatAdapter;

public class VAPanel extends AppCompatActivity {
    private Button btnSend;
    private ChatAdapter chatAdapter;
    private ListView chatList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vapanel);
        ImageButton currentTab = (ImageButton) findViewById(R.id.tabBtnVA);
        currentTab.setImageResource(R.drawable.tab_voice_active);
        currentTab.setBackgroundColor(Color.WHITE);

        btnSend = (Button) findViewById(R.id.btnChatSend);

        chatList = (ListView) findViewById(R.id.lstMsgChat);

        chatAdapter = new ChatAdapter(this, R.layout.msg_right);
        chatList.setAdapter(chatAdapter);

        final EditText chatText = (EditText) findViewById(R.id.txtHumanChat);

        chatText.setText("");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                chatAdapter.add(new ChatAdapter.ViewHolder(false, chatText.getText().toString()));
            }
        });

        chatList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatList.setAdapter(chatAdapter);

        //to scroll the list view to bottom on data change
        chatAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                chatList.setSelection(chatAdapter.getCount() - 1);
            }
        });
    }


    public void clicktoControlPanel(View view) {
        startActivity(new Intent(this, ControlPanel.class));
    }

    public void clicktoModePanel(View view) {
        startActivity(new Intent(this, ModePanel.class));
    }

    public void clicktoSettingPanel(View view) {
        startActivity(new Intent(this, SettingPanel.class));
    }

    public void clicktoVAPanel(View view) {
        startActivity(new Intent(this, VAPanel.class));
    }
}
