package com.example.alphaversion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class SecondActivity extends AppCompatActivity {
    private ImageView Image;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private static final int REQUEST_PICK_IMAGE = 1;
    private Uri imageUri;
    private StorageReference imageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Image = findViewById(R.id.iV);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    @Override
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
        if(id == R.id.AA)
            startActivity(new Intent(this, AlarmActivity.class));
        return super.onOptionsItemSelected(item);
    }

    public void LogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(SecondActivity.this, MainActivity.class));
        Toast.makeText(SecondActivity.this, "Logged Out successfully", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PICK_IMAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(SecondActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
            OpenGallery2();
        }
        else{
            Toast.makeText(SecondActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
            OpenGallery2();


        }
    }

    public void OpenGallery(View view) {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SecondActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PICK_IMAGE);
        }
        else{
            System.out.println("There's permissions");
            OpenGallery2();
        }


    }
    public void OpenGallery2() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Log.d("SecondActivity", "Image URI: " + imageUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();

                // Define the storage reference (replace "images/" with your desired path)
                imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");

                // Upload the image data to Firebase Storage
                UploadTask uploadTask = imageRef.putBytes(imageData);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    Toast.makeText(SecondActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    Toast.makeText(SecondActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(SecondActivity.this, "Error converting image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SecondActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
        }



    public void ShowPicture(View view) {
        if(imageRef != null){
            imageRef.getBytes(1920*1080).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Image.setImageBitmap(bitmap);
            }).addOnFailureListener(e -> {
                Toast.makeText(SecondActivity.this, e.getMessage().toString() , Toast.LENGTH_SHORT).show();
            });
        }
        else
            Toast.makeText(SecondActivity.this, "Couldn't find The Requested Image", Toast.LENGTH_SHORT).show();
    }


}
