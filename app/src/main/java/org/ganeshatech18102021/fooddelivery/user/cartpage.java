package org.ganeshatech18102021.fooddelivery.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.ganeshatech18102021.fooddelivery.R;
import org.ganeshatech18102021.fooddelivery.model.cartModel;
import org.jetbrains.annotations.NotNull;

public class cartpage extends AppCompatActivity {
    RecyclerView cartitemRV;
    FirestoreRecyclerAdapter cartAdapter;
    TextView empty;
    Button placeoreder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartpage);
        cartitemRV = findViewById(R.id.cart_recy);
        cartitemRV.setLayoutManager(new LinearLayoutManager(this));
        empty = findViewById(R.id.cart_empty);
        placeoreder = findViewById(R.id.placeorder_btn);

        Query cquery = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("cart");
        FirestoreRecyclerOptions<cartModel> option = new FirestoreRecyclerOptions.Builder<cartModel>()
                .setQuery(cquery, cartModel.class)
                .build();
        cartAdapter = new FirestoreRecyclerAdapter<cartModel, cartViewHolder>(option){
            @NonNull
            @NotNull
            @Override
            public cartViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View vie = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_card,parent,false);
                return new cartViewHolder(vie);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull cartViewHolder holder, int position, @NonNull @NotNull cartModel model) {
                Picasso.get().load(model.getUrl()).into(holder.cpimg);
                holder.cpname.setText(model.getName());
                holder.cpquantity.setText(String.valueOf(model.getQuantity()));
                Long tempPrice = model.getPrice();
                Long tempItem = model.getQuantity();
                Long price = tempPrice*tempItem;
                holder.cpprice.setText(String.valueOf(price)+" Rs.");


                placeoreder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), ConfirmOrderPage.class);
                        startActivity(intent);
                    }
                });

                holder.plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("cart").document(model.getId());
                        dref.update("quantity", FieldValue.increment(1));
                    }
                });
                holder.minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("cart").document(model.getId());
                        dref.update("quantity", FieldValue.increment(-1));
                    }
                });
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("cart").document(model.getId());
                        dref.delete().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() == 0){
                    empty.setVisibility(View.VISIBLE);
                    cartitemRV.setVisibility(View.GONE);
                    placeoreder.setClickable(false);
                } else {
                    empty.setVisibility(View.GONE);
                    cartitemRV.setVisibility(View.VISIBLE);
                    placeoreder.setClickable(true);
                }
            }

        };
        cartitemRV.setAdapter(cartAdapter);
        cartAdapter.startListening();
        cartAdapter.notifyDataSetChanged();

    }


    private class cartViewHolder extends RecyclerView.ViewHolder {
        TextView cpname,cpprice,cpquantity;
        ImageView cpimg;
        ImageButton delete,plus,minus;
        public cartViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cpname = itemView.findViewById(R.id.cart_name_txt);
            cpprice = itemView.findViewById(R.id.cart_pro_price);
            cpquantity = itemView.findViewById(R.id.quantity);
            cpimg = itemView.findViewById(R.id.cart_img);
            delete = itemView.findViewById(R.id.deletefromcart_btn);
            plus = itemView.findViewById(R.id.qinc);
            minus = itemView.findViewById(R.id.qdec);
        }
    }
}