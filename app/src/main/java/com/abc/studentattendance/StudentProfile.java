package com.abc.studentattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.sql.SQLOutput;

public class StudentProfile extends AppCompatActivity {

    private TextView profileName;
    private TextView email;
    private EditText fullName;
    private EditText mobileNumber;
    private TextView rollNumber;
    private RadioButton gender;
    private TextView branch;
    private TextView year;
    private TextView section;
    private RadioGroup genderGroup;
    private Uri imageUri;
    private ImageView profileImage;
    public int IMAGE_CODE = 1;
    private TextView profileImageError;
    private Button update;
    private Button uploadPhoto;
    private boolean enableUpdateButton;


    private DatabaseReference databaseReference;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private StorageReference storageReference;
    private StorageTask uploadTask;
    private Uri setImageUri=null;
    private boolean setImage = false;
    private ProgressBar progressBar_profile;
    private String uid="";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.item_logout)
        {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            Toast.makeText(StudentProfile.this,"Successfully Logout",Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(StudentProfile.this,LoginStudent.class));
            return true;
        }
        if(id == R.id.item_dashboard)
        {
            Intent intent = new Intent(StudentProfile.this,StudentDashboard.class);
            intent.putExtra("UID",uid);
            startActivity(intent);
            finish();
            return true;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        getSupportActionBar().setTitle("Student Profile");

        profileName = findViewById(R.id.text_profileName);
        email = findViewById(R.id.textView_emailText);
        fullName = findViewById(R.id.editText_fullName);
        mobileNumber = findViewById(R.id.editText_mobileNumber);
        rollNumber = findViewById(R.id.textView_RollNo);
        genderGroup = findViewById(R.id.radioGroup_studentGender);
        branch = findViewById(R.id.textView_branchName);
        year = findViewById(R.id.textView_year);
        section = findViewById(R.id.textView_section);
        update = findViewById(R.id.button_update);
        uploadPhoto = findViewById(R.id.button_saveImage);


        progressBar_profile = findViewById(R.id.progressBar_profile);
        progressBar_profile.setVisibility(View.INVISIBLE);

        int id = genderGroup.getCheckedRadioButtonId();

        profileImage = findViewById(R.id.image_studentProfile);

        uid = getIntent().getStringExtra("UID");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        email.setText(firebaseUser.getEmail());



        update.setEnabled(false);
        uploadPhoto.setEnabled(false);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageFrom();
            }
        });

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                update.setEnabled(true);
                enableUpdateButton = true;
            }
        });

        fullName.addTextChangedListener(enableUpdateButtonWatcher);
        mobileNumber.addTextChangedListener(enableUpdateButtonWatcher);

        storageReference = FirebaseStorage.getInstance().getReference("Upload").child(uid);
        databaseReference = FirebaseDatabase.getInstance().getReference("student").child(uid).child("RegistrationRecord");
        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                fullName.setHint(dataSnapshot.child("fullName").getValue(String.class));
                profileName.setText(dataSnapshot.child("fullName").getValue(String.class));
                mobileNumber.setHint(dataSnapshot.child("mobileNumber").getValue(String.class));
                rollNumber.setText(dataSnapshot.child("rollNumber").getValue(String.class));

                String studentGender = dataSnapshot.child("gender").getValue(String.class);
                if (studentGender.equals("Male"))
                    ((RadioButton) genderGroup.getChildAt(0)).setChecked(true);
                else if (studentGender.equals("Female"))
                    ((RadioButton) genderGroup.getChildAt(1)).setChecked(true);
                else
                    ((RadioButton) genderGroup.getChildAt(2)).setChecked(true);
                branch.setText(dataSnapshot.child("branch").getValue(String.class));
                year.setText(dataSnapshot.child("year").getValue(String.class));
                section.setText(dataSnapshot.child("section").getValue(String.class));

                String urlLink = dataSnapshot.child("imguri").getValue(String.class);
                if(urlLink.equals("uri"))
                {
                    profileImage.setImageResource(R.drawable.user);
                }
                else {
                    Picasso.get().load(urlLink).into(profileImage);
                    setImage = true;
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StudentProfile.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadTask!=null && uploadTask.isInProgress())
                {
                    Toast.makeText(StudentProfile.this,"Uploading...",Toast.LENGTH_LONG).show();
                }else {
                    uploadFile();
                }
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar_profile.setVisibility(View.VISIBLE);

                String name = fullName.getText().toString().trim();
                String pno = mobileNumber.getText().toString().trim();
                String userEmail = email.getText().toString();

                boolean flag = false;
                if(!name.isEmpty()) {
                    FirebaseDatabase.getInstance().getReference("student").child(uid).child("RegistrationRecord").child("fullName").setValue(name);
                    flag = true;
                }
                if(!pno.isEmpty())
                {
                    FirebaseDatabase.getInstance().getReference("student").child(uid).child("RegistrationRecord").child("mobileNumber").setValue(pno);
                    flag = true;
                }
                if(flag)
                {
                    Toast.makeText(StudentProfile.this,"Successfully Profile Updated",Toast.LENGTH_SHORT);
                    progressBar_profile.setVisibility(View.INVISIBLE);
                }

            }
        });

    }
     private TextWatcher enableUpdateButtonWatcher = new TextWatcher(){
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {

         }

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
            String fullname = fullName.getText().toString().trim();
            String mobilenumber = mobileNumber.getText().toString().trim();
            update.setEnabled(!fullname.isEmpty() || !mobilenumber.isEmpty() || enableUpdateButton );
         }

         @Override
         public void afterTextChanged(Editable s) {

         }
     };
    private void openImageFrom(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode == IMAGE_CODE && resultCode == RESULT_OK && data!=null && data.getData()!= null){

            imageUri = data.getData();
            Cursor returnCursor = getContentResolver().query(imageUri,null,null,null,null);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            Long l = returnCursor.getLong(sizeIndex);
            long size = l/1000;
            profileImageError = findViewById(R.id.textView_profileImageError);
            if(size<=100) {
                profileImage.setImageURI(imageUri);
                uploadPhoto.setEnabled(true);
                profileImageError.setText("");
            }

            else {
                imageUri = null;
                profileImageError.setText("Image size is greater than 100 Kb");
                uploadPhoto.setEnabled(false);
              if(setImage)
              {
                  databaseReference.addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          String url = dataSnapshot.child("imguri").getValue(String.class);
                          Picasso.get().load(url).into(profileImage);
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });
              }else{

              }
            }

        }
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver cR = getContentResolver();
        MimeTypeMap  mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void uploadFile()
    {
        if(imageUri!=null)
        {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(StudentProfile.this,"Photo uploaded successfully", Toast.LENGTH_LONG).show();
                    uploadPhoto.setEnabled(false);
                    uploadPhoto.setText("Change Photo");

                    //UploadStudentPhoto usp = new UploadStudentPhoto(email.getText().toString(),taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                    String uploadId = databaseReference.push().getKey();
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            databaseReference.child("imguri").setValue(uri.toString());
                            setImage = true;
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(StudentProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadPhoto.setEnabled(false);
                    Toast.makeText(StudentProfile.this,"Uploading....",Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Toast.makeText(StudentProfile.this,"No image selected",Toast.LENGTH_SHORT).show();
        }
    }
}

