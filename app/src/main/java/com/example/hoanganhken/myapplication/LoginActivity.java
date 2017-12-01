package com.example.hoanganhken.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import de.greenrobot.event.EventBus;

/**
 * Created by Hoang Anh Ken on 12/1/2017.
 */

public class LoginActivity extends AppCompatActivity {
    EditText edtEmail;
    EditText edtPass;
    Button btnLogin;
    Button btnRegister;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_login);
        addControl();
        addEvents();
    }

    private void addEvents() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String pass = edtPass.getText().toString();
                if (email.isEmpty() || pass.isEmpty()) {
                } else {
                    Firebase authenticateUser = new Firebase(Constant.FIREBASE_CHAT_URL);
                    authenticateUser.authWithPassword(email, pass, new Firebase.AuthResultHandler() {

                        @Override
                        public void onAuthenticated(AuthData authData) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            Toast.makeText(LoginActivity.this, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));

            }
        });

    }


    private void addControl() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        EventBus.getDefault().register(this);
    }

    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEvent(String event) {
        if (event.equals(Constant.KEY_CLOSE)) {
            LoginActivity.this.finish();
        }
    }
}
