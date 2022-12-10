package org.ganeshatech18102021.fooddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RestRegister extends AppCompatActivity {
    EditText regname,regmail,regmob,regpass;
    TextView signupbtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ProgressDialog progressDialog;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rest_register);
        regname = findViewById(R.id.red_username);
        regmail = findViewById(R.id.red_email);
        regpass = findViewById(R.id.red_password);
        regmob = findViewById(R.id.red_alternatmob);
        signupbtn = findViewById(R.id.rbtn_sign);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait we are creating your account....");

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });
    }

    private void uploadData() {
        String name = regname.getText().toString();
        String email = regmail.getText().toString();
        String phone = regmob.getText().toString();
        String pass = regpass.getText().toString();

        if (name.isEmpty()){
            regname.setError("Restaurant Name Required");
            return;
        }
        if (email.isEmpty()){
            regmail.setError("Email Required");
            return;
        }
        if (phone.isEmpty()){
            regmob.setError("Phone Number Required");
            return;
        }
        if (pass.isEmpty()){
            regpass.setError("password Required ");
            return;
        }
        if (pass.length() <= 5){
            regpass.setError("Password length should be more than six latter");
        }

        progressDialog.show();
        fAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    userID = task.getResult().getUser().getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    Map<String,Object> user = new HashMap<>();
                    user.put("name",name);
                    user.put("email",email);
                    user.put("phone",phone);
                    user.put("access","restaurant");
                    user.put("address",null);
                    user.put("id",userID);
                    user.put("deliverycharge",0);
                    user.put("city",null);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(RestRegister.this, "User Successfully Registered", Toast.LENGTH_SHORT).show();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(),RestOwnerActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RestRegister.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    });
                }else {


                }

            }
        });
    }

    int backButtonCount;
    public void onBackPressed()
    {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }
}