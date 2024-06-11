package com.example.earningapp.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.earningapp.Models.UserModel;
import com.example.earningapp.R;
import com.example.earningapp.databinding.ActivityReferBinding;
import com.example.earningapp.databinding.ActivityScratchCardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ReferActivity extends AppCompatActivity {
ActivityReferBinding binding;
FirebaseAuth auth;
FirebaseDatabase database;
FirebaseUser user;
DatabaseReference reference;

String oppositeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityReferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        auth= FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("Users");

       loadReferCode();


       checkRedeemAvailable();
       binding.CopyCode.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               ClipboardManager clipboardManager =(ClipboardManager) getSystemService(ReferActivity.this.CLIPBOARD_SERVICE);
               ClipData clipData = ClipData.newPlainText("Data",binding.referCode.getText().toString());
               clipboardManager.setPrimaryClip(clipData);

               Toast.makeText(ReferActivity.this, "Referral code copied", Toast.LENGTH_SHORT).show();
           }
       });

       binding.btnRedeem.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               EditText editText = new EditText(ReferActivity.this);
               editText.setHint("code12f");

               LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                       LinearLayout.LayoutParams.MATCH_PARENT,
                       LinearLayout.LayoutParams.MATCH_PARENT);

                       editText.setLayoutParams(layoutParams);

               AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReferActivity.this);
               alertDialog.setTitle("Redeem code");
               alertDialog.setView(editText);
               alertDialog.setPositiveButton("Redeem", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       String inputCode = editText.getText().toString();
                       if(TextUtils.isEmpty(inputCode)){

                           Toast.makeText(ReferActivity.this, "Input valid code" , Toast.LENGTH_SHORT).show();
                        return;

                       }
                       if(inputCode.equals(binding.referCode.getText().toString())){
                           Toast.makeText(ReferActivity.this, "You can not input own code", Toast.LENGTH_SHORT).show();
                           return;
                       }

                       redeemQuery(inputCode,dialog);

                   }
               }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                       dialog.dismiss();
                   }
               });

               alertDialog.show();
           }
       });
       binding.btnRefer.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String referCode = binding.referCode.getText().toString();
               String shareBody =" Hey ,I am using best app, Join using my invite code and get 100 coins." +
                       "my invite code is "+referCode;
               Intent intent = new Intent(Intent.ACTION_SEND);
               intent.setType("text/plain");
               intent.putExtra(Intent.EXTRA_TEXT,shareBody);
               startActivity(intent);
           }
       });
    }

    private void redeemQuery(String inputCode, DialogInterface dialog) {

        Query query = reference.orderByChild("referCode").equalTo(inputCode);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    oppositeId=dataSnapshot.getKey();

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserModel model = snapshot.child(oppositeId).getValue(UserModel.class);
                            UserModel myModel = snapshot.child(user.getUid()).getValue(UserModel.class);

                            int coins = model.getCoins();
                            int updatedCoins = coins +100;
                            int mycoins = myModel.getCoins();

                            int myupdateCoins = mycoins + 100;

                            HashMap<String,Object> map = new HashMap<>();
                            map.put("coins",updatedCoins);

                            HashMap<String,Object> myMap = new HashMap<>();
                            myMap.put("coins",myupdateCoins);
                            myMap.put("redeemed",true);

                            reference.child(oppositeId).updateChildren(map);
                            reference.child(user.getUid()).updateChildren(myMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            dialog.dismiss();
                                            Toast.makeText(ReferActivity.this, "Congratulation", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Toast.makeText(ReferActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkRedeemAvailable() {

        reference.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists() && snapshot.hasChild("redeemed")){
                            boolean isAvailable = snapshot.child("redeemed").getValue(Boolean.class);
                            if (isAvailable){
                                binding.btnRedeem.setVisibility(View.GONE);
                            }
                            else{
                                binding.btnRedeem.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(ReferActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void loadReferCode() {

        reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String referCode = snapshot.child("referCode").getValue(String.class);
                binding.referCode.setText(referCode);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ReferActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}