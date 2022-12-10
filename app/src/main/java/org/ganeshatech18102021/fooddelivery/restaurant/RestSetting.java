package org.ganeshatech18102021.fooddelivery.restaurant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.ganeshatech18102021.fooddelivery.R;
import org.ganeshatech18102021.fooddelivery.RestOwnerActivity;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RestSetting extends AppCompatActivity {
    CircleImageView upprofileImageView;
    EditText upname,upphone,delcharge,city;
    TextView uplocation;
    Button savebtn;
    FloatingActionButton pickimgbtn;
    Uri imageUri;
    Uri newImgUri;
    Uri updateUri;
    String chrg;
    String cityname;
    boolean tfval = false ;
    String myUrl = "";
    StorageReference storageProfilePrictureRef;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    StorageReference fStorage;
    DocumentReference documentReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String userID;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_setting);

        Places.initialize(getApplicationContext(),"AIzaSyBpkX3iFEKiCtN2Q1AsIRkkKe8lQE4lf8s");

        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        documentReference = fStore.collection("users").document(userID);
        storageReference = FirebaseStorage.getInstance().getReference("Profile Images");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait updating data....");
        upprofileImageView = findViewById(R.id.re_profile_image);
        upname = findViewById(R.id.re_full_name);
        upphone = findViewById(R.id.re_phone);
        uplocation = findViewById(R.id.re_location);
        savebtn = findViewById(R.id.re_savebtn);
        pickimgbtn = findViewById(R.id.re_update_image_btn);
        delcharge = findViewById(R.id.del_charge);
        city = findViewById(R.id.re_city);

        pickimgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(RestSetting.this)
                        .crop()
                        .maxResultSize(300, 300)
                        .start();
            }
        });
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateProfile();
            }
        });
        uplocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestSetting.this.startActivityForResult(new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, Arrays.asList(new Place.Field[]{Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME})).setCountry("IN").build(RestSetting.this), 100);
            }
        });
    }

    private void UpdateProfile() {
        String name = upname.getText().toString();
        String phone = upphone.getText().toString();
        String location = uplocation.getText().toString();
        String delfee = delcharge.getText().toString();
        cityname = city.getText().toString().toUpperCase();

        if (name.isEmpty()){
            upname.setError("Name Required");
            return;
        }
        if (phone.isEmpty()){
            upphone.setError("Phone Number Required");
            return;
        }
        if (location.isEmpty()){
            uplocation.setError("Address Required");
            return;
        }
        if (cityname.isEmpty()){
            city.setError("City Name Required");
            return;
        }
        if (delfee.isEmpty()){
            delcharge.setError("Enter Delivery Charge");
            return;
        }
        if (tfval == true){
            imageUri = newImgUri;
        }if (imageUri == null){
            Toast.makeText(RestSetting.this, "Please select an Image", Toast.LENGTH_SHORT).show();
            return;
        }
        long charge = Long.parseLong(delcharge.getText().toString());
        progressDialog.show();
        userID = fAuth.getCurrentUser().getUid();
        final StorageReference reference = storageReference.child(System.currentTimeMillis()+".jpg");
        UploadTask uploadTask = reference.putFile(imageUri);
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
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    Map<String,Object> user = new HashMap<>();
                    user.put("name",name);
                    user.put("phone",phone);
                    user.put("address",location);
                    user.put("url",downloadUri.toString());
                    user.put("deliverycharge",charge);
                    user.put("city",cityname.toUpperCase());
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(RestSetting.this, "Data Successfully Updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), RestOwnerActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RestSetting.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RestSetting.this, "Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == -1) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            uplocation.setText(place.getName()+", "+place.getAddress());

            Double lat = place.getLatLng().latitude;
            Double lon = place.getLatLng().longitude;
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(lat, lon, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String ct = addresses.get(0).getLocality();
            if (ct != null){
                city.setText(ct.toUpperCase());
            }
            Log.d("TAG", "onActivityResult Restsetting: "+ct);

        } else if (resultCode == 2) {
            Toast.makeText(getApplicationContext(), Autocomplete.getStatusFromIntent(data).getStatusMessage(), Toast.LENGTH_SHORT).show();
        } else {
            newImgUri = data.getData();
            Picasso.get().load(newImgUri).into(upprofileImageView);
            tfval = true;
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    String rname = task.getResult().getString("name");
                    String rphone = task.getResult().getString("phone");
                    String rlocation = task.getResult().getString("address");
                    String ctlo = task.getResult().getString("city");
                    String Url = task.getResult().getString("url");
                    long ch = task.getResult().getLong("deliverycharge");
                    if (!String.valueOf(ch).isEmpty()){
                        chrg = String.valueOf(ch);
                    }

                    if (tfval == false){
                        if (Url != null){
                            Picasso.get().load(Url).into(upprofileImageView);
                        }
                    }
                    upname.setText(rname);
                    upphone.setText(rphone);
                    uplocation.setText(rlocation);
                    city.setText(ctlo);
                    delcharge.setText(chrg);
                } else {
                    Toast.makeText(RestSetting.this, "No Profile Exist", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(RestSetting.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}