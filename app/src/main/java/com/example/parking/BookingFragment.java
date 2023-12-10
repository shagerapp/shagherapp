package com.example.parking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.parking.ApiClient.Controllers.BookingController;
import com.example.parking.Enum.Options;
import com.example.parking.Enum.ParkingStatus;
import com.example.parking.Helpers.AlertDialog;
import com.example.parking.Helpers.CurrentTime;
import com.example.parking.Helpers.Helper;
import com.example.parking.Helpers.ICallback;
import com.example.parking.Helpers.LocalStorage;
import com.example.parking.Models.Booking;
import com.example.parking.Models.Spot;
import com.example.parking.Models.TablesName;
import com.example.parking.Models.UserDetails;
import com.example.parking.Permissions.AppPermissions;
import com.example.parking.Services.LocationTrackingService;
import com.example.parking.databinding.FragmentBookingBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.time.LocalTime;
import java.util.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 * implements IFirebaseHelper
 */
public class BookingFragment extends Fragment  implements ICallback,AdapterView.OnItemSelectedListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private AppPermissions appPermissions;
    private  final String  TIME_PATTREN="hh:mm a";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private  Spot mySpot;
    private FirebaseDatabase mDatabase;
    FirebaseUser firebaseUser;

    private FirebaseAuth mAuth;
    Handler handelr=new Handler();
    FragmentBookingBinding binding;
    private  String spotId,groupId,startTimeValue,endTimeValue;
    DatePicker datePicker;

    private BookingController bookingController;
    public   Boolean isDateChanged = false,isTimeChanged=false,isTimeChanged2=false;
    private boolean isLocationPermissionOk;

    public BookingFragment()
    {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookingFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static BookingFragment newInstance(String param1, String param2) {

        BookingFragment fragment = new BookingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mySpot = (Spot)getArguments().getSerializable("spot");
        }

        appPermissions = new AppPermissions();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= FragmentBookingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        bookingController=new BookingController();
        mAuth = FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        getUserCarsPlate();
        binding.BtnShowStartTimePicker.setOnClickListener(v->IsDisplayStartTimePicker(v));
        binding.BtnShowEndTimePicker.setOnClickListener(v->IsDisplayEndTimePicker(v));
        binding.viewStartTime.setText(CurrentTime.getCurrentTime(TIME_PATTREN));
        binding.simpleEndTimePicker.setHour(LocalTime.now().getHour());
        binding.btnBack.setOnClickListener(v->onBack());

        binding.simpleEndTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hour, int minute) {

                isTimeChanged2 = true;
                handelr.post(new Runnable() {
                    @Override
                    public void run() {
                        isTimeChanged2 = true;
                        String time = Helper.FormatDateAndTime(TIME_PATTREN, binding.simpleEndTimePicker);
                        binding.viewTime2.setText(time);
                        binding.viewStartTime.setText(CurrentTime.getCurrentTime(TIME_PATTREN));
                    }
                });
            }
        });
        binding.BookingBtn.setOnClickListener(v->
        {
            binding.backgroundDialog2.setVisibility(View.VISIBLE);
            bookingController.checkIfUserHasBooking(this);
        });

    }

    private void onBack() {
        Intent intent = new Intent(BookingFragment.this.getActivity(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private  void getUserCarsPlate()
    {
        DatabaseReference ref= mDatabase.getReference(TablesName.Profile).child(firebaseUser.getUid());
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<DataSnapshot> task) {

                if (!task.isSuccessful())
                    Log.e("firebase", "Error getting data", task.getException());
                else {

                    try {
                        if (task.getResult().getValue() != null) {
                            UserDetails value = new Gson().fromJson(task.getResult().getValue().toString(), UserDetails.class);
                            if (value != null) {
                                List<String> carPlates = value.getCarNumbers();
                                if (carPlates != null) {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(BookingFragment.this.getContext(), android.R.layout.simple_spinner_item, carPlates);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    binding.spinnerCarsPlate.setAdapter(adapter);
                                    binding.spinnerCarsPlate.setOnItemSelectedListener(BookingFragment.this);
                                }
                            }
                        } else
                            Log.d("response", "null");
                    }catch (Exception ex)
                    {
                        Log.e("error", ex.getMessage());
                    }
                }
            }
        });
    }
    private  void createBooking() {
        endTimeValue = Helper.FormatDateAndTime(TIME_PATTREN, binding.simpleEndTimePicker);
        String startTime=CurrentTime.getCurrentTime(TIME_PATTREN);
        if( endTimeValue.isEmpty() && binding.spinnerCarsPlate.getSelectedItem()!=null)
            Toast.makeText(BookingFragment.this.getActivity(), "Select the Start  and End time !!", Toast.LENGTH_SHORT).show();
        else {
//            if(!CurrentTime.CheckTime(startTime,endTimeValue))
//            {
//                new AlertDialog(BookingFragment.this.getActivity()).Show(startTime, "وقت انتهاء الحجز المحدد غير صالح !!"+ endTimeValue,"إخفاء","");
//                return;
//            }
            DatabaseReference database = mDatabase.getReference(TablesName.Spots).child(mySpot.getId());
            if(database!=null)
                database.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()) {
                            DataSnapshot dataSnapshot = task.getResult();
                            if(dataSnapshot!=null) {
                                Spot spot=dataSnapshot.getValue(Spot.class);
                                if(spot.getStatus()!=ParkingStatus.BookedUp.ordinal()) {
                                    DatabaseReference ref=database.child("status");
                                    ref.setValue((int)ParkingStatus.BookedUp.ordinal()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                                startBookingSpot(startTime,endTimeValue,binding.spinnerCarsPlate.getSelectedItem().toString());
                                            else
                                                new AlertDialog(BookingFragment.this.getActivity()).Show(" Error   !!", "Valid !!", "yes", "");
                                        }
                                    });
                                }
                                else
                                    new AlertDialog(BookingFragment.this.getActivity()).Show("Error !"," The position is currently unavailable. You cannot check. Please try searching for another position !!","yes","");
                            }
                        }
                    }
                });

        }
    }
    @Override
    public  void OnResponseCallback(Object response) {


        if(response.toString().equals(Options.HASBOOKED.getValue()))
            new AlertDialog(BookingFragment.this.getActivity()).Show( "info !!","You have a previous reservation. You cannot reserve more " +
                    "than one parking space at the same time ", "ok", "");
        else if(response.toString().equals(Options.NOTHASBOOKED.getValue()))
        {
            createBooking();
        }


    }
    private void startBookingSpot(String starTime,String endTime,String carplate)
    {
        if(firebaseUser==null)
            return;
        Booking book= new Booking();
        book.setUserId(firebaseUser.getUid());
        book.setSpotId(mySpot.getId());
        book.setGroupId(mySpot.getGroupId());
        book.setCarPlate(carplate);
        book.setStatus(true);
        book.setStartTime(starTime);
        book.setEndTime(endTime);
        book.setDate(CurrentTime.getCurrentDate("yyyy-MM-dd"));
        book.setNotifiy(ParkingStatus.Available.ordinal());
        DatabaseReference database = mDatabase.getReference(TablesName.Bookings).push();
        database.setValue(book).addOnCompleteListener(new OnCompleteListener<Void>() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    binding.backgroundDialog2.setVisibility(View.GONE);
                     mDatabase.getReference(TablesName.BookingTemp).child(mySpot.getId()).removeValue();
                    // start  location tracking  service
                    if(!database.getKey().isEmpty())
                        LocalStorage.storage(BookingFragment.this.getActivity(),"BookingId",database.getKey());
                        onSuccessCreateBooking();
                }
                else
                {new AlertDialog(BookingFragment.this.getActivity()).Show(" Error   !!", "Valid", "yes", "");}
            }
        });
    }

    private void onSuccessCreateBooking() {

        MainActivity activity=(MainActivity)BookingFragment.this.getActivity();

        if (appPermissions.isLocationOk(activity))
        {
            isLocationPermissionOk = true;
            Intent intentService=new Intent(BookingFragment.this.getActivity(), LocationTrackingService.class);
            BookingFragment.this.getActivity().startForegroundService(intentService) ;
        }
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                new android.app.AlertDialog.Builder(activity)
                        .setTitle("Location Permission")
                        .setMessage("To activate the reservation, you require the site’s permission to track your location only for the entire time of reservation")
                        .setIcon(R.drawable.baseline_share_location_24)
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                appPermissions.requestLocationPermission(activity);
                            }
                        }).create().show();
            }
            else
            {
                appPermissions.requestLocationPermission(activity);
            }
        }

        FragmentManager fragmentManager = BookingFragment.this.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new showBookingFragment());
        fragmentTransaction.commit();
        new AlertDialog(BookingFragment.this.getActivity()).Show("Notify !!", "The position has been booked successfully", "yes", "");

    }


    public void IsDisplayTimePicker(TimePicker timePicker ,View v)
    {
        if(timePicker.getVisibility()!=View.VISIBLE)
        {
            timePicker.setVisibility(View.VISIBLE);
        }
        else
            timePicker.setVisibility(View.GONE);
    }
    public void IsDisplayStartTimePicker(View v)
    {
//        IsDisplayTimePicker(binding.simpleStartTimePicker,v);
    }
    public void IsDisplayEndTimePicker(View v)
    {
        IsDisplayTimePicker(binding.simpleEndTimePicker,v);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}




}