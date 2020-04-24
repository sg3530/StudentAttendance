package com.abc.studentattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminDashboard extends AppCompatActivity {


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_add_student_subjects,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.item_1styear)
        {
            Intent intent = new Intent(AdminDashboard.this,ViewSubjects.class);
            intent.putExtra("YEAR","1st Year");
            startActivity(intent);
            return true;
        }
        if(id == R.id.item_2ndyear)
        {
            Intent intent = new Intent(AdminDashboard.this,ViewSubjects.class);
            intent.putExtra("YEAR","2nd Year");
            startActivity(intent);
            return true;
        }
        if(id == R.id.item_3rdyear)
        {
            Intent intent = new Intent(AdminDashboard.this,ViewSubjects.class);
            intent.putExtra("YEAR","3rd Year");
            startActivity(intent);
            return true;
        }
        if(id == R.id.item_4thyear)
        {
            Intent intent = new Intent(AdminDashboard.this,ViewSubjects.class);
            intent.putExtra("YEAR","4th Year");
            startActivity(intent);
            return true;
        }
        return true;
    }



    private Spinner branch;
    private Spinner year;
    private Button button_add;
    private Button button_remove;
    private Button button_submit;
    private LinearLayout linearLayout;
    private DatabaseReference databaseReference;
    private ArrayList<String> al;

    private int nol = 0;
    private int nos = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        getSupportActionBar().setTitle("Welcome:Admin");
        year = findViewById(R.id.spinner_studentYear);
        branch = findViewById(R.id.spinner_studentBranch);

        button_add = findViewById(R.id.button_addEditText);
        button_remove = findViewById(R.id.button_removeEditText);
        button_submit = findViewById(R.id.button_submitSubjects);
        linearLayout = findViewById(R.id.linearLayout_adminDashboard);


        if(linearLayout.getChildCount()<=5)
        {
            button_remove.setVisibility(View.INVISIBLE);
            button_submit.setVisibility(View.INVISIBLE);
        }

        ArrayAdapter<String> branchesAdapter = new ArrayAdapter<String>(AdminDashboard.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.BranchNames));
        branchesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branch.setAdapter(branchesAdapter);

        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<String>(AdminDashboard.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.YearNames));
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year.setAdapter(yearsAdapter);

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEditText();
            }
        });
        button_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEditText();
            }
        });



        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 al = new ArrayList<String>();
                final ProgressDialog pd = new ProgressDialog(AdminDashboard.this);
                pd.setMessage("Subjects are adding...");
                pd.show();
            for(int i=4;i<nol+4;i++)
            {

                EditText et = (EditText)linearLayout.getChildAt(i);
                String text = et.getText().toString().trim();
                if(text.isEmpty())
                {
                    pd.dismiss();
                    et.setError("This field is required");
                    return;
                }
                al.add(text);
            }
                String studentBranch = branch.getSelectedItem().toString().trim();
                String studentYear = year.getSelectedItem().toString().trim();
                System.out.println(studentYear);

                databaseReference = FirebaseDatabase.getInstance().getReference().child("Admin").child(studentYear).child(studentBranch);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        System.out.println(dataSnapshot.getChildrenCount());
                        nos = (int)dataSnapshot.getChildrenCount();

                        int j = nos+1;
                        for(String subject:al)
                        {
                            databaseReference.child(j+"").setValue(subject);
                            j++;
                        }
                        pd.dismiss();
                        Toast.makeText(AdminDashboard.this,"Subjects successfully added",Toast.LENGTH_SHORT).show();
                        for(int i=3+nol;i>3;i--)
                        {
                            linearLayout.removeViewAt(i);
                        }
                        nol=0;
                        button_remove.setVisibility(View.INVISIBLE);
                        button_submit.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    private void addEditText(){

        EditText et = new EditText(AdminDashboard.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        et.setLayoutParams(lp);
        et.setHint("Enter subject name");
        et.setId(nol+1);
        linearLayout.addView(et);

        nol++;
        if(nol>0){
            button_remove.setVisibility(View.VISIBLE);
            button_submit.setVisibility(View.VISIBLE);
        }
    }
    private void removeEditText(){

        linearLayout.removeViewAt(linearLayout.getChildCount()-1);
        nol--;
        if(nol<1)
        {
            button_remove.setVisibility(View.INVISIBLE);
            button_submit.setVisibility(View.INVISIBLE);
        }
    }
}
