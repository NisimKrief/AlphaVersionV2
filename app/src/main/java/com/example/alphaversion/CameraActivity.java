package com.example.alphaversion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class CameraActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 2;
    private ImageView Image;
    private ImageView Image2;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Image = (ImageView) findViewById(R.id.iV);
        Image2 = (ImageView) findViewById(R.id.iV2);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        if(id == R.id.GA)
            startActivity(new Intent(this, SecondActivity.class));
        if(id == R.id.CA)
            startActivity(new Intent(this, CameraActivity.class));
        if(id == R.id.DBA)
            startActivity(new Intent(this, DataBaseActivity.class));
        return super.onOptionsItemSelected(item);
    }

    public void OpenCamera(View view) {
        Intent OpenCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(OpenCamera, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Image.setImageBitmap(photo);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] imageData = baos.toByteArray();
            // Define the storage reference (replace "images/" with your desired path)
            imageRef = storageRef.child("imagesfromcamera/" + System.currentTimeMillis() + ".jpg");

            // Upload the image data to Firebase Storage
            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Image uploaded successfully
                Toast.makeText(CameraActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                // Handle unsuccessful uploads
                Toast.makeText(CameraActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            });
        }
        else {
            Toast.makeText(CameraActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }





    public void GetFromFireBase(View view) {
        if(imageRef != null){
            imageRef.getBytes(1920*1080).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Image2.setImageBitmap(bitmap);
            }).addOnFailureListener(e -> {
                Toast.makeText(CameraActivity.this, e.getMessage().toString() , Toast.LENGTH_SHORT).show();
            });
        }
        else
            Toast.makeText(CameraActivity.this, "Couldn't find The Requested Image", Toast.LENGTH_SHORT).show();

    }
}