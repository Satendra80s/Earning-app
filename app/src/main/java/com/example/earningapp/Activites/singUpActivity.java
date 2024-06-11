package com.example.earningapp.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.earningapp.MainActivity;
import com.example.earningapp.R;
import com.example.earningapp.databinding.ActivitySingUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class singUpActivity extends AppCompatActivity {

    ActivitySingUpBinding binding;
   FirebaseAuth auth;
   FirebaseDatabase database;
   FirebaseUser user;
   ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySingUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        user=auth.getCurrentUser();



        dialog  = new ProgressDialog(singUpActivity.this);
        dialog.setTitle("Creating Account");
        dialog.setMessage("We are Creating your account");

        binding.btnSingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();;

                auth.createUserWithEmailAndPassword(binding.editEmail.getText().toString(),binding.editPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                dialog.dismiss();
                              if(task.isSuccessful()){

                                  String email = binding.editEmail.getText().toString();
                                  String refer = email.substring(0,email.lastIndexOf("@"));
                                  String referCode = refer.replace(".","");

                                  HashMap<String,Object> map = new HashMap<>();

                                  map.put("name",binding.editName.getText().toString());
                                  map.put("mobileNuber ",binding.editMobile.getText().toString());
                                  map.put("email",binding.editEmail.getText().toString());
                                  map.put("password",binding.editPassword.getText().toString());
                                  map.put("profile","https://firebasestorage.googleapis.com/v0/b/earning-app-15f05.appspot.com/o/er.png?alt=media&token=500351c4-3eb5-4274-82b6-e3546c15d727");
                                  map.put("referCode",referCode);
                                  map.put("coins",20);
                                  map.put("spins",4);


                                  Date date= Calendar.getInstance().getTime();
                                  SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy", Locale.ENGLISH);
                                  Calendar calendar = Calendar.getInstance();

                                  calendar.setTime(date);

                                  calendar.add(Calendar.DAY_OF_MONTH,-1);
                                  Date previousDate = calendar.getTime();
                                  String dateString = dateFormat.format(previousDate);
                                  database.getReference().child("Daliy Rewarde")
                                          .child(user.getUid()).child("Date").setValue(dateString);




                                  database.getReference().child("Users").child(user.getUid()).setValue(map);

                                  Intent intent = new Intent(singUpActivity.this, MainActivity.class);
                                  startActivity(intent);
                                  finish();

                                  Toast.makeText(singUpActivity.this, "Your Account is created", Toast.LENGTH_SHORT).show();
                              }
                              else{
                                  Toast.makeText(singUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                              }
                            }
                        });
            }
        });

    }
}