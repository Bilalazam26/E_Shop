package com.learn.e_shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.learn.e_shop.Model.Cart;
import com.learn.e_shop.Model.User;
import com.learn.e_shop.Prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText name_edtxt, phone_edtxt, city_edtxt, address_edtxt;
    private String name, address, city, phone;
    private Button confirm;
    private ImageButton back, home;

    ArrayList<Cart> cartProducts;

    User currentUser;

    private String total_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        cartProducts = (ArrayList<Cart>) getIntent().getSerializableExtra("Cart Products");
        currentUser = Prevalent.currentUser;

        confirm = findViewById(R.id.confirm_final_order_btn);
        back = findViewById(R.id.back_btn);
        home = findViewById(R.id.home_btn);
        name_edtxt = findViewById(R.id.shipment_name);
        address_edtxt = findViewById(R.id.shipment_address);
        city_edtxt = findViewById(R.id.shipment_city);
        phone_edtxt = findViewById(R.id.shipment_phone);

        name_edtxt.setText(currentUser.getName());
        address_edtxt.setText(currentUser.getAddress());
        phone_edtxt.setText(currentUser.getPhone());

        name = name_edtxt.getText().toString();
        city = city_edtxt.getText().toString();
        address = address_edtxt.getText().toString();
        phone = phone_edtxt.getText().toString();



        total_amount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Amount To Pay is : " + total_amount, Toast.LENGTH_SHORT).show();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

    }

    private void check() {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please write your name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please write your phone", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(address_edtxt.getText().toString())) {
            Toast.makeText(this, "Please write your address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(city_edtxt.getText().toString())) {
            Toast.makeText(this, "Please write your city", Toast.LENGTH_SHORT).show();
        } else {
            confirmOrder();
        }

    }

    private void confirmOrder() {
        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentUser.getPhone());
        HashMap<String, Object> ordersMap = new HashMap<>();

        ordersMap.put("total amount", total_amount);
        ordersMap.put("name", name);
        ordersMap.put("phone", phone);
        ordersMap.put("address", address_edtxt.getText().toString());
        ordersMap.put("time", getCurrentTimee());
        ordersMap.put("city", city_edtxt.getText().toString());
        ordersMap.put("date", getCurrentDate());
        ordersMap.put("state", "not shipped");
        ordersMap.put("products", cartProducts);

        orderRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(Prevalent.currentUser.getPhone()).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ConfirmFinalOrderActivity.this, "Your Order has been placed successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }
                        });

                }
            }
        });

    }

    String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String time = simpleDateFormat.format(calendar.getTime());
        return time;
    }

    String getCurrentTimee() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss a");
        String time = simpleDateFormat.format(calendar.getTime());
        return time;
    }
}