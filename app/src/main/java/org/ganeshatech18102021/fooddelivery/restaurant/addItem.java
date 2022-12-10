package org.ganeshatech18102021.fooddelivery.restaurant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.ganeshatech18102021.fooddelivery.R;
import org.ganeshatech18102021.fooddelivery.RestOwnerActivity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class addItem extends AppCompatActivity {
    ImageView img;
    EditText pname,pprice;
    Button save;
    String Uid;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    StorageReference fStorage;
    DocumentReference documentReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    Uri newImgUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        img = findViewById(R.id.add_pr_img);
        pname = findViewById(R.id.add_pr_name);
        pprice = findViewById(R.id.add_pr_price);
        save = findViewById(R.id.add_pr_save);

        documentReference = fStore.collection("users").document(Uid).collection("product").document();
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait ....");

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(addItem.this)
                        .crop()
                        .maxResultSize(300, 300)
                        .start();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploaddata();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        newImgUri = data.getData();
        Picasso.get().load(newImgUri).into(img);
    }

    private void uploaddata() {
        String name = pname.getText().toString();
        String pr = pprice.getText().toString();

        if (name.isEmpty()){
            pname.setError("Name Required");
            return;
        }
        if (pr.isEmpty()){
            pprice.setError("Enter Price");
            return;
        }
        if (newImgUri == null){
            Toast.makeText(addItem.this, "Please select an Image", Toast.LENGTH_SHORT).show();
            return;
        }
        long price = Long.parseLong(pr);
        progressDialog.show();
        final StorageReference reference = storageReference.child(System.currentTimeMillis()+".jpg");
        UploadTask uploadTask = reference.putFile(newImgUri);
        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    DocumentReference documentReference = fStore.collection("users").document(Uid).collection("product").document();
                    Map<String,Object> user = new HashMap<>();
                    user.put("name",name);
                    user.put("url",downloadUri.toString());
                    user.put("price",price);
                    user.put("id",documentReference.getId());
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(addItem.this, "Item Successfully saved", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), RestOwnerActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(addItem.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(addItem.this, "Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}