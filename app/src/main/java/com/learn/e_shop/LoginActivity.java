package com.learn.e_shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.e_shop.Model.User;
import com.learn.e_shop.Prevalent.Prevalent;
import com.rey.material.widget.CheckBox;

import java.io.Serializable;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button logIn;
    private EditText phone_input, password_input;
    private CheckBox rememberMe;
    private TextView adminLink, notAdminLink;
    private String dbParentName = "Users";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logIn = findViewById(R.id.sign_in_btn);
        phone_input = findViewById(R.id.input_phone);
        password_input = findViewById(R.id.input_password);
        rememberMe = findViewById(R.id.remember_me_chk);

        adminLink = findViewById(R.id.admin_panel_link);
        notAdminLink = findViewById(R.id.not_admin_panel_link);

        Paper.init(this);


        progressDialog = new ProgressDialog(this);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInUser();
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logIn.setText("Login Admin");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                dbParentName = "Admin";
            }
        });

        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn.setText("Login");
                notAdminLink.setVisibility(View.INVISIBLE);
                adminLink.setVisibility(View.VISIBLE);
                dbParentName = "Users";
            }
        });
    }

    private void logInUser() {
        String phone = phone_input.getText().toString();
        String password = password_input.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter your phone number...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password...", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Login Account  ");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            Authenticate(phone, password);


        }
    }

    public void Authenticate(String phone, String password) {
        if (rememberMe.isChecked()) {
            Paper.book().write(Prevalent.userPhoneKey, phone);
            Paper.book().write(Prevalent.userPasswordKey, password);
            Paper.book().write("USERTYPE", dbParentName);
        }
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(dbParentName).child(phone).exists()) {

                    User user = snapshot.child(dbParentName).child(phone).getValue(User.class);
                    if(user.getPassword().equals(password)) {
                        if (dbParentName.equals("Users")) {
                            Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("user", (Serializable) user);
                            Prevalent.currentUser = user;
                            startActivity(intent);
                        } else if (dbParentName.equals("Admin")) {
                            Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                            intent.putExtra("user", (Serializable) user);
                            startActivity(intent);
                        }


                    } else {
                        Toast.makeText(LoginActivity.this, "Wrong password, Please try again!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "There is no account with this phone number", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}