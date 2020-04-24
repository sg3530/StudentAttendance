package com.abc.studentattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
public class LoginStudent extends AppCompatActivity {
    private TextView createAccountStudent;
    private Button button_login;
    private EditText email;
    private EditText password;
    private ImageView showPassword;
    private ImageView hidePassword;
    private ImageView showHidePassword;
    private ProgressBar progressBar;
    private TextView forgotPassword;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String studentEmail="";
    private String branch="",year="",sec="",isAddInList="",rollNo="",uid="";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_student);
        getSupportActionBar().setTitle("Student Login");
        button_login = findViewById(R.id.button_studentLogin);
        createAccountStudent = findViewById(R.id.text_studentcreate);
        email = findViewById(R.id.ediText_studentLoginEmail);
        password = findViewById(R.id.editText_studentLoginPassword);
        showPassword = findViewById(R.id.toggle_visibleOffPassword);
        hidePassword = findViewById(R.id.toggle_visiblePassword);
        showHidePassword = findViewById(R.id.toggle_visibleOffPassword);
        progressBar = findViewById(R.id.progressBar);
        forgotPassword = findViewById(R.id.text_studentLoginForgotPassword);

        progressBar.setVisibility(View.INVISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        hidePassword.setVisibility(View.INVISIBLE);

        createAccountStudent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginStudent.this,studentRegistration.class));
            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                studentEmail = email.getText().toString().trim();
                String studentPassword = password.getText().toString().trim();
                if(studentEmail.isEmpty())
                {
                    email.setError("This field is required!");
                    return;
                }
                if(studentPassword.isEmpty())
                {
                    password.setError("This field is required!");
                    return;
                }
                if(studentPassword.length()<6)
                {
                    password.setError("Minimum 6 characters required!");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(studentEmail, studentPassword)
                        .addOnCompleteListener(LoginStudent.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    checkEmailVerification();
                                } else {
                                    Toast.makeText(LoginStudent.this,"Incorrect Email or Password!",Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
            }
        });

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassword.setVisibility(View.INVISIBLE);
                hidePassword.setVisibility(View.VISIBLE);
                showHidePassword = findViewById(R.id.toggle_visiblePassword);
                password.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });
        hidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassword.setVisibility(View.VISIBLE);
                hidePassword.setVisibility(View.INVISIBLE);
                showHidePassword =  findViewById(R.id.toggle_visibleOffPassword);
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginStudent.this,ForgotPassword.class));
                finish();
            }
        });
        password.addTextChangedListener(enableToggleButtonWatcher);

    }
    private TextWatcher enableToggleButtonWatcher = new TextWatcher(){
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(showHidePassword.getId() == R.id.toggle_visibleOffPassword)
            {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }else{
                password.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private void checkEmailVerification(){
         FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
         uid = firebaseUser.getUid();
        Boolean email_flag = firebaseUser.isEmailVerified();
        if(email_flag){
            Toast.makeText(LoginStudent.this,"Login Successfull!",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
           // String email = firebaseUser.getEmail();
            //int l = email.length();
            //studentEmail = email.substring(0,l-4);
            databaseReference = FirebaseDatabase.getInstance().getReference().child("student").child(uid).child("RegistrationRecord");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    branch = dataSnapshot.child("branch").getValue(String.class);
                    year = dataSnapshot.child("year").getValue(String.class);
                    sec = dataSnapshot.child("section").getValue(String.class);
                    rollNo = dataSnapshot.child("rollNumber").getValue(String.class);

                    isAddInList = dataSnapshot.child("isAddInSecList").getValue(String.class);
                    System.out.println(branch+" "+year+" "+sec+" "+isAddInList);
                    if(isAddInList.equals("false"))
                    {
                        System.out.println("yes");
                        databaseReference.child("isAddInSecList").setValue("true");
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("StudentsList").child(year).child(branch).child(sec);
                        databaseReference.child(uid).setValue(rollNo);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Intent intent  = new Intent(LoginStudent.this,StudentDashboard.class);
            intent.putExtra("UID",uid);
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(LoginStudent.this,"Verify your email",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            firebaseAuth.signOut();
        }
    }

}
