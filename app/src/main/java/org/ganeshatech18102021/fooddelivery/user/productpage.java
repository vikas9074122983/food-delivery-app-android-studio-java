package org.ganeshatech18102021.fooddelivery.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.ganeshatech18102021.fooddelivery.R;
import org.ganeshatech18102021.fooddelivery.model.ProductModel;
import org.ganeshatech18102021.fooddelivery.restaurant.manageItem;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class productpage extends AppCompatActivity {
    String id,restname;
    RecyclerView recyclerView,serchppRV;
    FirestoreRecyclerAdapter adapter,searchadapter;
    ProgressDialog dialogg;
    TextView resname,resaddr,empty;
    Button gotocart,backbtn;
    ImageView searchbtn;
    EditText searchET;
    String searchInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productpage);

        id = getIntent().getStringExtra("id");
        restname = getIntent().getStringExtra("resname");
        Log.d("TAG", "onCreate: "+id+" "+restname);
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            DocumentReference doref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            doref.update("restid",id);
            CollectionReference cref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("cart");
            cref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()){
                            document.getReference().delete();
                        }
                    }
                }
            });
        }


        recyclerView = findViewById(R.id.ppRV);
        serchppRV = findViewById(R.id.serchppRV);
        empty = findViewById(R.id.ppsechemptt_txt);
        backbtn = findViewById(R.id.backbtn);
        resname = findViewById(R.id.pp_rest_name);
        gotocart = findViewById(R.id.goto_cart_btn);
        searchbtn = findViewById(R.id.ppimg_search);
        searchET = findViewById(R.id.pped_search);
        dialogg = new ProgressDialog(this);
        dialogg.setCancelable(false);
        dialogg.setMessage("please wait..");

        resname.setText(restname);

        Query query = FirebaseFirestore.getInstance().collection("users").document(id).collection("product");
        FirestoreRecyclerOptions<ProductModel> options = new FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(query,ProductModel.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<ProductModel, mnViewHolder>(options) {
            @NonNull
            @NotNull
            @Override
            public mnViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card,parent,false);
                return new mnViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull mnViewHolder holder, int position, @NonNull @NotNull ProductModel model) {
                holder.name.setText(model.getName());
                long prs = model.getPrice();
                holder.price.setText("Price "+prs+" Rs.");
                if (model.getUrl() != null){
                    Picasso.get().load(model.getUrl()).into(holder.img);
                }
                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    holder.addtocart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gotocart.setVisibility(View.VISIBLE);
                            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .collection("cart").document(model.getId());
                            dialogg.show();
                            Map<String,Object> addcart = new HashMap<>();
                            addcart.put("name",model.getName());
                            addcart.put("price",model.getPrice());
                            addcart.put("id",model.getId());
                            addcart.put("url",model.getUrl());
                            addcart.put("quantity",1);
                            documentReference.set(addcart).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialogg.dismiss();
                                    Toast.makeText(productpage.this, "Item added to Cart", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    dialogg.dismiss();
                                    Toast.makeText(productpage.this, "Error", Toast.LENGTH_SHORT).show();
                                    Log.d("TAG", "onFailure: "+e.getMessage());
                                }
                            });
                        }
                    });
                } else {
                    holder.addtocart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(productpage.this, "Log in to use this feature", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();

        gotocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(productpage.this,cartpage.class));
                finish();
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchInput = searchET.getText().toString();
                if (searchInput.isEmpty()){
                    serchppRV.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                } else {
                    serchppRV.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    serchaction();
                }
            }
        });
    }
    private void serchaction() {
        Query squery = FirebaseFirestore.getInstance().collection("users").document(id).collection("product").orderBy("name").startAt(searchInput);
        FirestoreRecyclerOptions<ProductModel> poptions = new FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(squery,ProductModel.class)
                .build();
        searchadapter = new FirestoreRecyclerAdapter<ProductModel, srViewHolder>(poptions) {
            @NonNull
            @NotNull
            @Override
            public srViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card,parent,false);
                return new srViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull srViewHolder holder, int position, @NonNull @NotNull ProductModel model) {
                holder.name.setText(model.getName());
                long prs = model.getPrice();
                holder.price.setText("Price "+prs+" Rs.");
                if (model.getUrl() != null){
                    Picasso.get().load(model.getUrl()).into(holder.img);
                }
                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    holder.addtocart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gotocart.setVisibility(View.VISIBLE);
                            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .collection("cart").document(model.getId());
                            dialogg.show();
                            Map<String,Object> addcart = new HashMap<>();
                            addcart.put("name",model.getName());
                            addcart.put("price",model.getPrice());
                            addcart.put("id",model.getId());
                            addcart.put("url",model.getUrl());
                            addcart.put("quantity",1);
                            documentReference.set(addcart).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialogg.dismiss();
                                    Toast.makeText(productpage.this, "Item added to Cart", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    dialogg.dismiss();
                                    Toast.makeText(productpage.this, "Error", Toast.LENGTH_SHORT).show();
                                    Log.d("TAG", "onFailure: "+e.getMessage());
                                }
                            });
                        }
                    });
                } else {
                    holder.addtocart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(productpage.this, "Log in to use this feature", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() == 0){
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);
                    serchppRV.setVisibility(View.VISIBLE);
                }
            }
        };
        serchppRV.setAdapter(searchadapter);
        serchppRV.setLayoutManager(new LinearLayoutManager(this));
        searchadapter.startListening();
    }

    private class mnViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name,price;
        Button addtocart;
        public mnViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.pc_img);
            name = itemView.findViewById(R.id.pc_name);
            price = itemView.findViewById(R.id.pc_price);
            addtocart = itemView.findViewById(R.id.pc_addcartbtn);
        }
    }
    private class srViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name,price;
        Button addtocart;
        public srViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.pc_img);
            name = itemView.findViewById(R.id.pc_name);
            price = itemView.findViewById(R.id.pc_price);
            addtocart = itemView.findViewById(R.id.pc_addcartbtn);
        }
    }
}