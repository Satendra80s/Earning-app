package com.example.earningapp.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

import com.example.earningapp.Adapters.TrAdapter;
import com.example.earningapp.Models.TrModel;
import com.example.earningapp.R;
import com.example.earningapp.databinding.ActivitySpinnerBinding;
import com.example.earningapp.databinding.ActivityTrHistoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrHistoryActivity extends AppCompatActivity {

    ActivityTrHistoryBinding binding;
    ArrayList<TrModel>list;
    FirebaseDatabase database;
    FirebaseUser user;
    FirebaseAuth auth;
    TrAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityTrHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth =FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        //binding.redeemRcy.setLayoutManager(manager);

        database.getReference().child("Redeem").child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        list = new ArrayList<>();

                        if(snapshot.exists()){

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                TrModel model = dataSnapshot.getValue(TrModel.class);
                                list.add(model);
                            }
                            adapter = new TrAdapter(TrHistoryActivity.this,list);
                            //binding.redeemRcy.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(TrHistoryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}