package com.example.diana.dreamcakes;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    EditText txt_fullName,txt_username,txt_email,txt_password,txt_phone;
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
        txt_username=findViewById(R.id.etUsername);
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
                final String userName=txt_username.getText().toString();
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
                    txt_password.setError("Name is required.");
                    return;
                }
                if(TextUtils.isEmpty(phone)) {
                    txt_password.setError("Phone is required.");
                    return;
                }
                if(TextUtils.isEmpty(userName)) {
                    txt_password.setError("Username is required.");
                    return;
                }
                if(password.length()<6){
                    txt_password.setError("Password must be 6 or more characters.");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                final User newUser=new User(
                        fullName,
                        userName,
                        sEmail,
                        phone
                );
                databaseReference.push().setValue(newUser);
              //  databaseReference.child(newUser.getFullName()).setValue(newUser);
                Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
               /*fAuth.createUserWithEmailAndPassword(sEmail, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final User newUser=new User(
                                            fullName,
                                            userName,
                                            sEmail,
                                            phone
                                    );

                                    FirebaseDatabase.getInstance().getReference("User").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.child(sEmail).exists()){
                                                Toast.makeText(Register.this, "Email already register", Toast.LENGTH_SHORT).show();
                                            }
                                            else {

                                                databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(newUser);
                                                Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }


                                    });
                                } else {
                                   Toast.makeText(Register.this, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);

                                }

                            }
                        });*/
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