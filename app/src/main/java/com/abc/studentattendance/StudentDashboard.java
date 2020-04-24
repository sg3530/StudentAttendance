package com.abc.studentattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Base64;

public class StudentDashboard extends AppCompatActivity {

    private TextView text_dashboardEmail;
    private TextView text_dashboardName;
    private ImageView image_dashboard;
    private long backPressedTime;


    private Toast backTost;
    private FirebaseUser firebaseUser;


    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private String uid="";
    private String fileName="";
    private TextView resultData;
    public static TextView scannerResult;
    private String year="",branch="";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.item_profile)
        {
            Intent intent = new Intent(StudentDashboard.this,StudentProfile.class);
            intent.putExtra("UID",uid);
            startActivity(intent);
            finish();
        }
        if(id == R.id.item_markAttendance)
        {
            Intent intent = new Intent(StudentDashboard.this,StudentQRScanner.class);
            intent.putExtra("UID",uid);
            intent.putExtra("Branch",branch);
            intent.putExtra("Year",year);
            startActivity(intent);
        }
        if(id == R.id.item_viewAttendance)
        {
            Intent intent = new Intent(StudentDashboard.this,StudentViewAttendance.class);
            intent.putExtra("UID",uid);
            intent.putExtra("Branch",branch);
            intent.putExtra("Year",year);
            startActivity(intent);
        }
        if(id == R.id.item_logout)
        {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            Toast.makeText(StudentDashboard.this,"Successfully Logout",Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(StudentDashboard.this,LoginStudent.class));
            return true;
        }

        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        getSupportActionBar().setTitle("Student Dashboard");
        text_dashboardEmail = findViewById(R.id.textView_studentProfileEmail);
        text_dashboardName = findViewById(R.id.textView_studentProfileName);
        image_dashboard = findViewById(R.id.image_studentProfile);
        scannerResult = findViewById(R.id.textView_scannerResult);



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        text_dashboardEmail.setText(firebaseUser.getEmail());
        uid =  getIntent().getStringExtra("UID");

        databaseReference = FirebaseDatabase.getInstance().getReference("student").child(uid).child("RegistrationRecord");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user_name = dataSnapshot.child("fullName").getValue(String.class);
                text_dashboardName.setText(user_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StudentDashboard.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("student").child(uid).child("RegistrationRecord");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String urlLink = dataSnapshot.child("imguri").getValue(String.class);
                if(urlLink.equals("uri"))
                    image_dashboard.setImageResource(R.drawable.ic_person_black_24dp);
                else
                    Picasso.get().load(urlLink).into(image_dashboard);
                branch = dataSnapshot.child("branch").getValue(String.class);
                year = dataSnapshot.child("year").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StudentDashboard.this,databaseError.getMessage(),Toast.LENGTH_SHORT);
            }
        });

    }
    @Override
    public void onBackPressed() {

        if(backPressedTime + 2000 > System.currentTimeMillis())
        {
            backTost.cancel();
            super.onBackPressed();
            return;
        }else
        {
            backTost = Toast.makeText(StudentDashboard.this,"Press back again to exit",Toast.LENGTH_SHORT);
            backTost.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
