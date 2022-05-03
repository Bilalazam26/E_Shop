package com.learn.e_shop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private static final int gallaryPick = 1;
    private Uri imageUri;

    private String categoryName, name, describtion, price;
    private TextView add_category_product;

    private Button addNewProductButton;
    private ImageView input_product_image;
    private EditText input_product_name, input_product_price, input_product_describtion;

    private String saveCurrentDate, saveCurrentTime;

    private String productRandomKey;

    private StorageReference productImagesRef;

    private String downloadImageUrl;

    private DatabaseReference productRef;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        productRef = FirebaseDatabase.getInstance().getReference().child("products");

        categoryName = getIntent().getStringExtra("category");
        productImagesRef = FirebaseStorage.getInstance().getReference().child("product Images");

        add_category_product = findViewById(R.id.add_category_product);
        add_category_product.setText("Add "+categoryName);

        addNewProductButton = findViewById(R.id.add_new_product);
        input_product_image = findViewById(R.id.select_product_image);
        input_product_name = findViewById(R.id.product_name);
        input_product_describtion = findViewById(R.id.product_describtion);
        input_product_price = findViewById(R.id.product_price);

        input_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallary();
            }
        });

        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateProductData();
            }
        });

        progressDialog = new ProgressDialog(this);

    }

    private void validateProductData() {
        describtion = input_product_describtion.getText().toString();
        price = input_product_price.getText().toString();
        name = input_product_name.getText().toString();

        if (imageUri == null) {
            Toast.makeText(this, "Product Image is mandatory", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(describtion)) {
            Toast.makeText(this, "Please write Product Describtion", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Please write Product Price", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please write Product Name", Toast.LENGTH_SHORT).show();
        } else {
            storeProductInfo();
        }

    }

    private void storeProductInfo() {

        progressDialog.setTitle("Adding new Product");
        progressDialog.setMessage("Please wait, While adding the product.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        productRandomKey = saveCurrentDate+saveCurrentTime;
        StorageReference filePath = productImagesRef.child(imageUri.getLastPathSegment()+productRandomKey+".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Product Image Uploded Successfully", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Get Product Image url successfully", Toast.LENGTH_SHORT).show();
                            saveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void saveProductInfoToDatabase() {

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("name",name);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("describtion",describtion);
        productMap.put("image",downloadImageUrl);
        productMap.put("category",categoryName);
        productMap.put("price",price);

        productRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Product added Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                    startActivity(intent);
                } else {
                    progressDialog.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(AdminAddNewProductActivity.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void openGallary() {
    Intent gallaryIntent = new Intent();
    gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
    gallaryIntent.setType("image/*");
    startActivityForResult(gallaryIntent, gallaryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gallaryPick && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            input_product_image.setImageURI(imageUri);
        }
    }
}