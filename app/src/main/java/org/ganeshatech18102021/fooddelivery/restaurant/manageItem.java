package org.ganeshatech18102021.fooddelivery.restaurant;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.ganeshatech18102021.fooddelivery.R;
import org.ganeshatech18102021.fooddelivery.model.ProductModel;
import org.jetbrains.annotations.NotNull;

public class manageItem extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView empty;
    FirestoreRecyclerAdapter adapter;
    ProgressDialog dialogg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_item);

        recyclerView = findViewById(R.id.manageItemRV);
        empty = findViewById(R.id.mn_empty);
        dialogg = new ProgressDialog(this);
        dialogg.setCancelable(false);
        dialogg.setMessage("Deleting please wait..");

        Query query = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("product");
        FirestoreRecyclerOptions<ProductModel> options = new FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(query,ProductModel.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<ProductModel, mnViewHolder>(options) {
            @NonNull
            @NotNull
            @Override
            public mnViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_item_card,parent,false);
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
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(manageItem.this);
                        builder.setTitle("Delete Item");
                        builder.setMessage("Do you want to Delete this item ? ");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                 dialogg.show();
                                DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("product").document(model.getId());
                                dref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialogg.dismiss();
                                        Toast.makeText(manageItem.this, "Item Deleted..", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        dialogg.dismiss();
                                        Toast.makeText(manageItem.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    recyclerView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();
    }

    private class mnViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name,price;
        Button delete;
        public mnViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.mn_card_img);
            name = itemView.findViewById(R.id.mn_card_name);
            price = itemView.findViewById(R.id.mn_card_price);
            delete = itemView.findViewById(R.id.mn_card_delete);
        }
    }
}