package com.example.unicornfashion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unicornfashion.Admin.AdminCategoryActivity;
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

public class AddSellerActivity extends AppCompatActivity {

    private String CategoryName, Company, Type, Sname, saveCurrentDate, saveCurrentTime;
    private String ProductRandomKey, downloadImageUrl;
    private Button AddSellerButton;
    private ImageView InputSellerImage;
    private EditText InputSellerName, InputCompany, InputSellerType;
    private static final int GalleryPick = 1;
    private Uri ImagrUri;
    private StorageReference SellerImageRef;
    private DatabaseReference SellerRef;
    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_seller);




        CategoryName = getIntent().getExtras().get("category").toString();
        SellerImageRef = FirebaseStorage.getInstance().getReference().child("Seller Images");
        SellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers");



        AddSellerButton = (Button) findViewById(R.id.add_new_seller);
        InputSellerImage = (ImageView) findViewById(R.id.select_seller_Image);
        InputSellerName = (EditText) findViewById(R.id.seller_name);
        InputCompany = (EditText) findViewById(R.id.seller_company);
        InputSellerType = (EditText) findViewById(R.id.seller_type);

        loadingBar = new ProgressDialog(this);


        InputSellerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }

        });


        AddSellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });
    }



    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick && resultCode == RESULT_OK && data != null){

            ImagrUri = data.getData();
            InputSellerImage.setImageURI(ImagrUri);

        }
    }


    private void ValidateProductData() {
        Company= InputCompany.getText().toString();
        Type= InputSellerType.getText().toString();
        Sname= InputSellerName.getText().toString();

        if (ImagrUri == null){
            Toast.makeText(AddSellerActivity.this, "Seller Image is Mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Company)){
            Toast.makeText(AddSellerActivity.this, "Name is empty", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Type)){
            Toast.makeText(AddSellerActivity.this, "Company is empty", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Sname)){
            Toast.makeText(AddSellerActivity.this, "Type is empty", Toast.LENGTH_SHORT).show();
        }
        else{
            StoreProductInformation();
        }


    }

    private void StoreProductInformation() {

        loadingBar.setTitle("Add new Product");
        loadingBar.setMessage("Please Wait, while we are adding the new product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calender = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd yyyy");
        saveCurrentDate = currentDate.format(calender.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calender.getTime());

        ProductRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = SellerImageRef.child(ImagrUri.getLastPathSegment() + ProductRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImagrUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message = e.toString();
                Toast.makeText(AddSellerActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddSellerActivity.this, "Seller Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        if (task.isSuccessful()){

                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AddSellerActivity.this, "Got Sellers Image Successfully", Toast.LENGTH_SHORT).show();
                            saveProductInfoToDatabasr();

                        }
                    }
                });
            }

        });


    }

    private void saveProductInfoToDatabasr() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", ProductRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("company", Company);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", CategoryName);
        productMap.put("type", Type);
        productMap.put("seller_name", Sname);

        SellerRef.child(ProductRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Intent intent = new Intent(AddSellerActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AddSellerActivity.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AddSellerActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}