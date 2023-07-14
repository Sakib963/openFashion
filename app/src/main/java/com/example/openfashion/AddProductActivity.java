package com.example.openfashion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddProductActivity extends AppCompatActivity {


    AppCompatEditText product_name_edit_text, product_price_edit_text, product_available_quantity_edit_text, product_description_edit_text;
    AppCompatImageView upload_image_view;
    AppCompatTextView image_url;

    ProgressDialog progressDialog;
    Uri image_uri;

    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        FirebaseApp.initializeApp(this);


        ProgressBar progressBar = findViewById(R.id.progressBar);
        Button submitButton = findViewById(R.id.submit_button);


        product_name_edit_text = findViewById(R.id.product_name_edit_text);
        product_price_edit_text = findViewById(R.id.product_price_edit_text);
        product_available_quantity_edit_text = findViewById(R.id.available_quantity_edit_text);
        upload_image_view = findViewById(R.id.upload_image_image_view);
        image_url = findViewById(R.id.image_url_text);
        product_description_edit_text = findViewById(R.id.product_description_edit_text);


        upload_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                submitButton.setEnabled(false);


                String productName = product_name_edit_text.getText().toString();
                String productPrice = product_price_edit_text.getText().toString();
                String availableQuantity = product_available_quantity_edit_text.getText().toString();
                String imageUpload = image_url.getText().toString();
                String productDescription = product_description_edit_text.getText().toString();

                if (TextUtils.isEmpty(productName)) {
                    product_name_edit_text.setError("Product name is required");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(productDescription)) {
                    product_description_edit_text.setError("Product Description is required");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(productPrice)) {
                    product_price_edit_text.setError("Product price is required");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(availableQuantity)) {
                    product_available_quantity_edit_text.setError("Available quantity is required");
                    progressBar.setVisibility(View.GONE);
                    return;
                }


                if (imageUpload.equals("Image URL")) {
                    Toast.makeText(AddProductActivity.this, "Image Upload is mandatory.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                // Get a reference to the Firebase Realtime Database
                DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
                // Generate a unique key for the product
                String productId = productsRef.push().getKey();

                // If all fields are valid, proceed to image upload and database save
                Product product = new Product(
                        productId,
                        productName,
                        productDescription,
                        Double.parseDouble(productPrice),
                        Integer.parseInt(availableQuantity),
                        imageUpload
                        );

                // Save the product data to the database under the generated key
                productsRef.child(productId).setValue(product)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddProductActivity.this, "Product saved successfully", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // Clear the input fields
                                product_name_edit_text.setText("");
                                product_description_edit_text.setText("");
                                product_price_edit_text.setText("");
                                product_available_quantity_edit_text.setText("");
                                image_url.setText(R.string.image_url_text);
                                upload_image_view.setImageResource(R.drawable.image_icon);

                                submitButton.setEnabled(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddProductActivity.this, "Failed to save product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                submitButton.setEnabled(true);
                            }
                        });
            }
        });
    }

    private void uploadImage() {
        if (image_uri != null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Image.......");
            progressDialog.show();


            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
            Date now = new Date();
            String filename = formatter.format(now);
            storageReference = FirebaseStorage.getInstance().getReference("/images"+filename);

            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageURL = uri.toString();
                                    image_url.setText(imageURL);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null){
            image_uri = data.getData();
            upload_image_view.setImageURI(image_uri);
            uploadImage();
        }
    }
}