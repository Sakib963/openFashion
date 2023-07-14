package com.example.openfashion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProductAdminActivity extends AppCompatActivity {

    private String productId;
    private Product product;

    AppCompatEditText product_name_edit_text, product_description_edit_text, product_price_edit_text, available_quantity_edit_text, rating_edit_text;
    AppCompatImageView upload_image_view;
    AppCompatTextView image_url;

    ProgressDialog progressDialog;
    Uri image_uri;

    AppCompatButton update_button, delete_button;

    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_admin);

        FirebaseApp.initializeApp(this);


        ProgressBar progressBar = findViewById(R.id.progressBar);

        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);

        product_name_edit_text = findViewById(R.id.product_name_edit_text);
        product_price_edit_text = findViewById(R.id.product_price_edit_text);
        available_quantity_edit_text = findViewById(R.id.available_quantity_edit_text);
        rating_edit_text = findViewById(R.id.rating_edit_text);
        upload_image_view = findViewById(R.id.upload_image_image_view);
        image_url = findViewById(R.id.image_url_text);
        product_description_edit_text = findViewById(R.id.product_description_edit_text);

        // Handling Image Upload
        upload_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });



        // Retrieve the product ID from the intent extras
        productId = getIntent().getStringExtra("productId");

        // Fetch the data for the specific product
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("products").child(productId);
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the product data from the dataSnapshot
                    Product product = dataSnapshot.getValue(Product.class);

                    // Update the views with the fetched product data
                    product_name_edit_text.setText(product.getName());
                    product_price_edit_text.setText(String.valueOf(product.getPrice()));
                    available_quantity_edit_text.setText(String.valueOf(product.getQuantity()));
                    product_description_edit_text.setText(product.getDescription());
                    // Load the image using Glide
                    Glide.with(upload_image_view.getContext())
                            .load(product.getImageURL())
                            .into(upload_image_view);
                    image_url.setText(product.getImageURL());
                } else {
                    // Handle the case where the product doesn't exist
                    // ...
                    Toast.makeText(ProductAdminActivity.this, "Product Doesn't Exists.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ProductAdminActivity.this, AllProductsActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the database error
                // ...
                Toast.makeText(ProductAdminActivity.this, "Something Went Wrong.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProductAdminActivity.this, AllProductsActivity.class));
                finish();
            }
        });


        // Update Button OnClickListener
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                update_button.setEnabled(false);
                delete_button.setEnabled(false);

                String productName = product_name_edit_text.getText().toString();
                String productPrice = product_price_edit_text.getText().toString();
                String availableQuantity = available_quantity_edit_text.getText().toString();
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
                    available_quantity_edit_text.setError("Available quantity is required");
                    progressBar.setVisibility(View.GONE);
                    return;
                }


                if (imageUpload.equals("Image URL")) {
                    Toast.makeText(ProductAdminActivity.this, "Image Upload is mandatory.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }


                Map<String, Object> updates = new HashMap<>();
                updates.put("name", productName);
                updates.put("description", productDescription);
                updates.put("price", Double.parseDouble(productPrice));
                updates.put("quantity", Integer.parseInt(availableQuantity));
                updates.put("imageURL", imageUpload);

                productsRef.updateChildren(updates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Product updated successfully
                                Toast.makeText(ProductAdminActivity.this, "Product updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ProductAdminActivity.this, AllProductsActivity.class));
                                finish();
                                update_button.setEnabled(true);
                                delete_button.setEnabled(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error occurred while updating product
                                Toast.makeText(ProductAdminActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                                update_button.setEnabled(true);
                                delete_button.setEnabled(true);
                            }
                        });
            }
        });


        // Delete Button OnClickListener
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductAdminActivity.this);
                builder.setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this product?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                update_button.setEnabled(false);
                                delete_button.setEnabled(false);

                                productsRef.removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                update_button.setEnabled(true);
                                                delete_button.setEnabled(true);
                                                // Product deleted successfully
                                                Toast.makeText(ProductAdminActivity.this, "Product deleted", Toast.LENGTH_SHORT).show();
                                                // Perform any additional operations or navigation after deletion
                                                startActivity(new Intent(ProductAdminActivity.this, AllProductsActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Error occurred while deleting product
                                                Toast.makeText(ProductAdminActivity.this, "Failed to delete product", Toast.LENGTH_SHORT).show();
                                                update_button.setEnabled(true);
                                                delete_button.setEnabled(true);
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked Cancel, do nothing
                                dialog.dismiss();
                            }
                        })
                        .show();
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
                            Toast.makeText(ProductAdminActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
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