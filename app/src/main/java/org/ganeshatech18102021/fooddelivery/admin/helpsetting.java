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

public class helpsetting extends AppCompatActivity {
    EditText num,wpnum,mail;
    Button save;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpsetting);

        num = findViewById(R.id.shelp_num);
        wpnum = findViewById(R.id.swp_num);
        mail = findViewById(R.id.shelp_mail);
        save = findViewById(R.id.save_helpsetting);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Saving data please wait..");

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nnum = num.getText().toString();
                String wpnnum = wpnum.getText().toString();
                String mmail = mail.getText().toString();
                if (nnum.isEmpty()){
                    num.setError("Field Required..");
                    return;
                }
                if (wpnnum.isEmpty()){
                    wpnum.setError("Field Required..");
                    return;
                }
                if (mmail.isEmpty()){
                    mail.setError("Field Required..");
                    return;
                }
                dialog.show();
                DocumentReference dref = FirebaseFirestore.getInstance().collection("setting").document("help");
                dref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            dref.update("number",nnum,"wpnumber",wpnnum,"mail",mmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                    Toast.makeText(helpsetting.this, "Data Updated", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(helpsetting.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Map<String,Object> data = new HashMap<>();
                            data.put("number",nnum);
                            data.put("wpnumber",wpnnum);
                            data.put("mail",mmail);
                            dref.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    dialog.dismiss();
                                    Toast.makeText(helpsetting.this, "Data Updated", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(helpsetting.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(helpsetting.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("setting").document("help");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    String nn = task.getResult().getString("number");
                    String cabt = task.getResult().getString("wpnumber");
                    String ma = task.getResult().getString("mail");
                    if (nn != null){
                        num.setText(nn);
                    }
                    if (cabt != null){
                        wpnum.setText(cabt);
                    }
                    if (ma != null){
                        mail.setText(ma);
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