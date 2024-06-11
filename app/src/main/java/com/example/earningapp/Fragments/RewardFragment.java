package com.example.earningapp.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.earningapp.Activites.TrHistoryActivity;
import com.example.earningapp.Models.UserModel;
import com.example.earningapp.R;
import com.example.earningapp.databinding.FragmentRewardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class RewardFragment extends Fragment {

    FragmentRewardBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    int currentCoin;

    Dialog dialog;
    ImageView withdLogo;
    TextView withMethods;
    EditText edtPaymentDetails,edtCoins;
    AppCompatButton btnredeem,btncancel;


    public RewardFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRewardBinding.inflate(inflater, container, false);

        auth =FirebaseAuth.getInstance();
        user =auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        dialog= new Dialog(getContext());
        dialog.setContentView(R.layout.payment_dialog);


        btnredeem =dialog.findViewById(R.id.btnRedeem);
        btncancel =dialog.findViewById(R.id.btnCancel);
        edtPaymentDetails = dialog.findViewById(R.id.editPaymentDetails);
        edtCoins =dialog.findViewById(R.id.editCoins);
        withdLogo =dialog.findViewById(R.id.withdrawallMethodLogo);
        withMethods =dialog.findViewById(R.id.withdwralMethod);


        if (dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
        }

        binding.PaytemRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                withdLogo.setImageResource(R.drawable.paytm);
                withMethods.setText("Paytm");

                dialog.show();
            }
        });
        binding.PayPalRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                withdLogo.setImageResource(R.drawable.paypal);
                withMethods.setText("PayPal");

                dialog.show();
            }
        });

        binding.btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TrHistoryActivity.class);
                startActivity(intent);
            }
        });

        binding.GooglePlayRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                withdLogo.setImageResource(R.drawable.amazon);
                withMethods.setText("Amazon Gift" );

                dialog.show();
            }
        });
        binding.PaytemRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                withdLogo.setImageResource(R.drawable.googleplay);
                withMethods.setText("Google Gift");

                dialog.show();
            }
        });


        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnredeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String paymentMethods = withMethods.getText().toString();
                redeem(paymentMethods);
            }
        });


        reference.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserModel model =snapshot.getValue(UserModel.class);


                binding.progressBar.setProgress(model.getCoins());
                binding.currentCoin.setText(String.valueOf(model.getSpins()));
                binding.currentcoin2.setText(String.valueOf(model.getCoins()));

                currentCoin = Integer.parseInt(String.valueOf(model.getCoins()));
                int requireCoin = 5000-currentCoin;

                binding.paytemProg.setProgress(model.getCoins());
                binding.amozonProg.setProgress(model.getCoins());
                binding.payPalProg.setProgress(model.getCoins());
                binding.googleProg.setProgress(model.getCoins());

                if(currentCoin>=5000){
                    binding.needTextPaytm.setText("Completed");
                    binding.needTextAmazon.setText("Completed");
                    binding.needTextGoogle.setText("Completed");
                    binding.needTextPaypal.setText("Completed");

                    binding.needCoinPaytm.setVisibility(View.GONE);
                    binding.needCoinAmazon.setVisibility(View.GONE);
                    binding.needCoinGoogle.setVisibility(View.GONE);
                    binding.needCoinPaypal.setVisibility(View.GONE);

                    binding.PaytemRedeem.setEnabled(true);
                    binding.AmazonRedeem.setEnabled(true);
                    binding.GooglePlayRedeem.setEnabled(true);
                    binding.PayPalRedeem.setEnabled(true);


                }
                else {
                    binding.PaytemRedeem.setEnabled(false);
                    binding.AmazonRedeem.setEnabled(false);
                    binding.GooglePlayRedeem.setEnabled(false);
                    binding.PayPalRedeem.setEnabled(false);

                    binding.needCoinPaytm.setText(requireCoin+"");
                    binding.needCoinAmazon.setText(requireCoin+"");
                    binding.needCoinGoogle.setText(requireCoin+"");
                    binding.needCoinPaypal.setText(requireCoin+"");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return binding.getRoot();
    }

    private void redeem(String paymentMethods) {

        String withCoin = edtCoins.getText().toString();
        String paymentDetails = edtPaymentDetails.getText().toString();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyy");
        String date = currentDate.format(calendar.getTime());

        HashMap<String,Object> map = new HashMap<>();
        map.put("paymentDetails",paymentDetails);
        map.put("coin",withCoin);
        map.put("paymentMethode",paymentMethods);
        map.put("status","false");
        map.put("date",date);

        reference.child("Redeem").child(user.getUid())
                .push()
                .setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                       updateCoin();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateCoin() {
       int withdrawalCoin = Integer.parseInt(edtCoins.getText().toString());
        int updateCoin = currentCoin - withdrawalCoin;

        HashMap<String,Object> map=new HashMap<>();
        map.put("coins",updateCoin);

        reference.child("Users").child(user.getUid())
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(getContext(), "Congratulation", Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }
}