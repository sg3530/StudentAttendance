package com.abc.studentattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {

    private ImageView teacherImage;
    private ImageView studentImage;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_admin) {
            Intent intent = new Intent(MainActivity.this, AdminLogin.class);
            startActivity(intent);
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("WELCOME");
        teacherImage = (ImageView)findViewById(R.id.image_teacher);
        studentImage = (ImageView)findViewById(R.id.image_student);
        teacherImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginTeacher.class));
            }
        });
        System.out.println(studentImage);
        studentImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if(firebaseUser!=null) {


                    Intent intent = new Intent(MainActivity.this,StudentDashboard.class);
                    intent.putExtra("UID",firebaseUser.getUid());
                    startActivity(intent);
                    finish();
                }else {
                    startActivity(new Intent(MainActivity.this, LoginStudent.class));
                }
            }
        });

    }
}
