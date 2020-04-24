package com.abc.studentattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.StringTokenizer;


public class StudentQRScanner extends AppCompatActivity {
    CodeScanner codeScanner;
    CodeScannerView scanView;
    private TextView resultScanner;
    private String result="",id="",teacherMail="",date="",time="",subject="",studentEmail="",studentBranch="",studentYear="",uid="";
    private DatabaseReference databaseReference;
    private MediaPlayer beep;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_q_r_scanner);
        scanView = findViewById(R.id.scannerView);
        resultScanner = findViewById(R.id.scannerResult);
        uid = getIntent().getStringExtra("UID");
        studentBranch = getIntent().getStringExtra("Branch");
        studentYear = getIntent().getStringExtra("Year");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        studentEmail = firebaseUser.getEmail();
        codeScanner = new CodeScanner(StudentQRScanner.this,scanView);
        beep = MediaPlayer.create(StudentQRScanner.this,R.raw.beepsound);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        resultScanner.setText(result.getText());
                        markAttendance();
                    }
                });
            }
        });
        scanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();
    }
    private void requestForCamera(){
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(StudentQRScanner.this,"Camera permission is required",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }
    private void markAttendance(){
        result = resultScanner.getText().toString();
        StringTokenizer st = new StringTokenizer(result,",");
        id = st.nextToken();
        teacherMail = st.nextToken();
        date = st.nextToken();
        time = st.nextToken();
        subject = st.nextToken();
        System.out.println(id+" "+teacherMail+" "+date+" "+time+" "+subject+" "+studentEmail);
        databaseReference = FirebaseDatabase.getInstance().getReference("student").child(uid).child("Attendance").child(date);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(subject))
                {
                    Toast.makeText(StudentQRScanner.this,"Sorry,Attendance of this subject is already marked.",Toast.LENGTH_SHORT).show();

                }else{
                    databaseReference.child(subject).setValue("Present");
                    beep.start();
                    Intent intent = new Intent(StudentQRScanner.this,StudentViewAttendance.class);
                    intent.putExtra("UID",uid);
                    intent.putExtra("Branch",studentBranch);
                    intent.putExtra("Year",studentYear);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
