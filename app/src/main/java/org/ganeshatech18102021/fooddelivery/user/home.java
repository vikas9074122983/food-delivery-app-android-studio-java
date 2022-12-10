package org.ganeshatech18102021.fooddelivery.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.ganeshatech18102021.fooddelivery.AddressPickMap;
import org.ganeshatech18102021.fooddelivery.LoginActivity;
import org.ganeshatech18102021.fooddelivery.R;
import org.ganeshatech18102021.fooddelivery.model.ProductModel;
import org.ganeshatech18102021.fooddelivery.model.StoryAdapter;
import org.ganeshatech18102021.fooddelivery.model.StoryModel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class home extends Fragment {
    TextView cityname,empty,noRest;
    LinearLayout eattext;
    ImageView cartbtn,searchbtn;
    EditText searchET;
    RecyclerView searchRV,homeRV,storyRv;
    CardView onfreecard;
    FirestoreRecyclerAdapter searchAdapter,adapter;
    String ctlo;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String searchInput;
    ArrayList<StoryModel> list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        cityname = view.findViewById(R.id.city_home);
        empty = view.findViewById(R.id.home_search_empty);
        cartbtn = view.findViewById(R.id.home_cart);
        searchbtn = view.findViewById(R.id.img_search);
        searchET = view.findViewById(R.id.ed_search);
        searchRV = view.findViewById(R.id.home_searchRV);
        homeRV = view.findViewById(R.id.homeRV);
        noRest = view.findViewById(R.id.norest_home);
        storyRv = view.findViewById(R.id.storyRV);
        onfreecard = view.findViewById(R.id.onfreecard);
        eattext = view.findViewById(R.id.eattext);


        cityname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddressPickMap.class));
            }
        });

        list = new ArrayList<>();
        list.add(new StoryModel(R.drawable.sltwo));
        list.add(new StoryModel(R.drawable.slone));
        list.add(new StoryModel(R.drawable.slthree));
        list.add(new StoryModel(R.drawable.slfour));
        list.add(new StoryModel(R.drawable.slfive));
        list.add(new StoryModel(R.drawable.slsix));
        list.add(new StoryModel(R.drawable.slone));
        list.add(new StoryModel(R.drawable.sltwo));
        list.add(new StoryModel(R.drawable.slthree));
        list.add(new StoryModel(R.drawable.slfour));
        list.add(new StoryModel(R.drawable.slfive));
        list.add(new StoryModel(R.drawable.slsix));

        StoryAdapter adapter = new StoryAdapter(list,getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        storyRv.setLayoutManager(linearLayoutManager);
        storyRv.setNestedScrollingEnabled(false);
        storyRv.setAdapter(adapter);


        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchInput = searchET.getText().toString();
                if (searchInput.isEmpty()){
                    searchRV.setVisibility(View.GONE);
                    storyRv.setVisibility(View.VISIBLE);
                    homeRV.setVisibility(View.VISIBLE);
                    onfreecard.setVisibility(View.VISIBLE);
                    eattext.setVisibility(View.VISIBLE);
                } else {
                    searchRV.setVisibility(View.VISIBLE);
                    homeRV.setVisibility(View.GONE);
                    storyRv.setVisibility(View.GONE);
                    onfreecard.setVisibility(View.GONE);
                    eattext.setVisibility(View.GONE);
                    serchaction();
                }
            }
        });

        cartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    startActivity(new Intent(getContext(),cartpage.class));
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("Login to continue");
                    builder.setMessage("Do you want to login ? ");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            getActivity().finish();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getContext(), "Please login first to use more features..", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();
                }

            }
        });

        return view;
    }

    private void serchaction() {
        Query psquery = fStore.collection("users").whereEqualTo("access","restaurant")
                .whereEqualTo("city",ctlo);
        Query squery = psquery.orderBy("name").startAt(searchInput);
        FirestoreRecyclerOptions<ProductModel> poptions = new FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(squery,ProductModel.class)
                .build();
        searchAdapter = new FirestoreRecyclerAdapter<ProductModel,homesViewHolder>(poptions) {
            @NonNull
            @NotNull
            @Override
            public homesViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_restaurant_card,parent,false);
                return new homesViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull homesViewHolder holder, int position, @NonNull @NotNull ProductModel model) {
                holder.name.setText(model.getName());
                Picasso.get().load(model.getUrl()).into(holder.img);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(),productpage.class);
                        intent.putExtra("id",model.getId());
                        intent.putExtra("resname",model.getName());
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() == 0){
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);
                    searchRV.setVisibility(View.VISIBLE);
                }
            }
        };
        searchRV.setAdapter(searchAdapter);
        searchRV.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        String i = getActivity().getIntent().getStringExtra("address");
        Log.d("TAG", "onCreateView: "+i);
        if (i != null){
            ctlo = i;
            cityname.setText(ctlo);
            loadrv();
        } else if(FirebaseAuth.getInstance().getCurrentUser() != null){
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()){
                        ctlo = task.getResult().getString("city");
                        if (ctlo != null){
                            cityname.setText(ctlo);
                            loadrv();
                        }
                        if (ctlo == null){
                            startActivity(new Intent(getContext(),usersetting.class));
                        }
                    } else {
                        Toast.makeText(getContext(), "No Profile Exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ctlo = "BILASPUR";
            cityname.setText(ctlo);
            loadrv();
        }

    }

    private void loadrv() {
        Query query = fStore.collection("users").whereEqualTo("access","restaurant")
                .whereEqualTo("city",ctlo);
        FirestoreRecyclerOptions<ProductModel> option = new FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(query,ProductModel.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<ProductModel,homeViewHolder>(option) {
            @NonNull
            @NotNull
            @Override
            public homeViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View vv = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_card,parent,false);
                return new homeViewHolder(vv);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull homeViewHolder holder, int position, @NonNull @NotNull ProductModel model) {
                holder.name.setText(model.getName());
                Picasso.get().load(model.getUrl()).into(holder.img);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            Intent intent = new Intent(getContext(),productpage.class);
                            intent.putExtra("id",model.getId());
                            intent.putExtra("resname",model.getName());
                            startActivity(intent);
                    }
                });
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() == 0){
                    homeRV.setVisibility(View.GONE);
                    noRest.setVisibility(View.VISIBLE);
                }else {
                    homeRV.setVisibility(View.VISIBLE);
                    noRest.setVisibility(View.GONE);
                }
            }
        };
        homeRV.setAdapter(adapter);
        homeRV.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.startListening();
    }

    private class homeViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView name;
        public homeViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.rest_card_img);
            name = itemView.findViewById(R.id.rest_name_txt);
        }
    }
    private class homesViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView name;
        public homesViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.srest_card_img);
            name = itemView.findViewById(R.id.srest_name_txt);
        }
    }
}