package com.example.hoanganhken.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Hoang Anh Ken on 12/1/2017.
 */

public class ChatRoomActivity extends AppCompatActivity {
    ListView lvMessage;
    EditText edtMessage;
    Button btnSendMessage;
    TextView tvRecipientMessage;
    TextView tvSenderMessage;
    private ArrayList<Messages> arrMessage;
    private ChatAdapter chatAdapter;
    private User receiverUser;
    private User currenUser;
    private Firebase urlChatroom;
    private ChildEventListener childEventListenerMessage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_chat_room);
        arrMessage = new ArrayList<>();
        chatAdapter = new ChatAdapter(ChatRoomActivity.this,0,arrMessage);
        lvMessage.setAdapter(chatAdapter);
        String jsonReceiverUser = getIntent().getStringExtra(Constant.KEY_SEND_USER).split("---")[0];
        String jsonCurrenUser = getIntent().getStringExtra(Constant.KEY_SEND_USER).split("---")[1];
        Gson gson = new Gson();
        receiverUser = gson.fromJson(jsonReceiverUser, User.class);
        currenUser = gson.fromJson(jsonCurrenUser, User.class);
        long createReceiverUser = Long.parseLong(receiverUser.cratedAt);
        long createCurrenUser = Long.parseLong(currenUser.cratedAt);
        String roomName = "";
        if (createReceiverUser > createCurrenUser) {
            roomName = String.valueOf(createReceiverUser) + String.valueOf(createCurrenUser);
        } else {
            roomName = String.valueOf(createCurrenUser) + String.valueOf(createReceiverUser);
        }
        urlChatroom = new Firebase(Constant.FIREBASE_CHAT_URL).child(Constant.CHILD_CHAT).child(roomName);
        childEventListenerMessage = urlChatroom.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                arrMessage.add(messages);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        addControl();
        addEvents();
    }

    private void addEvents() {
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ms = edtMessage.getText().toString();
                if (!ms.isEmpty()) {
                    Messages messages = new Messages(currenUser.email, ms);
                    urlChatroom.push().setValue(messages);
                    edtMessage.setText("");
                }
            }
        });
    }

    private void addControl() {
        lvMessage = findViewById(R.id.lvMessage);
        edtMessage = findViewById(R.id.edtMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        tvRecipientMessage = findViewById(R.id.tvRecipientMessage);
        tvSenderMessage = findViewById(R.id.tvSenderMessage);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            urlChatroom.removeEventListener(childEventListenerMessage);
        } catch (Exception e) {
        }
    }




    public class ChatAdapter extends ArrayAdapter<Messages> {
        private Activity activity;
        private ArrayList<Messages> mArrMessage;


        public ChatAdapter(Activity activity, int resource, ArrayList<Messages> mArrMessage) {
            super(activity, resource, mArrMessage);
            this.activity = activity;
            this.mArrMessage = mArrMessage;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = activity.getLayoutInflater();
                convertView = layoutInflater.inflate(R.layout.item_list_message, null);
            }
            if (mArrMessage.get(position).emailUser.equals(currenUser.email)) {
                tvSenderMessage.setText(mArrMessage.get(position).message);
                tvSenderMessage.setVisibility(View.VISIBLE);
                tvRecipientMessage.setVisibility(View.GONE);
            } else {
                tvRecipientMessage.setText(mArrMessage.get(position).message);
                tvSenderMessage.setVisibility(View.GONE);
                tvRecipientMessage.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }
}
