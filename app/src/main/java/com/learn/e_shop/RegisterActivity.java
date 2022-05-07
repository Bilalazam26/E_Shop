package com.learn.e_shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private Button register, verify_phone;
    private EditText input_name,input_password,input_phone,verification_code;
    private ProgressDialog progressDialog;
    private String randomCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);

        register = findViewById(R.id.sign_up_btn);
        input_name = findViewById(R.id.register_name);
        input_password = findViewById(R.id.register_password);
        input_phone = findViewById(R.id.register_phone);
        verification_code = findViewById(R.id.register_code);
        verify_phone = findViewById(R.id.verify_btn);
        verify_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int min = 1000;
                int max = 9999;
                int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
                randomCode = String.valueOf(random_int);
                Toast.makeText(RegisterActivity.this, "the code is "+randomCode, Toast.LENGTH_SHORT).show();
                verifyUserByPhone(randomCode, input_phone.getText().toString());

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();

            }
        });

    }

    private void verifyUserByPhone(String randomCode, String phone) {

        if (TextUtils.isEmpty(input_name.getText().toString())) {
            Toast.makeText(this, "Please enter your name...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter your phone...", Toast.LENGTH_SHORT).show();
        } else {

            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!(snapshot.child("Users").child(phone).exists())) {
                        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.SEND_SMS)
                                == PackageManager.PERMISSION_GRANTED) {

                            sendCode();


                        } else {

                            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.SEND_SMS}
                                    ,100);
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "This phone is already exist...", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


            sendCode();
        } else {
            Toast.makeText(RegisterActivity.this, "Permit Sending SMS", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendCode() {
        String name = input_name.getText().toString();
        String message = "Dear "+name+"\nThe Verification code for your 'E Shop' Account \nIs : "+randomCode;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(input_phone.getText().toString(), null, message, null, null);
        Toast.makeText(this, "The Verification Code sent Successfully", Toast.LENGTH_SHORT).show();
        register.setVisibility(View.VISIBLE);
        verify_phone.setVisibility(View.INVISIBLE);
    }

    private void createAccount() {
        String name = input_name.getText().toString();
        String phone = input_phone.getText().toString();
        String password = input_password.getText().toString();
        String code = verification_code.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your name...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter your phone...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "Please enter verification code...", Toast.LENGTH_SHORT).show();
        }else {

            if(code.equals(randomCode)) {
                progressDialog.setTitle("Creating your Account");
                progressDialog.setMessage("Please wait, While macking your Account ready.");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                validatePhone(name, phone, password);
            } else {
                Toast.makeText(this, "The verification code is not correct", Toast.LENGTH_SHORT).show();
            }


        }



    }

    private void validatePhone(String name, String phone, String password) {
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> userDataMap = new HashMap<>();
        userDataMap.put("phone", phone);
        userDataMap.put("password", password);
        userDataMap.put("name", name);

        myRef.child("Users").child(phone).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Your Account has been created", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "Error: Please try again later!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

}