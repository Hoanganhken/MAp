package com.example.hoanganhken.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Date;

import de.greenrobot.event.EventBus;

/**
 * Created by Hoang Anh Ken on 12/1/2017.
 */

public class RegisterActivity extends AppCompatActivity {
    EditText edtName;
    EditText edtEmail;
    EditText edtPass;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        addControl();
        addEvents();
    }

    private void addControl() {
        btnRegister = findViewById(R.id.btnRegister);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
    }

    private void addEvents() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Firebase rootUrl = new Firebase(Constant.FIREBASE_CHAT_URL);
                final String userFirstName = edtName.getText().toString();
                final String userEmail = edtEmail.getText().toString();
                final String userPassword = edtPass.getText().toString();
                if (userFirstName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty()) {

                } else {
                    rootUrl.createUser(userEmail, userPassword, new Firebase.ResultHandler() {
                        @Override
                        public void onSuccess() {
                            rootUrl.authWithPassword(userEmail, userPassword, new Firebase.AuthResultHandler() {
                                @Override
                                public void onAuthenticated(AuthData authData) {
                                    long createTime = new Date().getTime();
                                    double lat = 0;
                                    double lng = 0;
                                    if (LocationService.lattitude != 0) {
                                        lat = LocationService.lattitude;
                                    }
                                    if (LocationService.longitude != 0) {
                                        lng = LocationService.longitude;
                                    }
                                    Log.d("", "" + lat);
                                    rootUrl.child(Constant.CHILD_USERS).child(authData.getUid()).setValue(new User(authData.getUid(), userFirstName, userEmail, Constant.KEY_ONLINE, String.valueOf(createTime), lat, lng), new Firebase.CompletionListener() {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                            EventBus.getDefault().post(Constant.KEY_CLOSE);
                                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    });
                                }

                                @Override
                                public void onAuthenticationError(FirebaseError firebaseError) {

                                }
                            });
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            Toast.makeText(RegisterActivity.this, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }
}