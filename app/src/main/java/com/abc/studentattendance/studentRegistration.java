package com.abc.studentattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.*;


public class studentRegistration extends AppCompatActivity {
    private Spinner branch;
    private Spinner year;
    private Spinner section;


    private TextView errorImage;

    private Button registerStudent;
    private TextView loginStudent;
    private RadioGroup genderGroup;
    private EditText email;
    private EditText name;
    private EditText mobileNumber;
    private EditText password;
    private EditText RollNumber;
    private RadioButton gender;

    private DatabaseReference databaseReference;

    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar_registration;
    private FirebaseUser firebaseUser;


    String studentEmail = "", studentMobileNumber = "", studentPassword = "", studentRollNumber = "", studentGender = "", studentBranch = "", studentYear = "",
            studentSection = "", studentName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);
        getSupportActionBar().setTitle("Student Registration");

        branch = findViewById(R.id.spinner_studentBranch);
        year = findViewById(R.id.spinner_studentYear);
        section = findViewById(R.id.spinner_studentSection);

        genderGroup = findViewById(R.id.radioGroup_studentGender);
        registerStudent = findViewById(R.id.button_studentRegister);
        email = findViewById(R.id.editText_studentEmail);
        name = findViewById(R.id.editText_studentName);
        mobileNumber = findViewById(R.id.editText_studentMobileno);
        password = findViewById(R.id.editText_studentPassword);
        RollNumber = findViewById(R.id.editText_studentRollno);

        loginStudent = findViewById(R.id.text_studentLogin);
        progressBar_registration = findViewById(R.id.progressBar_registration);
        progressBar_registration.setVisibility(View.INVISIBLE);


        ArrayAdapter<String> branchesAdapter = new ArrayAdapter<String>(studentRegistration.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.BranchNames));
        branchesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branch.setAdapter(branchesAdapter);

        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<String>(studentRegistration.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.YearNames));
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year.setAdapter(yearsAdapter);

        ArrayAdapter<String> sectionsAdapter = new ArrayAdapter<String>(studentRegistration.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.SectionNames));
        sectionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        section.setAdapter(sectionsAdapter);


        registerStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                studentEmail = email.getText().toString().trim();
                studentName = name.getText().toString().trim();
                studentMobileNumber = mobileNumber.getText().toString().trim();
                studentPassword = password.getText().toString().trim();
                studentRollNumber = RollNumber.getText().toString().trim();
                int id = genderGroup.getCheckedRadioButtonId();
                gender = findViewById(id);

                studentGender = gender.getText().toString().trim();
                studentBranch = branch.getSelectedItem().toString().trim();
                studentYear = year.getSelectedItem().toString().trim();
                studentSection = section.getSelectedItem().toString().trim();


                if (studentEmail.isEmpty()) {
                    email.setError("This field is Required!");
                    return;
                }
                if (studentName.isEmpty()) {
                    name.setError("This field is Required!");
                    return;
                }
                if (studentMobileNumber.isEmpty()) {
                    mobileNumber.setError("This field is Required!");
                    return;
                }
                if (studentMobileNumber.length() != 10) {
                    mobileNumber.setError("Invalid Number!");
                    return;
                }
                if (studentPassword.isEmpty()) {
                    password.setError("This field is Required!");
                    return;
                }
                if (studentPassword.length() < 6) {
                    password.setError("6 characters are required!");
                }
                if (studentRollNumber.isEmpty()) {
                    RollNumber.setError("This field is Required!");
                    return;
                }

                databaseReference = FirebaseDatabase.getInstance().getReference("student");
                firebaseAuth = FirebaseAuth.getInstance();

                progressBar_registration.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(studentEmail, studentPassword)
                        .addOnCompleteListener(studentRegistration.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    sendEmailVerification();

                                } else {
                                    Toast.makeText(studentRegistration.this, "Registration Failed", Toast.LENGTH_LONG).show();
                                    progressBar_registration.setVisibility(View.INVISIBLE);
                                }
                            }

                        });
            }
        });

        loginStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(studentRegistration.this, LoginStudent.class));
            }
        });
    }
    private void sendEmailVerification(){
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                        String uid = firebaseUser.getUid();
                        //Toast.makeText(studentRegistration.this,"Verification email has been sent",Toast.LENGTH_SHORT);
                        Student information = new Student(studentName, studentEmail, studentMobileNumber, studentRollNumber, studentGender, studentBranch, studentYear, studentSection, "uri","false");

                        FirebaseDatabase.getInstance().getReference("student").child(uid).child("RegistrationRecord").setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(studentRegistration.this, "Registration Complete,Verification email has been sent on your registered email.", Toast.LENGTH_LONG).show();
                            }
                        });
                        progressBar_registration.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(studentRegistration.this, LoginStudent.class);
                        startActivity(intent);
                        firebaseAuth.signOut();
                        finish();
                    }else{
                        Toast.makeText(studentRegistration.this,"Verification mail has'nt sent",Toast.LENGTH_SHORT);
                        progressBar_registration.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

}
