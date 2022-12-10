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

public class aboutCompany extends AppCompatActivity {

    TextView cname,about;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_company);
        cname = findViewById(R.id.c_name);
        about = findViewById(R.id.c_about);

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