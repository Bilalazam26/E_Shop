package com.learn.e_shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.e_shop.Model.Product;
import com.learn.e_shop.Prevalent.Prevalent;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {


    Product product;
    String productId, price;
    ImageView product_img;
    Button add_to_cart;
    TextView product_name, product_price, product_description;

    ImageButton mince, plus, back;
    TextView product_count_txt;
    int count;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productId = getIntent().getStringExtra("pid");



        product_img = findViewById(R.id.product_img_details);
        add_to_cart = findViewById(R.id.add_product_to_cart);
        product_name = findViewById(R.id.product_name_details);
        product_description = findViewById(R.id.product_describtion_details);
        product_price = findViewById(R.id.product_price_details);


        getProductDetails(productId);


        mince = findViewById(R.id.mince_btn);
        plus = findViewById(R.id.plus_btn);
        back = findViewById(R.id.back_btn);
        product_count_txt = findViewById(R.id.product_count_txt);
        mince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(product_count_txt.getText().toString());

                if (count > 1) {
                    count --;
                    product_count_txt.setText(String.valueOf(count));
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "have to buy at least one", Toast.LENGTH_SHORT).show();
                }

            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(product_count_txt.getText().toString());
                count ++;
                product_count_txt.setText(String.valueOf(count));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCartList();
            }
        });
    }

    private void getProductDetails(String productId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("products");
        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    price = product.getPrice();
                    product_name.setText(product.getName());
                    product_price.setText("Price: "+price+" L.E");
                    product_description.setText(product.getName()+" Describtion\n"+product.getDescribtion());
                    Picasso.get().load(product.getImage()).into(product_img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addToCartList() {
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productId);
        cartMap.put("name", product_name.getText().toString());
        cartMap.put("price", price);
        cartMap.put("date", getCurrentDate());
        cartMap.put("time", getCurrentTimee());
        count = Integer.parseInt(product_count_txt.getText().toString());
        cartMap.put("quantity", String.valueOf(count));
        cartMap.put("discount", "");

        cartListRef.child("User View").child(Prevalent.currentUser.getPhone()).child("products").child(productId).updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            cartListRef.child("Admin View").child(Prevalent.currentUser.getPhone()).child("products").child(productId).updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProductDetailsActivity.this, "Added to Cart Successfylly", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ProductDetailsActivity.this, CartActivity.class);
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