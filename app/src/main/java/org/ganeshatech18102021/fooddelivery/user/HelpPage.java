package org.ganeshatech18102021.fooddelivery.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.ganeshatech18102021.fooddelivery.R;
import org.jetbrains.annotations.NotNull;

public class HelpPage extends AppCompatActivity {

    TextView num,wpnum,mail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page);
        num = findViewById(R.id.help_num);
        wpnum = findViewById(R.id.wp_num);
        mail = findViewById(R.id.help_mail);

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