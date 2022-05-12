package com.learn.e_shop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.learn.e_shop.Model.User;
import com.learn.e_shop.Prevalent.Prevalent;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profile_img;
    private EditText edt_name, edt_phone, edt_address;
    private TextView chng_profile_img;
    private Button update;
    private ImageButton  close;
    private Uri imageUri;
    private String myUrl = "";

    private StorageReference storageProfilePictureRef;
    private StorageTask uploadTask;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        profile_img = findViewById(R.id.img_profile_setting);
        edt_name = findViewById(R.id.full_name_settings);
        edt_address = findViewById(R.id.address_setting);
        edt_phone = findViewById(R.id.phone_settings);
        close = findViewById(R.id.back_btn);
        update = findViewById(R.id.update_profile_settings);
        chng_profile_img = findViewById(R.id.profile_img_change_btn);



        userInfoDisplay();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checker.equals("clicked")) {
                    userInfoSaved();
                } else {
                    updateOnlyUserInfo();
                }
            }
        });

        chng_profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";
                openGallary();
            }
        });
    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", edt_name.getText().toString());
        userMap.put("address", edt_address.getText().toString());
        userMap.put("phone", edt_phone.getText().toString());
        ref.child(Prevalent.currentUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile Information updated Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void userInfoSaved() {

        if (TextUtils.isEmpty(edt_name.getText().toString())) {
            Toast.makeText(this, "Name is Mandatory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(edt_address.getText().toString())) {
            Toast.makeText(this, "Address is Mandatory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(edt_phone.getText().toString())) {
            Toast.makeText(this, "Phone is Mandatory", Toast.LENGTH_SHORT).show();
        } else if (checker.equals("clicked")) {
            uploadImage();
        }

    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while updating your account info");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (imageUri != null) {
            final StorageReference fileRef = storageProfilePictureRef.child(Prevalent.currentUser.getPhone()+".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", edt_name.getText().toString());
                        userMap.put("address", edt_address.getText().toString());
                        userMap.put("phone", edt_phone.getText().toString());
                        userMap.put("image", myUrl);
                        ref.child(Prevalent.currentUser.getPhone()).updateChildren(userMap);
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Profile Information updated Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay() {

        User user = Prevalent.currentUser;
        edt_phone.setText(user.getPhone());
        edt_name.setText(user.getName());
        edt_address.setText(user.getAddress());
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(profile_img);

    }

    private void openGallary() {
        Intent gallaryIntent = new Intent();
        gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
        gallaryIntent.setType("image/*");
        startActivityForResult(gallaryIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profile_img.setImageURI(imageUri);
        }
    }
}