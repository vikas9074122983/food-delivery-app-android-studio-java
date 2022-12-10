package org.ganeshatech18102021.fooddelivery.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.ganeshatech18102021.fooddelivery.R;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class aboutsetting extends AppCompatActivity {

    EditText cname,about;
    Button save;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutsetting);
        cname = findViewById(R.id.co_name);
        about = findViewById(R.id.co_about);
        save = findViewById(R.id.save_abtsetting);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Saving data please wait..");

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cn = cname.getText().toString();
                String abt = about.getText().toString();
                if (cn.isEmpty()){
                    cname.setError("Field Required..");
                    return;
                }
                if (abt.isEmpty()){
                    about.setError("Field Required..");
                    return;
                }
                dialog.show();
                DocumentReference dref = FirebaseFirestore.getInstance().collection("setting").document("about");
                dref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            dref.update("company",cn,"about",abt).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                    Toast.makeText(aboutsetting.this, "Data Updated", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(aboutsetting.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Map<String,Object> data = new HashMap<>();
                            data.put("company",cn);
                            data.put("about",abt);
                            dref.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    dialog.dismiss();
                                    Toast.makeText(aboutsetting.this, "Data Updated", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(aboutsetting.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(aboutsetting.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("setting").document("about");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    String nn = task.getResult().getString("company");
                    String cabt = task.getResult().getString("about");
                    if (nn != null){
                        cname.setText(nn);
                    }
                    if (cabt != null){
                        about.setText(cabt);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("TAG", "onFailure: "+e.getMessage());
            }
        });
    }
}