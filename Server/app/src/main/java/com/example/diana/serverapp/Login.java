package com.example.diana.serverapp;

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

import com.example.diana.serverapp.Common.Common;
import com.example.diana.serverapp.Model.User;
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

    EditText email,password;
    Button login;
    TextView forgotTextLink;
    ProgressBar progressBar;

    FirebaseDatabase database;
    DatabaseReference table_user;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=(EditText)findViewById(R.id.etEmail);
        password=(EditText)findViewById(R.id.etParola);
        login=(Button)findViewById(R.id.loginBtn);
        forgotTextLink=(TextView)findViewById(R.id.forgotPass) ;
        progressBar=(ProgressBar)findViewById(R.id.progressBar) ;

        database=FirebaseDatabase.getInstance();
        table_user=database.getReference("User");


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e = email.getText().toString().trim();
                String p = password.getText().toString().trim();
                signIn(e,p);

            }

        });
        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordAlertDialog = new AlertDialog.Builder(v.getContext());
                passwordAlertDialog.setTitle("Reset Passowrd");
                passwordAlertDialog.setMessage("Enter Your Email To Recived Reset Link ");
                passwordAlertDialog.setView(resetMail);

                passwordAlertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //extract the mail and send reset link

                        String mail = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Login.this, "Reset Link Send To Your Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Error ! Reset Link is Not Send" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void signIn(final String emailS, final String parola){

        if (TextUtils.isEmpty(emailS)) {
            email.setError("Email is required.");
            return;
        }
        if (TextUtils.isEmpty(parola)) {
            password.setError("Password is required.");
            return;
        }

        fAuth=FirebaseAuth.getInstance();
        progressBar.setVisibility(View.VISIBLE);

        fAuth.signInWithEmailAndPassword(emailS, parola).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user=fAuth.getCurrentUser();
                    final String uid=user.getUid();
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            User u = dataSnapshot.child(uid).getValue(User.class);
                            if (Boolean.parseBoolean(u.getIsStaff())) {
                                Toast.makeText(Login.this, "Login succesfuly", Toast.LENGTH_SHORT).show();
                                Common.currentUser = u;
                                 startActivity(new Intent(getApplicationContext(), Home.class));
                                finish();
                            } else
                                Toast.makeText(Login.this, "Please login with Staff account", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Error" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }



}