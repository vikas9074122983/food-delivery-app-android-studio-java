package org.ganeshatech18102021.fooddelivery.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.ganeshatech18102021.fooddelivery.R;
import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class userInfo extends AppCompatActivity {

    CircleImageView img;
    TextView name,mail,phone,address;
    Button edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        img = findViewById(R.id.up_imageView);
        name = findViewById(R.id.up_name);
        mail = findViewById(R.id.up_email);
        phone = findViewById(R.id.up_phone);
        address = findViewById(R.id.apa_add);
        edit = findViewById(R.id.edit_btn);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    String na = task.getResult().getString("name");
                    String em = task.getResult().getString("email");
                    String ph = task.getResult().getString("phone");
                    String add = task.getResult().getString("address");
                    String url = task.getResult().getString("url");
                    if (na != null){
                        name.setText(na);
                    }
                    if (em != null){
                        mail.setText(em);
                    }
                    if (ph != null){
                        phone.setText(ph);
                    }
                    if (add != null){
                        address.setText(add);
                    }
                    if (url != null){
                        Picasso.get().load(url).into(img);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(userInfo.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(userInfo.this,usersetting.class));
                finish();
            }
        });
    }
}