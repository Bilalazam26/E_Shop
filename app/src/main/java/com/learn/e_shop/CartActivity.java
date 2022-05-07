package com.learn.e_shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.learn.e_shop.Model.Cart;
import com.learn.e_shop.Model.Product;
import com.learn.e_shop.Prevalent.Prevalent;
import com.learn.e_shop.ViewHolder.CartViewHolder;
import com.learn.e_shop.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button next_process_btn;
    private TextView total_price_txt;

    int overall_total_price;

    private ArrayList<Cart> cartProducts;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartProducts = new ArrayList();
        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        next_process_btn = findViewById(R.id.next_process_btn);
        total_price_txt = findViewById(R.id.total_price);

        ImageButton back = findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        next_process_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(overall_total_price));
                intent.putExtra("Cart Products", cartProducts);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        overall_total_price = 0;

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                        .child(Prevalent.currentUser.getPhone())
                        .child("products"), Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {

            int total_price;
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {

                cartProducts.add(model);
                String price_str = model.getPrice();
                String quantity_str = model.getQuantity();
                int price = Integer.parseInt(price_str);
                int quantity = Integer.parseInt(quantity_str);
                total_price = price * quantity;
                overall_total_price += total_price;
                holder.txt_product_name.setText(model.getName());
                holder.txt_product_price.setText("Price for " + quantity_str + "pcs : " + String.valueOf(total_price));
                holder.txt_product_quantity.setText("Quantity : " + model.getQuantity() + " pcs");
                total_price_txt.setText("Overall Price : " + String.valueOf(overall_total_price));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[] {
                          "Edit",
                          "Remove"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options:");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i == 0) {
                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());

                                    startActivity(intent);

                                }
                                if (i == 1) {
                                    cartListRef.child("User View").child(Prevalent.currentUser.getPhone()).child("products").child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        cartProducts.remove(model);
                                                        String price_str = model.getPrice();
                                                        String quantity_str = model.getQuantity();
                                                        int price = Integer.parseInt(price_str);
                                                        int quantity = Integer.parseInt(quantity_str);
                                                        total_price = price * quantity;
                                                        overall_total_price -= total_price;
                                                        total_price_txt.setText("Overall Price : " + String.valueOf(overall_total_price));
                                                        Toast.makeText(CartActivity.this, "Item Removed Successfully", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}