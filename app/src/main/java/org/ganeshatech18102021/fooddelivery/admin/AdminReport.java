package org.ganeshatech18102021.fooddelivery.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.ganeshatech18102021.fooddelivery.R;
import org.ganeshatech18102021.fooddelivery.model.RestModel;
import org.ganeshatech18102021.fooddelivery.model.reportModel;
import org.jetbrains.annotations.NotNull;
import org.library.worksheet.cellstyles.WorkSheet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminReport extends AppCompatActivity {

    RecyclerView recyclerView;
    FirestoreRecyclerAdapter categoryAdapter;
    Spinner monthSpin,yearSpin;
    private String month,year;
    private String monthyear;
    WorkSheet workSheet;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    ArrayList<reportModel> recordModelArrayList;
    ArrayList<reportModel> newrecArray;
    ArrayList<reportModel> onerecArray;
    ProgressDialog progressDialog;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String resID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_report);

        recyclerView = findViewById(R.id.restlistRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminReport.this));

        monthSpin = findViewById(R.id.month_spin);
        yearSpin = findViewById(R.id.year_spin);
        List<String> slot = new ArrayList<>();
        slot.add(0, "Select Month");
        slot.add("1");
        slot.add("2");
        slot.add("3");
        slot.add("4");
        slot.add("5");
        slot.add("6");
        slot.add("7");
        slot.add("8");
        slot.add("9");
        slot.add("10");
        slot.add("11");
        slot.add("12");
        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, slot);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpin.setAdapter(dataAdapter);
        monthSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("Select Month")) {
                    month = "";
                } else {
                    month = adapterView.getItemAtPosition(i).toString();
                    Log.d("TAG", "onItemSelected: "+month);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(AdminReport.this, "Please Select month", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2018; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        years.add(0, "Select year");
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpin.setAdapter(adapter);
        yearSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("Select year")) {
                    year = "";
                } else {
                    year = adapterView.getItemAtPosition(i).toString();
                    Log.d("TAG", "onItemSelected: "+year);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(AdminReport.this, "Please Select year", Toast.LENGTH_SHORT).show();
            }
        });

        recordModelArrayList = new ArrayList<reportModel>();
        newrecArray = new ArrayList<reportModel>();
        onerecArray = new ArrayList<reportModel>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait ....");

        Query fpquery = fStore.collection("users").whereEqualTo("access","restaurant");
        FirestoreRecyclerOptions<RestModel> fpoption = new FirestoreRecyclerOptions.Builder<RestModel>()
                .setQuery(fpquery,RestModel.class)
                .build();
        categoryAdapter = new FirestoreRecyclerAdapter<RestModel, resViewHolder>(fpoption) {
            @NonNull
            @NotNull
            @Override
            public resViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View vv = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_card,parent,false);
                return new resViewHolder(vv);
            }
            @Override
            protected void onBindViewHolder(@NonNull @NotNull resViewHolder holder, int position, @NonNull @NotNull RestModel model) {
                if (model.getUrl() != null){
                    Picasso.get().load(model.getUrl()).into(holder.cpimg);
                }
                holder.cpname.setText(model.getName());
                holder.cpdes.setText(model.getAddress());
                holder.export.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        monthyear = month+"-"+year;
                        Log.d("TAG", "spin selected: "+monthyear+" "+monthyear.length());
                        if (monthyear.length() < 6){
                            Toast.makeText(AdminReport.this, "Please select Month and Year", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.show();
                            collectionReference = db.collection("Report").document(monthyear).collection(model.getId());
                            resID = model.getId();
                            Log.d("TAG", "getdata: "+resID);
                            collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (!queryDocumentSnapshots.isEmpty()){
                                        for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                                            reportModel repm = documentSnapshot.toObject(reportModel.class);
                                            recordModelArrayList.add(repm);
                                            if (onerecArray.isEmpty()){
                                                onerecArray.add(repm);
                                            }
                                        }
                                        recordModelArrayList.addAll(onerecArray);

                                        if (!recordModelArrayList.isEmpty()){
                                            createWorkSheet();
                                        }else {
                                            Toast.makeText(AdminReport.this, "No data to Export", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    } else {
                                        Toast.makeText(AdminReport.this, "No Data Found", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(AdminReport.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(categoryAdapter);
        categoryAdapter.startListening();
    }


    private void createWorkSheet() {
        Log.d("TAG", "spin selected: "+monthyear+" "+resID);
        try {
            workSheet = new WorkSheet.Builder(getApplicationContext(),resID)
                    .setSheet(recordModelArrayList)
                    .writeSheet();
            Toast.makeText(this, "Record exported Successfully to Folder Worksheets", Toast.LENGTH_SHORT).show();
            recordModelArrayList.clear();
            progressDialog.dismiss();
            resID = " ";
        } catch (Exception e) {
            Log.d("TAG", "onFailure: "+e.getMessage());
            e.printStackTrace();
            Toast.makeText(AdminReport.this, "Error Occured", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private class resViewHolder extends RecyclerView.ViewHolder {
        TextView cpname,cpdes,export;
        ImageView cpimg;
        public resViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cpname = itemView.findViewById(R.id.rfav_name_txt);
            cpdes = itemView.findViewById(R.id.rfav_desc_txt);
            cpimg = itemView.findViewById(R.id.rfav_img);
            export = itemView.findViewById(R.id.generate_btn);
        }
    }
}