package com.example.diana.dreamcakes;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diana.dreamcakes.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText txt_fullName,txt_email,txt_password,txt_phone;
    Button btn_register;
    TextView btn_login;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txt_fullName=findViewById(R.id.etNume);
        txt_email=findViewById(R.id.etEmail);
        txt_password=findViewById(R.id.etParola);
        txt_phone=findViewById(R.id.etTelefon);
        btn_register=findViewById(R.id.registerBtn);
        btn_login=findViewById(R.id.loginBtn);
        progressBar=findViewById(R.id.progressBar);

        fAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("User");

        //if(fAuth.getCurrentUser()!=null)
        // startActivity(new Intent(getApplicationContext(),MainActivity.class));

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sEmail=txt_email.getText().toString();
                final String password=txt_password.getText().toString();
                final String phone=txt_phone.getText().toString();
                final String fullName=txt_fullName.getText().toString();

                if(TextUtils.isEmpty(sEmail)) {
                    txt_email.setError("Email is required.");
                    return;
                }
                if(TextUtils.isEmpty(password)) {
                    txt_password.setError("Password is required.");
                    return;
                }
                if(TextUtils.isEmpty(fullName)) {
                    txt_fullName.setError("Name is required.");
                    return;
                }
                if(TextUtils.isEmpty(phone)) {
                    txt_phone.setError("Phone is required.");
                    return;
                }

                if(password.length()<6){
                    txt_password.setError("Password must be 6 or more characters.");
                    return;
                }
                txt_phone.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String regex = "\\d{10}";
                        boolean a = txt_phone.toString().matches(regex);
                        if (txt_phone.getText().toString().length() <= 0) {
                            txt_phone.setError("Enter your phone number ");
                        }
                        else if(!txt_phone.getText().toString().matches(regex)){
                            txt_phone.setError("Please enter a valid phone number!");

                        }
                        else {
                            txt_phone.setError(null);

                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                progressBar.setVisibility(View.VISIBLE);


                FirebaseAuth.getInstance().createUserWithEmailAndPassword(sEmail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final User newUser = new User(
                                    fullName,
                                    sEmail,
                                    phone
                            );
                            FirebaseDatabase.getInstance().getReference("User")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(),Home.class));

                                }
                            });
                        } else {
                            Toast.makeText(Register.this,  task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                        }


                    }
                });
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }
}