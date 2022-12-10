package org.ganeshatech18102021.fooddelivery.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.ganeshatech18102021.fooddelivery.R;
import org.ganeshatech18102021.fooddelivery.model.RestModel;
import org.jetbrains.annotations.NotNull;

public class ManageRest extends AppCompatActivity {
    RecyclerView categoryRv,searchList;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirestoreRecyclerAdapter categoryAdapter,searchAdapter;
    private Button searchBtn;
    private EditText inputText;
    private String searchInput;
    LinearLayout emptyserch;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rest);
        categoryRv = findViewById(R.id.allproductRV);
        searchBtn = findViewById(R.id.prosearch_btn);
        inputText = findViewById(R.id.search_pro);
        emptyserch = findViewById(R.id.caasser_empty);
        searchList = findViewById(R.id.search_res);
        searchList.setLayoutManager(new LinearLayoutManager(ManageRest.this));
        categoryRv.setLayoutManager(new LinearLayoutManager(ManageRest.this));

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait ....");

        Query fpquery = fStore.collection("users").whereEqualTo("access","restaurant");
        FirestoreRecyclerOptions<RestModel> fpoption = new FirestoreRecyclerOptions.Builder<RestModel>()
                .setQuery(fpquery,RestModel.class)
                .build();
        categoryAdapter = new FirestoreRecyclerAdapter<RestModel, prooViewHolder>(fpoption) {
            @NonNull
            @NotNull
            @Override
            public prooViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View vv = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_card,parent,false);
                return new prooViewHolder(vv);
            }
            @Override
            protected void onBindViewHolder(@NonNull @NotNull prooViewHolder holder, int position, @NonNull @NotNull RestModel model) {
                if (model.getUrl() != null){
                    Picasso.get().load(model.getUrl()).into(holder.cpimg);
                }
                holder.cpname.setText(model.getName());
                holder.cpdes.setText(model.getAddress());
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageRest.this);
                        builder.setTitle("Delete Restaurant");
                        builder.setMessage("Do you want to delete this Restaurant ? ");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressDialog.show();
                                DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(model.getId());
                                dref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        builder.show();
                    }
                });
            }
        };
        categoryRv.setAdapter(categoryAdapter);
        categoryAdapter.startListening();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchInput = inputText.getText().toString();
                if (searchInput.isEmpty()){
                    searchList.setVisibility(View.GONE);
                } else {
                    searchList.setVisibility(View.VISIBLE);
                    search();
                }
            }
        });
    }

    private void search() {
        CollectionReference reference = fStore.collection("users");
        Query squery = reference.whereEqualTo("access","restaurant").orderBy("name").startAt(searchInput);
        FirestoreRecyclerOptions<RestModel> poptions = new FirestoreRecyclerOptions.Builder<RestModel>()
                .setQuery(squery,RestModel.class)
                .build();
        searchAdapter = new FirestoreRecyclerAdapter<RestModel, proViewHolder>(poptions) {
            @NonNull
            @NotNull
            @Override
            public proViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_card,parent,false);
                return new proViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull proViewHolder holder, int position, @NonNull @NotNull RestModel model) {
                if (model.getUrl() != null){
                    Picasso.get().load(model.getUrl()).into(holder.cpimg);
                }
                holder.cpname.setText(model.getName());
                holder.cpdes.setText(model.getAddress());
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageRest.this);
                        builder.setTitle("Delete Restaurant");
                        builder.setMessage("Do you want to delete this Restaurant ? ");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressDialog.show();
                                DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(model.getId());
                                dref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        builder.show();
                    }
                });
            }
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() == 0){
                    searchList.setVisibility(View.GONE);
                    emptyserch.setVisibility(View.VISIBLE);
                } else {
                    searchList.setVisibility(View.VISIBLE);
                    emptyserch.setVisibility(View.GONE);
                }
            }
        };
        searchList.setAdapter(searchAdapter);
        searchAdapter.startListening();
    }

    private class proViewHolder extends RecyclerView.ViewHolder {
        TextView cpname,cpdes,delete;
        ImageView cpimg;
        public proViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cpname = itemView.findViewById(R.id.fav_name_txt);
            cpdes = itemView.findViewById(R.id.fav_desc_txt);
            cpimg = itemView.findViewById(R.id.fav_img);
            delete = itemView.findViewById(R.id.rest_delete);
        }
    }
    private class prooViewHolder extends RecyclerView.ViewHolder {
        TextView cpname,cpdes,delete;
        ImageView cpimg;
        public prooViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cpname = itemView.findViewById(R.id.fav_name_txt);
            cpdes = itemView.findViewById(R.id.fav_desc_txt);
            cpimg = itemView.findViewById(R.id.fav_img);
            delete = itemView.findViewById(R.id.rest_delete);
        }
    }
}