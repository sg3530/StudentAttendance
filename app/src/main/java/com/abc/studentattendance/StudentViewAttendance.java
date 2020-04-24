package com.abc.studentattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.util.JsonUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;
import org.w3c.dom.ls.LSOutput;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class StudentViewAttendance extends AppCompatActivity {
    private CalendarView calendarView;
    private  Date date;
    private String formattedDate="",onClickDate="";
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private String studentEmail = "",studentYear="",studentBranch="",uid="";
    private String []studentSubjects;
    private LinearLayout linearLayout;
    private ProgressBar progressBar_pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_view_attendance);
        calendarView = findViewById(R.id.calendarView_studentAttendance);
        linearLayout = findViewById(R.id.linearLayout_StudentViewAttendance);
        progressBar_pb = findViewById(R.id.progressBar_studentViewAttendance);
        date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        formattedDate = sdf.format(date);

        StringTokenizer st = new StringTokenizer(formattedDate,"/");
        String month = st.nextToken();
        String day = st.nextToken();
        String year = st.nextToken();
        formattedDate = day+"/"+month+"/"+year;
        System.out.println(formattedDate);
        uid = getIntent().getStringExtra("UID");
        studentBranch = getIntent().getStringExtra("Branch");
        studentYear = getIntent().getStringExtra("Year");

        progressBar_pb.setVisibility(View.VISIBLE);
       databaseReference = FirebaseDatabase.getInstance().getReference("Admin").child(studentYear).child(studentBranch);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int n = (int)dataSnapshot.getChildrenCount();
                studentSubjects = new String[n];
                System.out.println(n);

                for(int i=1;i<=n;i++)
                {
                    studentSubjects[i-1] = dataSnapshot.child(i+"").getValue(String.class);
                }
                databaseReference = FirebaseDatabase.getInstance().getReference().child("student").child(uid).child("Attendance");
                databaseReference.child(formattedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int n = (int)dataSnapshot.getChildrenCount();
                        System.out.println(n);

                        for(int i=0;i<studentSubjects.length;i++)
                        {
                            System.out.println(dataSnapshot.child(studentSubjects[i]).getValue(String.class));

                            if(dataSnapshot.hasChild(studentSubjects[i]))
                            {

                                LinearLayout  ll = new LinearLayout(StudentViewAttendance.this);
                                ll.setOrientation(LinearLayout.HORIZONTAL);
                                TextView tv1 = new TextView(StudentViewAttendance.this);
                                ImageView iv = new ImageView(StudentViewAttendance.this);
                                LinearLayout.LayoutParams lptv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                tv1.setLayoutParams(lptv);
                                LinearLayout.LayoutParams lpiv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                lpiv.topMargin = 5;
                                iv.setLayoutParams(lpiv);
                                tv1.setTextSize(20);
                                iv.setImageResource(R.drawable.ic_markattendance);
                                tv1.setText(studentSubjects[i]);
                                ll.addView(tv1);
                                ll.addView(iv);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                ll.setLayoutParams(lp);
                                linearLayout.addView(ll);
                            }
                        }
                        progressBar_pb.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


       // databaseReference = FirebaseDatabase.getInstance().getReference().child("student").child(studentEmail).child("Attendance");
       // viewStudentAttendance();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                if((month+1)<10)
                {
                    onClickDate = dayOfMonth+"/0"+(month+1)+"/"+year;
                }else{
                    onClickDate = dayOfMonth+"/"+(month+1)+"/"+year;
                }
                    viewStudentAttendance();

            }
        });

    }
    private void viewStudentAttendance(){

        while(linearLayout.getChildCount()!=0)
        {
            linearLayout.removeViewAt(linearLayout.getChildCount()-1);
        }
        progressBar_pb.setVisibility(View.VISIBLE);
         databaseReference = FirebaseDatabase.getInstance().getReference("Admin").child(studentYear).child(studentBranch);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int n = (int)dataSnapshot.getChildrenCount();
                studentSubjects = new String[n];
                System.out.println(n);

                for(int i=1;i<=n;i++)
                {
                    studentSubjects[i-1] = dataSnapshot.child(i+"").getValue(String.class);
                }

                 databaseReference = FirebaseDatabase.getInstance().getReference().child("student").child(uid).child("Attendance");
                databaseReference.child(onClickDate).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int n = (int)dataSnapshot.getChildrenCount();
                        System.out.println(onClickDate);
                        System.out.println(n);
                        for(int i=0;i<studentSubjects.length;i++)
                        {
                            System.out.println(dataSnapshot.hasChild(""));
                            if(dataSnapshot.hasChild(studentSubjects[i]))
                            {

                                LinearLayout  ll = new LinearLayout(StudentViewAttendance.this);
                                ll.setOrientation(LinearLayout.HORIZONTAL);
                                TextView tv1 = new TextView(StudentViewAttendance.this);
                                ImageView iv = new ImageView(StudentViewAttendance.this);
                                LinearLayout.LayoutParams lptv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                tv1.setLayoutParams(lptv);
                                LinearLayout.LayoutParams lpiv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                lpiv.topMargin = 5;
                                iv.setLayoutParams(lpiv);
                                tv1.setTextSize(20);
                                iv.setImageResource(R.drawable.ic_markattendance);
                                tv1.setText(studentSubjects[i]);
                                ll.addView(tv1);
                                ll.addView(iv);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                ll.setLayoutParams(lp);
                                linearLayout.addView(ll);
                            }
                        }
                        progressBar_pb.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
