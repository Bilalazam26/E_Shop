package com.learn.e_shop.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.e_shop.Interface.ItemClickListener;
import com.learn.e_shop.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txt_product_name, txt_product_describtion, txt_product_price;
    public ImageView product_img;
    public ItemClickListener itemClickListener;



    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        product_img = itemView.findViewById(R.id.product_image);
        txt_product_name = itemView.findViewById(R.id.product_name);
        txt_product_describtion = itemView.findViewById(R.id.product_describtion);
        txt_product_price = itemView.findViewById(R.id.product_price);
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
