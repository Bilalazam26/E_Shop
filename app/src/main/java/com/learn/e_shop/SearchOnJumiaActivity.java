package com.learn.e_shop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class SearchOnJumiaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_on_jumia);

        WebView jumia = findViewById(R.id.jumia);
        jumia.loadUrl("https://www.amazon.eg/");
    }
}