//Codded by Eng.Bilal Azzam
package com.learn.e_shop;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.e_shop.Model.User;
import com.learn.e_shop.Prevalent.Prevalent;

import java.io.Serializable;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button sign_up,sign_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sign_up = findViewById(R.id.sign_up_btn);
        sign_in = findViewById(R.id.sign_in_btn);


        Paper.init(this);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        String currentUserPhone = Paper.book().read(Prevalent.userPhoneKey);
        String currentUserPassword = Paper.book().read(Prevalent.userPasswordKey);
        String dbParentName = Paper.book().read("USERTYPE");
        if(currentUserPhone != null && currentUserPassword != null && dbParentName != null) {

            Authenticate(currentUserPhone, currentUserPassword, dbParentName);
        }
    }

    public void Authenticate(String phone, String password, String dbParentName) {
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(dbParentName).child(phone).exists()) {

                    User user = snapshot.child(dbParentName).child(phone).getValue(User.class);
                    if(user.getPassword().equals(password)) {

                        Toast.makeText(MainActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();

                        if (dbParentName.equals("Admin")) {
                            Intent intent = new Intent(MainActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);
                        } else if (dbParentName.equals("Users")) {
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.putExtra("user",  user);
                            Prevalent.currentUser = user;
                            startActivity(intent);
                        }

                    } else {
                        Toast.makeText(MainActivity.this, "Wrong password, Please try again!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(MainActivity.this, "There is no account with this phone number", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}