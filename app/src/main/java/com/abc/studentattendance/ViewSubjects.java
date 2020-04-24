package com.abc.studentattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewSubjects extends AppCompatActivity {

    private Spinner branch;
    private LinearLayout linearLayout;
    private DatabaseReference databaseReference;
    private String year="",selectedItem="";
    private Button btn_updateSubjects;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_subjects);
        year = getIntent().getStringExtra("YEAR");
        getSupportActionBar().setTitle(year+" Subjects");
        branch = findViewById(R.id.spinner_studentBranch);
        linearLayout = findViewById(R.id.linearLayout_viewSubjects);
        btn_updateSubjects = findViewById(R.id.button_updateSubjects);



        ArrayAdapter<String> branchesAdapter = new ArrayAdapter<String>(ViewSubjects.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.BranchNames));
        branchesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branch.setAdapter(branchesAdapter);

        selectedItem = branch.getSelectedItem().toString();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Admin").child(year);

        branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final ProgressDialog pd = new ProgressDialog(ViewSubjects.this);
                pd.setCanceledOnTouchOutside(false);
                pd.setMessage("Record Fetching...");
                pd.show();
                selectedItem = parent.getItemAtPosition(position).toString();


                databaseReference.child(selectedItem).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        while(linearLayout.getChildCount()>2)
                        {
                            linearLayout.removeViewAt(linearLayout.getChildCount()-1);
                        }

                        int count = (int)dataSnapshot.getChildrenCount();
                        for(int i=1;i<=count;i++) {
                            EditText et = new EditText(ViewSubjects.this);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            et.setLayoutParams(lp);
                            et.setHint(dataSnapshot.child(i + "").getValue(String.class));
                            linearLayout.addView(et);
                        }
                        pd.dismiss();
                        btn_updateSubjects.setVisibility(View.VISIBLE);
                        if(linearLayout.getChildCount()<3)
                        {
                            TextView tv = new TextView(ViewSubjects.this);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            tv.setLayoutParams(lp);
                            tv.setTextSize(20);
                            btn_updateSubjects.setVisibility(View.INVISIBLE);
                            tv.setText("Sorry, No Subjects here!");
                            linearLayout.addView(tv);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ViewSubjects.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_updateSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSubjects();
            }
        });
    }
    private void updateSubjects(){
        int noe = linearLayout.getChildCount()-1;
        System.out.println(noe);
        boolean flag = false;
        for(int i=2;i<=noe;i++)
        {
            EditText et = (EditText)linearLayout.getChildAt(i);
            System.out.println(linearLayout.getChildCount());
            if(!et.getText().toString().trim().isEmpty())
            {
                databaseReference.child(selectedItem).child((i-1)+"").setValue(et.getText().toString().trim());
                flag = true;
            }
        }
        if(!flag)
        {
            Toast.makeText(ViewSubjects.this,"No edit for update",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(ViewSubjects.this,"Subjects updated successfully",Toast.LENGTH_SHORT).show();
        }
    }
}
