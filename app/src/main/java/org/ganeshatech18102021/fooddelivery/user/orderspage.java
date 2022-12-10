package org.ganeshatech18102021.fooddelivery.user;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.ganeshatech18102021.fooddelivery.R;
import org.ganeshatech18102021.fooddelivery.model.cartModel;
import org.ganeshatech18102021.fooddelivery.model.orderModel;
import org.jetbrains.annotations.NotNull;

public class orderspage extends AppCompatActivity {
    RecyclerView cartitemRV;
    FirestoreRecyclerAdapter cartAdapter;
    TextView empty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderspage);
        cartitemRV = findViewById(R.id.op_recy);
        cartitemRV.setLayoutManager(new LinearLayoutManager(this));
        empty = findViewById(R.id.op_empty);

        Query cquery = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("order").orderBy("priority",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<orderModel> option = new FirestoreRecyclerOptions.Builder<orderModel>()
                .setQuery(cquery, orderModel.class)
                .build();
        cartAdapter = new FirestoreRecyclerAdapter<orderModel, opViewHolder>(option){
            @NonNull
            @NotNull
            @Override
            public opViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View vie = LayoutInflater.from(parent.getContext()).inflate(R.layout.myorder_card,parent,false);
                return new opViewHolder(vie);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull opViewHolder holder, int position, @NonNull @NotNull orderModel model) {
                holder.items.setText(model.getItemdetail());
                holder.time.setText(model.getTime());
                holder.date.setText(model.getDate());
                holder.price.setText(model.getItemtotal());
                holder.paymode.setText(model.getPaymode());
                holder.status.setText(model.getStatus());
                if (model.getPayID() != null){
                    holder.paylay.setVisibility(View.VISIBLE);
                    holder.payid.setText(model.getPayID());
                }
                DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(model.getRestid());
                dref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        holder.resname.setText(task.getResult().getString("name"));
                        Picasso.get().load(task.getResult().getString("url")).into(holder.cpimg);
                    }
                });
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() == 0){
                    empty.setVisibility(View.VISIBLE);
                    cartitemRV.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.GONE);
                    cartitemRV.setVisibility(View.VISIBLE);
                }
            }
        };
        cartitemRV.setAdapter(cartAdapter);
        cartAdapter.startListening();
        cartAdapter.notifyDataSetChanged();
    }

    private class opViewHolder extends RecyclerView.ViewHolder {
        TextView resname,items,time,date,price,paymode,status,payid;
        ImageView cpimg;
        LinearLayout paylay;
        public opViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cpimg = itemView.findViewById(R.id.morder_img);
            resname = itemView.findViewById(R.id.morder_name_txt);
            items = itemView.findViewById(R.id.morder_desc_txt);
            time = itemView.findViewById(R.id.otime);
            date = itemView.findViewById(R.id.odate);
            price = itemView.findViewById(R.id.morder_price);
            paymode = itemView.findViewById(R.id.paymod);
            status = itemView.findViewById(R.id.morder_status);
            payid = itemView.findViewById(R.id.payid);
            paylay = itemView.findViewById(R.id.payidlay);
        }
    }
}