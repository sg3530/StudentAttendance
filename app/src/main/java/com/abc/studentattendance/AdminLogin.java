package com.abc.studentattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminLogin extends AppCompatActivity {

    private EditText editText_adminLogin;
    private EditText editText_adminPassword;
    private Button button_adminLoginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        getSupportActionBar().setTitle("Admin Login");
        editText_adminLogin = findViewById(R.id.ediText_adminLoginEmail);
        editText_adminPassword = findViewById(R.id.editText_adminLoginPassword);
        button_adminLoginButton = findViewById(R.id.button_adminLogin);



        button_adminLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog pd = new ProgressDialog(AdminLogin.this);
                pd.setMessage("Login...");
                pd.show();
                String email = editText_adminLogin.getText().toString().trim();
                String password = editText_adminPassword.getText().toString().trim();
                if(email.isEmpty())
                {
                    editText_adminLogin.setError("This field is required");
                    pd.dismiss();
                    return;
                }
                if(password.isEmpty())
                {
                    editText_adminPassword.setError("This field is required");
                    pd.dismiss();
                    return;
                }
                if(!email.equals("admin@gmail.com"))
                {
                    Toast.makeText(AdminLogin.this,"Wrong email or password",Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    return;
                }
                if(!password.equals("admin@3530"))
                {
                    Toast.makeText(AdminLogin.this,"Wrong email or password",Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    return;
                }
                pd.dismiss();
                Intent intent = new Intent(AdminLogin.this,AdminDashboard.class);
                startActivity(intent);
                finish();
            }
        });





    }
}
