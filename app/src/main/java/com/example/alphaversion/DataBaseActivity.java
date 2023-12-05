package com.example.alphaversion;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DataBaseActivity extends AppCompatActivity {
    EditText eT;
    TextView tV;
    FirebaseDatabase database;
    String value;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        eT = (EditText) findViewById(R.id.eT);
        tV = (TextView) findViewById(R.id.tV);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Text");
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

    public void ReadText(View view) {
        String Text = eT.getText().toString();
        myRef.setValue(Text);
    }

    public void WriteToTextView(View view) {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        tV.setText(value);
    }
}