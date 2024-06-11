package com.example.earningapp.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.anupkumarpanwar.scratchview.ScratchView;
import com.example.earningapp.Models.UserModel;
import com.example.earningapp.R;
import com.example.earningapp.databinding.ActivityScratchCardBinding;
import com.example.earningapp.databinding.ActivitySingUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

public class ScratchCardActivity extends AppCompatActivity {

    ActivityScratchCardBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseUser user;
    boolean doubleTab = false;
    int duration = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityScratchCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user=auth.getCurrentUser();

        loadDate();

        binding.scratchView.setRevealListener(new ScratchView.IRevealListener() {
            @Override
            public void onRevealed(ScratchView scratchView) {
                int currentCoins =Integer.parseInt(binding.totalEarnCoin.getText().toString());
                int won = Integer.parseInt(binding.scratchCoin.getText().toString());
                int totalcoin = currentCoins + won;
                HashMap<String,Object> map = new HashMap<>();
                map.put("coins",totalcoin);

                database.getReference().child("Users").child(user.getUid()).setValue(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ScratchCardActivity.this, "coin added", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(ScratchCardActivity.this, "coins not added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            @Override
            public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {
                
                if(percent>=0.5){
                    Toast.makeText(ScratchCardActivity.this, String.valueOf(percent), Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

    private void loadDate() {

        database.getReference().child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserModel model = snapshot.getValue(UserModel.class);

                binding.totalEarnCoin.setText(String.valueOf(model.getCoins()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ScratchCardActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(doubleTab){
            super.onBackPressed();
            return;
        }
        else{
            doubleTab = true;
            Toast.makeText(this,"press again to exit",Toast.LENGTH_SHORT).show();
            binding.scratchView.mask();

            Random random= new Random();
            int  val = random.nextInt(50);
            binding.scratchCoin.setText(val+"");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    doubleTab=false;
                }
            },duration);
        }
    }
}