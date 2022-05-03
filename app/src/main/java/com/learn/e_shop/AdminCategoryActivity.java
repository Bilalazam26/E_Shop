package com.learn.e_shop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class AdminCategoryActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView t_shirts, sports_t_shirts, female_dresses, sweather;
    private ImageView glasses, purses_bags, hats, shoes;
    private ImageView mobiles, watches, laptops, headphoness;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        t_shirts = findViewById(R.id.t_shirts);
        t_shirts.setOnClickListener(this);

        sports_t_shirts = findViewById(R.id.sports_t_shirts);
        sports_t_shirts.setOnClickListener(this);

        female_dresses = findViewById(R.id.female_dresses);
        female_dresses.setOnClickListener(this);

        sweather = findViewById(R.id.sweather);
        sweather.setOnClickListener(this);

        glasses = findViewById(R.id.glasses);
        glasses.setOnClickListener(this);

        purses_bags = findViewById(R.id.purses_bags);
        purses_bags.setOnClickListener(this);

        hats = findViewById(R.id.hats);
        hats.setOnClickListener(this);

        shoes = findViewById(R.id.shoes);
        shoes.setOnClickListener(this);


        mobiles = findViewById(R.id.mobiles);
        mobiles.setOnClickListener(this);

        watches = findViewById(R.id.watches);
        watches.setOnClickListener(this);

        laptops = findViewById(R.id.laptops);
        laptops.setOnClickListener(this);

        headphoness = findViewById(R.id.headphoness);
        headphoness.setOnClickListener(this);





    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
        if (view.getId() == R.id.t_shirts) {
            intent.putExtra("category", "t_shirts");
            startActivity(intent);
        } else if (view.getId() == R.id.sports_t_shirts) {
            intent.putExtra("category", "sports_t_shirts");
            startActivity(intent);
        } else if (view.getId() == R.id.female_dresses) {
            intent.putExtra("category", "female_dresses");
            startActivity(intent);
        } else if (view.getId() == R.id.sweather) {
            intent.putExtra("category", "sweather");
            startActivity(intent);
        } else if (view.getId() == R.id.glasses) {
            intent.putExtra("category", "glasses");
            startActivity(intent);
        } else if (view.getId() == R.id.purses_bags) {
            intent.putExtra("category", "purses_bags");
            startActivity(intent);
        } else if (view.getId() == R.id.hats) {
            intent.putExtra("category", "hats");
            startActivity(intent);
        } else if (view.getId() == R.id.shoes) {
            intent.putExtra("category", "shoes");
            startActivity(intent);
        } else if (view.getId() == R.id.mobiles) {
            intent.putExtra("category", "mobiles");
            startActivity(intent);
        } else if (view.getId() == R.id.watches) {
            intent.putExtra("category", "watches");
            startActivity(intent);
        } else if (view.getId() == R.id.laptops) {
            intent.putExtra("category", "laptops");
            startActivity(intent);
        } else if (view.getId() == R.id.headphoness) {
            intent.putExtra("category", "headphoness");
            startActivity(intent);
        } else {};
    }
}