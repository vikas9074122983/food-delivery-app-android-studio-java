package org.ganeshatech18102021.fooddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText email,pass;
    private TextView fogotpass,signup;
    private Button login;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.loginemial);
        pass = findViewById(R.id.loginpass);
        fogotpass = findViewById(R.id.forgotpass);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.loginbtn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait Logging in....");

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,Register.class));
                finish();
            }
        });
        fogotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ForgotPassActivity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = email.getText().toString();
                String passw = pass.getText().toString();

                if (mail.isEmpty()){
                    email.setError("Email Required");
                    return;
                }
                if (passw.isEmpty()){
                    pass.setError("Password Required");
                    return;
                }
                progressDialog.show();
                fAuth.signInWithEmailAndPassword(email.getText().toString(),pass.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        CheckUserAccessLevel(authResult.getUser().getUid());
//                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
//                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void CheckUserAccessLevel(String uid) {
        DocumentReference df = FirebaseFirestore.getInstance().collection("users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "onSuccess: " + documentSnapshot.getData());
                String dt = documentSnapshot.getString("access");
                if (dt.equals("admin")) {
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Intent intent = new Intent(LoginActivity.this, AdminHomepage.class);
                    startActivity(intent);
                    finish();
                }else if (dt.equals("user")){
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    startActivity(new Intent(getApplicationContext(),UserHomepage.class));
                    finish();
                }else if (dt.equals("restaurant")){
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    startActivity(new Intent(getApplicationContext(),RestOwnerActivity.class));
                    finish();
                } else {
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    FirebaseAuth.getInstance().signOut();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            progressDialog.show();
            CheckUserAccessLevel((FirebaseAuth.getInstance().getCurrentUser().getUid()));
        } else {
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }
//            fAuth.signOut();
        }
    }

    //    int backButtonCount;
//    public void onBackPressed()
//    {
//        if(backButtonCount >= 1)
//        {
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }
//        else
//        {
//            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
//            backButtonCount++;
//        }
//    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Exit");
        builder.setMessage("Do you want Exit ? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.show();
    }
}