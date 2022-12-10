package org.ganeshatech18102021.fooddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.ganeshatech18102021.fooddelivery.restaurant.Receivedorder;
import org.ganeshatech18102021.fooddelivery.restaurant.RestSetting;
import org.ganeshatech18102021.fooddelivery.restaurant.addItem;
import org.ganeshatech18102021.fooddelivery.restaurant.manageItem;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RestOwnerActivity extends AppCompatActivity {
    private Button logoutbtn;
    private TextView oname,oemail,ophone,warn;
    private ShapeableImageView oimg;
    private CardView additem,manageitem,setting,receivedorder;
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    String photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_owner);

        logoutbtn = findViewById(R.id.logout_button_rest);
        oname = findViewById(R.id.officer_name);
        oemail = findViewById(R.id.officer_email);
        ophone = findViewById(R.id.officer_phone);
        oimg = findViewById(R.id.officer_image);
        additem = findViewById(R.id.mark_officer_attbtn);
        setting = findViewById(R.id.pending_userbtn);
        manageitem = findViewById(R.id.officer_empbtn);
        receivedorder = findViewById(R.id.received_order);
        warn = findViewById(R.id.warn);

        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), addItem.class));
            }
        });
        manageitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), manageItem.class));
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RestSetting.class));
            }
        });
        receivedorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Receivedorder.class));
            }
        });
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            DocumentReference dr = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()){
                        String adname = task.getResult().getString("name");
                        photo = task.getResult().getString("url");
                        String ademail = task.getResult().getString("email");
                        String adphone = task.getResult().getString("phone");

                        String rlocation = task.getResult().getString("address");
                        if (rlocation == null){
                            warn.setVisibility(View.VISIBLE);
                        }

                        oname.setText(adname);
                        oemail.setText(ademail);
                        ophone.setText(adphone);
                        Picasso.get().load(photo).into(oimg);

                    } else {
                        Toast.makeText(RestOwnerActivity.this, "No Profile Exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(RestOwnerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {

        }
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