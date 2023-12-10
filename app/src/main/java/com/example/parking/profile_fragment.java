package com.example.parking;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.parking.Helpers.AlertDialog;
import com.example.parking.Helpers.Helper;
import com.example.parking.Helpers.VerifyInputs;
import com.example.parking.Models.TablesName;
import com.example.parking.Models.UserDetails;
import com.example.parking.databinding.FragmentProfileFragmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class profile_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;

    private  FragmentProfileFragmentBinding binding;
    private FirebaseDatabase mDatabase;
    FirebaseUser firebaseUser;
    private PictuteListener pictureListener;
    private final int REQUEST_CODE = 1;

    private  Uri imageUri;


    public profile_fragment()
    {
        // Required empty public constructor
    }

    private interface PictuteListener {
        void onProfilePictureUpdated();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profile_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static profile_fragment newInstance(String param1, String param2) {
        profile_fragment fragment = new profile_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        profile_fragment.this.getActivity().setTitle(R.string.profile);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding=FragmentProfileFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //here data must be an instance of the class MarsDataProvider
        return view;
    }


    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseApp.initializeApp(profile_fragment.this.getContext());


        mAuth = FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        isDisplay(false);

        binding.progressBar.setVisibility(View.GONE);


        isVisibility(View.GONE);

        binding.imageView.setOnClickListener(v->openFileDialog());
        binding.btnProfileSave.setOnClickListener(v->updateProfile());
        binding.btnActiveUpdate.setOnClickListener(v->isDisplay(true));
        binding.btnBack.setOnClickListener(v->onBack());
        binding.btnUploadImage.setOnClickListener(v->{
            if(!firebaseUser.getUid().isEmpty() && imageUri!=null)
                uplaodProfilePhoto(imageUri,firebaseUser.getUid());
            else
                Toast.makeText(profile_fragment.this.getContext(), "الرجاء النقر على الصورة السابقة واختيار الصورة الشخصية   !! ", Toast.LENGTH_SHORT).show();
        });

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        {
            if(user.getDisplayName()!=null)
                binding.profileDisplayName.setText(user.getDisplayName());

            binding.progressBar.setVisibility(View.VISIBLE);
            getUserCarsNumber(firebaseUser.getUid());

//            if(user.getPhotoUrl()!=null)
//                binding.imageView.setImageURI(user.getPhotoUrl());
        }



    }


    private void isVisibility(final int value)
    {
        binding.labelCar1.setVisibility(value);
        binding.labelCar2.setVisibility(value);
        binding.labelCar3.setVisibility(value);

        binding.carNum1.setVisibility(value);
        binding.carNum2.setVisibility(value);
        binding.carNum3.setVisibility(value);
    }
    private void isDisplay(boolean value)
    {
        Helper.setReadOnly(binding.profileDisplayName,value);
        Helper.setReadOnly(binding.carNum1,value);
        Helper.setReadOnly(binding.carNum2,value);
        Helper.setReadOnly(binding.carNum3,value);
        if(value)
            isVisibility(View.VISIBLE);
        final int flag=(!value)?View.GONE:View.VISIBLE;
        binding.btnProfileSave.setVisibility(flag);
        binding.btnUploadImage.setVisibility(flag);
        binding.btnActiveUpdate.setVisibility((value)?View.GONE:View.VISIBLE);
    }
    private void onBack() {
        Intent intent = new Intent(this.getActivity(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void getUserCarsNumber(String key)
    {

        mDatabase.getReference(TablesName.Profile).child(key).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful())
                {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else
                {
                    try{
                            if(task.getResult().getValue()!=null)
                            {

                                UserDetails value = new Gson().fromJson(task.getResult().getValue().toString(), UserDetails.class);
                                Log.d("userDetils", new Gson().toJson(value));

                                if (value !=null)
                                {
                                    List<String> cars_number = value.getCarNumbers();
                                    EditText[] carsInput=new EditText[]{binding.carNum1, binding.carNum2, binding.carNum3};
                                    TextView[] carsLabels=new TextView[]{binding.labelCar1, binding.labelCar2, binding.labelCar3};

                                    if(cars_number!=null) {

                                        int len = (cars_number.size() > 3) ? 3 : cars_number.size();
                                        for (int i = 0; i < len; i++) {
                                            carsInput[i].setText(cars_number.get(i));
                                            carsInput[i].setVisibility(View.VISIBLE);
                                            carsLabels[i].setVisibility(View.VISIBLE);
                                        }
                                    }
                                    binding.progressBar.setVisibility(View.GONE);


                                }


                            }
                    }catch (Exception ex)
                    {
                        Log.e("error", ex.getMessage());
                    }


                }
            }
        });
    }

    private void openFileDialog()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE)
        {
              imageUri = data.getData();
            if(imageUri!=null)
            {

                Picasso.get().load(imageUri).into(binding.imageView);
                //binding.imageView.setImageURI(imageUri);

//               if(!firebaseUser.getUid().isEmpty())
//                     uplaodProfilePhoto(imageUri,firebaseUser.getUid());
            }

        }
    }


    private  void uplaodProfilePhoto(Uri imageUri,String userId)
    {
        if(userId!=null)
        {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference("profileImages");


            Uri file = Uri.fromFile(new File(imageUri.getPath()));
            StorageReference imageRef = storageRef.child(file.getLastPathSegment());
            Toast.makeText(profile_fragment.this.getContext(), imageRef.getPath(), Toast.LENGTH_SHORT).show();

            UploadTask uploadTask = imageRef.putFile(imageUri);
            Toast.makeText(profile_fragment.this.getContext(),imageUri.getPath(), Toast.LENGTH_SHORT).show();

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception)
                {
                    Toast.makeText(profile_fragment.this.getContext(),exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(profile_fragment.this.getContext(), taskSnapshot.getMetadata().getPath(), Toast.LENGTH_SHORT).show();

                }
            });

        }
    }



    private void updateProfile()
    {
        updateDisplayName();
    }

    boolean checkInputs(String carPlate,EditText editCarPlate)
    {
        int countError=0;
        if (carPlate.isEmpty())
        {
            countError++;
            Helper.setEditTextError(editCarPlate, VerifyInputs.inputEmpty);
        }
        else if (!VerifyInputs.VerifyCarPlate(carPlate)) {
            countError++;
            Helper.setEditTextError(editCarPlate, VerifyInputs.CarPlateError);
        }

        return countError==0;
    }

    private void updateCarsNumber(String userKey)
    {

        String car1=binding.carNum1.getText().toString().trim();
        String car2=binding.carNum2.getText().toString().trim();
        String car3=binding.carNum3.getText().toString().trim();

        mDatabase.getReference(TablesName.Profile).child(userKey).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            if (task.getResult().getValue() != null) {

                                UserDetails user = new Gson().fromJson(task.getResult().getValue().toString(), UserDetails.class);
                                if (user != null) {

                                    List<String> carsPlate=new ArrayList<>();
                                    if(checkInputs(car1,binding.carNum1))
                                    {
                                        carsPlate.add(car1);
                                        if(checkInputs(car2,binding.carNum2))
                                        {
                                            if (car2.equals(car1))
                                            {

                                                Helper.setEditTextError(binding.carNum2, "Car plate numbers must be different!! ");
                                                return;
                                            }
                                            else
                                                carsPlate.add(car2);
                                        }

                                        if(checkInputs(car3,binding.carNum3))
                                        {
                                            if (car3.equals(car1) || car3.equals(car2))
                                            {

                                                Helper.setEditTextError(binding.carNum3, "Car plate numbers must be different  !! ");
                                                return;
                                            }
                                            else
                                                carsPlate.add(car3);
                                        }
                                        if(carsPlate.size()>0)
                                        {
                                            user.setCarNumbers(carsPlate);
                                            DatabaseReference database = mDatabase.getReference();
                                            database.child(TablesName.Profile).child(firebaseUser.getUid()).child("cars").setValue(user.getCarNumbers())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@androidx.annotation.NonNull Task<Void> task) {

                                                            if(task.isSuccessful())
                                                            {
                                                                isDisplay(false);
                                                                getUserCarsNumber(firebaseUser.getUid());
                                                                new AlertDialog(profile_fragment.this.getActivity()).Show(" Notifiy", "Modified successfully",
                                                                        "yes", "");
                                                            }
                                                            else
                                                            {
                                                                // progressBar.setVisibility(View.GONE);
                                                                new AlertDialog(profile_fragment.this.getActivity()).Show(" Error", "The modification process is incorrect", "yes", "");
                                                            }

                                                        }
                                                    });
                                        }


                                    }

                                }
                            }
                        }
                    }
                });




    }

    private void clearInputs() {

        binding.profileDisplayName.setText("");
        binding.carNum1.setText("");
        binding.carNum2.setText("");
        binding.carNum3.setText("");
    }

    private void updateDisplayName()
    {

         String  d_name=binding.profileDisplayName.getText().toString().trim();
         if(d_name.isEmpty())
         {
             Helper.setEditTextError(binding.profileDisplayName,VerifyInputs.inputEmpty);
             return;
         }
         else
         {

             UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                     .setDisplayName(d_name)
                     .build();
             firebaseUser.updateProfile(profileChangeRequest)
                     .addOnCompleteListener(new OnCompleteListener<Void>()
                     {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if (task.isSuccessful())
                             {
                                 updateCarsNumber(firebaseUser.getUid());
                                 //Toast.makeText(profile_fragment.this.getContext(), " Success update display name", Toast.LENGTH_SHORT).show();
                             }
                             else
                             {

                                 Toast.makeText(profile_fragment.this.getContext(), " !! faild update  your Photo !!", Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
         }


            //
            //
    }







    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}