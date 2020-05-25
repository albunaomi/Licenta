package com.example.diana.dreamcakes;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diana.dreamcakes.Common.Common;
import com.example.diana.dreamcakes.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    EditText email;
    EditText password;
    Button login;
    TextView registerBtn,forgotTextLink;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    String name,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=findViewById(R.id.etEmail);
        password=findViewById(R.id.etParola);
        login=findViewById(R.id.loginBtn);
        progressBar=findViewById(R.id.progressBar);
        fAuth=FirebaseAuth.getInstance();
        registerBtn=findViewById(R.id.tView);
        forgotTextLink=findViewById(R.id.forgotPass);

        //init Firebase

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sEmail=email.getText().toString().trim();
                String parola=password.getText().toString().trim();


                if(TextUtils.isEmpty(sEmail)) {
                    email.setError("Email is required.");
                    return;
                }
                if(TextUtils.isEmpty(parola)) {
                    password.setError("Password is required.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(sEmail,parola).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Login succesfuly", Toast.LENGTH_SHORT).show();
                            FirebaseUser user=fAuth.getCurrentUser();
                            setCurrentUser(user);
                            startActivity(new Intent(getApplicationContext(),Home.class));
                        }else{
                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });

            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail=new EditText(v.getContext());
                AlertDialog.Builder passwordAlertDialog=new AlertDialog.Builder(v.getContext());
                passwordAlertDialog.setTitle("Reset Passowrd");
                passwordAlertDialog.setMessage("Enter Your Email To Recived Reset Link ");
                passwordAlertDialog.setView(resetMail);

                passwordAlertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //extract the mail and send reset link

                        String mail=resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Login.this, "Reset Link Send To Your Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Error ! Reset Link is Not Send"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passwordAlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close the dialog

                    }
                });
                passwordAlertDialog.create().show();
            }
        });

    }
    public void setCurrentUser(FirebaseUser user){

        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");
        final String uid=user.getUid();
        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u=dataSnapshot.child(uid).getValue(User.class);
               Common.currentUser=u;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error"+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    }

