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
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class Login extends AppCompatActivity {

    EditText email;
    EditText password;
    Button login;
    TextView registerBtn,forgotTextLink;
    ProgressBar progressBar;
    CheckBox cbRemember;
    FirebaseAuth fAuth;
    String name,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=(EditText)findViewById(R.id.etEmail);
        password=(EditText)findViewById(R.id.etParola);
        login=(Button)findViewById(R.id.loginBtn);
        progressBar=( ProgressBar)findViewById(R.id.progressBar);
        registerBtn=(TextView)findViewById(R.id.tView);
        forgotTextLink=(TextView)findViewById(R.id.forgotPass);
        cbRemember=(CheckBox)findViewById(R.id.remember);

        Paper.init(this);

        //init Firebase
        fAuth=FirebaseAuth.getInstance();
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    final String sEmail = email.getText().toString().trim();
                    String parola = password.getText().toString().trim();

                    if (cbRemember.isChecked()) {
                        Paper.book().write(Common.USER_KEY, email.getText().toString());
                        Paper.book().write(Common.PWD_KEY, password.getText().toString());

                    }

                    if (TextUtils.isEmpty(sEmail)) {
                        email.setError("Email is required.");
                        return;
                    }
                    if (TextUtils.isEmpty(parola)) {
                        password.setError("Password is required.");
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);

                    fAuth.signInWithEmailAndPassword(sEmail, parola).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = fAuth.getCurrentUser();
                                final String uid = user.getUid();
                                table_user.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        User u = new User(dataSnapshot.child(uid).getValue(User.class));
                                        Common.currentUser = u;
                                        //  goToHome();
                                      startActivity(new Intent(getApplicationContext(), Home.class));
                                        finish();


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

                }else{
                    Toast.makeText(getApplicationContext(), "Please check your connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
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

    }

