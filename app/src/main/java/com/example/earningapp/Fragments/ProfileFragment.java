package com.example.earningapp.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.earningapp.Models.UserModel;
import com.example.earningapp.R;
import com.example.earningapp.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    Uri imageUri;
    FirebaseStorage storage;
    FirebaseUser user;
    ProgressDialog dialog;



    public ProfileFragment() {
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
        binding=FragmentProfileBinding.inflate(inflater, container, false);

        database= FirebaseDatabase.getInstance();

        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Profile Uploding");
        dialog.setMessage("WE ARE UPLODING YOUR PROFILE");

        binding.fatchimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,5);
            }
        });
        database.getReference().child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserModel model= snapshot.getValue(UserModel.class);
                if (snapshot.exists()){
                    binding.username.setText(model.getName());
                    binding.totalCoin.setText(model.getCoins()+"");

                    Picasso.get()
                            .load(model.getProfile())
                            .placeholder(R.drawable.placeholder)
                            .into(binding.userProfile);
                }
                else{
                    Toast.makeText(getContext(), "data not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getContext(),error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==5){
            if(data!=null)

                imageUri=data.getData();
            binding.userProfile.setImageURI(imageUri);

            dialog.show();

            final StorageReference reference= storage.getReference().child("profile").child(user.getUid());

            reference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            imageUri=uri;
                                            dialog.dismiss();
                                            HashMap<String,Object> map=new HashMap<>();
                                            map.put("profile",imageUri.toString());

                                            database.getReference().child("Users").child(user.getUid())
                                                    .updateChildren(map)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if(task.isSuccessful()){
                                                                Toast.makeText(getContext(), "Profile uploaded", Toast.LENGTH_SHORT).show();
                                                            }
                                                            else{
                                                                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                        }
                    });
        }
    }
}