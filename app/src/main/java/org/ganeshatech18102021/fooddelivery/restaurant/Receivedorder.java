package org.ganeshatech18102021.fooddelivery.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.ganeshatech18102021.fooddelivery.R;
import org.ganeshatech18102021.fooddelivery.model.orderModel;
import org.ganeshatech18102021.fooddelivery.user.orderspage;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Receivedorder extends AppCompatActivity {
    RecyclerView cartitemRV;
    FirestoreRecyclerAdapter cartAdapter;
    TextView empty;
    ProgressDialog progressDialog;
    String currentTime, DateToday,timedate,monthyear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receivedorder);
        cartitemRV = findViewById(R.id.orderItemRV);
        cartitemRV.setLayoutManager(new LinearLayoutManager(this));
        empty = findViewById(R.id.recorder_empty);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        currentTime = mdformat.format(calendar.getTime());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DateToday = day + "-" + (month + 1) + "-" + year;
        monthyear = (month + 1) + "-" + year;
        timedate = currentTime+"-"+DateToday;

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait....");

        Query cquery = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("order").orderBy("priority",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<orderModel> option = new FirestoreRecyclerOptions.Builder<orderModel>()
                .setQuery(cquery, orderModel.class)
                .build();
        cartAdapter = new FirestoreRecyclerAdapter<orderModel,reopViewHolder>(option){
            @NonNull
            @NotNull
            @Override
            public reopViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View vie = LayoutInflater.from(parent.getContext()).inflate(R.layout.receivedorder_card,parent,false);
                return new reopViewHolder(vie);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull reopViewHolder holder, int position, @NonNull @NotNull orderModel model) {
                holder.items.setText(model.getItemdetail());
                holder.time.setText(model.getTime());
                holder.date.setText(model.getDate());
                holder.price.setText(model.getItemtotal());
                holder.addr.setText(model.getAddress());
                holder.phn.setText(model.getPhone());
                holder.paymode.setText(model.getPaymode());
                holder.status.setText(model.getStatus());
                if (model.getPayID() != null){
                    holder.paylay.setVisibility(View.VISIBLE);
                    holder.payid.setText(model.getPayID());
                }
                DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(model.getId());
                dref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        holder.usrname.setText(task.getResult().getString("name"));
                    }
                });
                if (model.getStatus().equals("ordered")){
                    holder.stalay.setVisibility(View.VISIBLE);
                }
                holder.deliverebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.show();
                        DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(model.getId())
                                .collection("order").document(model.getOrderID());
                        dref.update("status","delivered").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(model.getRestid())
                                        .collection("order").document(model.getOrderID());
                                dref.update("status","delivered");

                                DocumentReference repref = FirebaseFirestore.getInstance().collection("Report").document(monthyear)
                                        .collection(model.getRestid()).document();
                                Map<String,Object> addcart = new HashMap<>();
                                addcart.put("date",model.getDate());
                                addcart.put("price",model.getItemtotal());
                                addcart.put("time",model.getTime());
                                addcart.put("paymode",model.getPaymode());
                                repref.set(addcart).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(Receivedorder.this,Receivedorder.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(Receivedorder.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
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

    private class reopViewHolder extends RecyclerView.ViewHolder {
        TextView usrname,items,time,date,price,addr,phn,paymode,status,payid;
        LinearLayout paylay,stalay;
        Button deliverebtn;
        public reopViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            usrname = itemView.findViewById(R.id.reo_name);
            items = itemView.findViewById(R.id.reo_des);
            time = itemView.findViewById(R.id.reotime);
            date = itemView.findViewById(R.id.reodate);
            price = itemView.findViewById(R.id.reoorder_price);
            addr = itemView.findViewById(R.id.reo_address);
            phn = itemView.findViewById(R.id.reo_mobno);
            paymode = itemView.findViewById(R.id.reo_paymod);
            status = itemView.findViewById(R.id.reo_order_status);
            payid = itemView.findViewById(R.id.reopayid);
            paylay = itemView.findViewById(R.id.reopayidlay);
            stalay = itemView.findViewById(R.id.status_lay);
            deliverebtn = itemView.findViewById(R.id.deliveredbtn);
        }
    }
}