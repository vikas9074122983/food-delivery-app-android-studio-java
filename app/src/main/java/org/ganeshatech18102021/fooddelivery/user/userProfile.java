package org.ganeshatech18102021.fooddelivery.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.ganeshatech18102021.fooddelivery.LoginActivity;
import org.ganeshatech18102021.fooddelivery.R;
import org.ganeshatech18102021.fooddelivery.Register;
import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class userProfile extends Fragment {
    private CircleImageView profileimg;
    private ImageButton pickimg,setting;
    private TextView name;
    private CardView personalinfo,orders,aboutus,help,logout,loginbtn,registerbtn;
    private ConstraintLayout userinfolay;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        profileimg = view.findViewById(R.id.userimg);
        setting = view.findViewById(R.id.settingbtn);
        name = view.findViewById(R.id.username);
        personalinfo = view.findViewById(R.id.personalinfobtn);
        orders = view.findViewById(R.id.ordersbtn);
        aboutus = view.findViewById(R.id.aboutbtn);
        help = view.findViewById(R.id.helpbtn);
        logout = view.findViewById(R.id.logoutbutton);
        loginbtn = view.findViewById(R.id.unreg_loginbtn);
        registerbtn = view.findViewById(R.id.ur_registerbtn);
        userinfolay = view.findViewById(R.id.ursinf_constraintLayout);
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            loginbtn.setVisibility(View.GONE);
            registerbtn.setVisibility(View.GONE);
        } else {
            logout.setVisibility(View.GONE);
            userinfolay.setVisibility(View.GONE);
            personalinfo.setVisibility(View.GONE);
            orders.setVisibility(View.GONE);
            help.setVisibility(View.GONE);
        }
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),LoginActivity.class));
                getActivity().finish();
            }
        });
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), Register.class));
                getActivity().finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), usersetting.class));
            }
        });
        personalinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), userInfo.class));
            }
        });
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), orderspage.class));
            }
        });
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), aboutCompany.class));
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), HelpPage.class));
            }
        });

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()){
                        String rname = task.getResult().getString("name");
                        String Url = task.getResult().getString("url");

                        if (Url != null){
                            Picasso.get().load(Url).into(profileimg);
                        }
                        name.setText(rname);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}