package com.example.parking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.parking.ApiClient.Controllers.BookingController;
import com.example.parking.ApiClient.Controllers.LocationHandlerController;
import com.example.parking.ApiClient.Controllers.TrackinUserController;
import com.example.parking.ApiClient.Enums.UserLocationState;
import com.example.parking.ApiClient.interfaces.ITrackingUserResponse;
import com.example.parking.ApiClient.models.BaseResponse;
import com.example.parking.Enum.Options;
import com.example.parking.Enum.ParkingStatus;
import com.example.parking.Helpers.AlertDialog;
import com.example.parking.Helpers.CurrentTime;
import com.example.parking.Helpers.ICallback;
import com.example.parking.Models.BookingTemp;
import com.example.parking.Models.ParkingGroups;
import com.example.parking.Models.Spot;
import com.example.parking.Models.TablesName;
import com.example.parking.Models.UserLocationStateModel;
import com.example.parking.databinding.FragmentParkingMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParkingMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParkingMapFragment extends Fragment  implements ITrackingUserResponse, OnMapReadyCallback,  ICallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Spot nearestSpot;
    private Handler handler=new Handler();
    private FragmentParkingMapBinding binding;
    private FirebaseDatabase mDatabase;
    public FirebaseUser firebaseUser;
    private ParkingGroups parkingGroup;
    private Spot spot;
    private ArrayList<Spot> mySpotsList;
    private     int FIRST_PARKING_INDEX;
    private final int  TOTAL_TIME = 10;
    private Button btnAccept,btnCancel,btnCloseDialogBox;
    private ProgressBar lineProgressBar;
    private  boolean TIMER_START=true;
    private  boolean isViewNearestSpot=false;
    private LinearLayout dialog_box;
    private  boolean flag=true;
    private Button btn_close_dialog;
    ImageView spotStatus,btn_booking,btn_view_spots ,btn_view_map;

    private CountDownTimer countDownTimer;

    private LinearLayout nearest_spot_dialog_box;
    private TextView nerestParking,textViewTimer,firstText,secondText;
    private int timeRemaining = 10;
    private LocationHandlerController locationHandlerController;
    String key="";
    private BookingController bookingController;
    private LinearLayout notify_dialog_box;
    private TrackinUserController trackingUserControll;
    private boolean allowBooking=false;
    private boolean isViewLocationSpot=false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ParkingMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ParkingMapFragment newInstance(String param1, String param2) {
        ParkingMapFragment fragment = new ParkingMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            if(getArguments().getSerializable("group")!=null)
                parkingGroup = (ParkingGroups)getArguments().getSerializable("group");
            if(getArguments().getSerializable("spot")!=null) {
                isViewLocationSpot=true;
                spot = (Spot) getArguments().getSerializable("spot");
            }
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentParkingMapBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //here data must be an instance of the class MarsDataProvider
        return view;
    }

    private  void hiddenDialog()
    {
        binding.backgroundDialog.setVisibility(View.GONE);
    }
    @Override
    public void onStart() {

        super.onStart();
        mDatabase= FirebaseDatabase.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        FIRST_PARKING_INDEX=R.id.p1;
        bookingController=new BookingController();
        trackingUserControll=new TrackinUserController(this.getActivity());

        InitializationComponent();

        if(parkingGroup!=null)
            key=parkingGroup.getKey();
        else if(spot!=null)
        {
            key = spot.getId();
            binding.btnContnuBooking.setVisibility(View.VISIBLE);
        }

        if(!isViewLocationSpot)
             new BookingController().checkIfUserHasBooking(this);
        else  if(parkingGroup!=null)
            getAllSpotsInGroup(parkingGroup.getKey());
    }

    private  void ListenerOfSpotParking()
    {
            mDatabase.getReference().child(TablesName.Spots).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                Log.d("Change Parking Status !! ",dataSnapshot.getKey());

                if(dataSnapshot!=null) {

                    mySpotsList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        // Get the question object
                        Spot item = snapshot.getValue(Spot.class);
                        if (item != null)
                        {
                            item.setId(snapshot.getKey());
                            mySpotsList.add(item);
                            setSpotColor(item);
                        }
                    }

                    if(mySpotsList.size()>0)
                        binding.progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void InitializationComponent()
    {
        binding.btnCancel.setOnClickListener(v->onCancel());
        binding.btnContnuBooking.setOnClickListener(v->onAcceptDefaultBooking());
        dialog_box=binding.getRoot().findViewById(R.id.group_dialog_box);
        btn_close_dialog=binding.getRoot().findViewById(R.id.btn_close_dialog);
        btn_close_dialog.setOnClickListener(v->hiddenDialog());
        btn_booking=binding.getRoot().findViewById(R.id.btn_booking);
        btn_view_spots=binding.getRoot().findViewById(R.id.btn_view_spots);
        btn_view_map=binding.getRoot().findViewById(R.id.btn_view_map);
        spotStatus=binding.getRoot().findViewById(R.id.spotStatus);
        btn_booking.setOnClickListener(v->onAcceptDefaultBooking());
        btn_view_map.setOnClickListener(v->viewSpotLocationInMap());
        btn_view_spots.setOnClickListener(v->viewSpotsInGroup());
    }

    @SuppressLint("ResourceAsColor")
    private  void setSpotColor(Spot item)
    {

        if(item!=null)
        {
            int spot_id=item.getIndex();

            if(spot_id>=0)
            {
                int lineAndIndex[]=getSpotLineAndIndex(spot_id);
                if(lineAndIndex[0]>-1) {
                    LinearLayout linearLayout =binding.getRoot().findViewById(lineAndIndex[0]);
                    if(linearLayout!=null)
                    {
                        if(lineAndIndex[1]<linearLayout.getChildCount())
                        {
                            ImageView btn = (ImageView)linearLayout.getChildAt(lineAndIndex[1]);
                            if(btn!=null)
                            {
                                Log.d("index",item.getIndex()+"");
//                                Toast.makeText(ParkingMapFragment.this.getActivity(), btn.getId()+"", Toast.LENGTH_SHORT).show();
                                btn.setOnClickListener(v->onSelectSpotParking(item));
                            }


                            if(spot!=null && spot.getIndex()==item.getIndex())
                            {
                                if (item.getStatus() == ParkingStatus.BookedUp.ordinal())
                                    btn.setImageResource(R.drawable.loc1);
                                else
                                    btn.setImageResource(R.drawable.loc2);

                            }
                            else
                            {
                                int color = Color.RED;
                                if (item.getStatus() == ParkingStatus.Available.ordinal())
                                    color = Color.GREEN;
                                else if (item.getStatus() == ParkingStatus.TemporarilyReserved.ordinal())
                                    color = Color.YELLOW;

                                ColorStateList colorStateList = ColorStateList.valueOf(color);
                                btn.setBackgroundTintList(colorStateList);
                            }


                        }
                     }
                    }

            }
        }
    }

    private  void InitializationDialogBox(){

        nearest_spot_dialog_box=binding.getRoot().findViewById(R.id.nearest_spot_dialog_box);
        nerestParking=(TextView) binding.getRoot().findViewById(R.id.neerestParking);
        textViewTimer=(TextView) binding.getRoot().findViewById(R.id.spotTextViewTimer);
        firstText=(TextView) binding.getRoot().findViewById(R.id.firstText);
        secondText=(TextView) binding.getRoot().findViewById(R.id.secondText);
        btnAccept=(Button) binding.getRoot().findViewById(R.id.btnSpotAccept);
        btnCancel=(Button) binding.getRoot().findViewById(R.id.btnSpotCancel);
        lineProgressBar=(ProgressBar) binding.getRoot().findViewById(R.id.progressBar);
        btnAccept.setOnClickListener(v->onAcceptDefaultBooking());
        btnCancel.setOnClickListener(v->onCancelDefaultBooking());

    }

    private void  onSelectSpotParking(Spot item){

       spot=nearestSpot=item;
        flag=false;
        StopTimer();
        if(item.getStatus()!=ParkingStatus.Available.ordinal() || !allowBooking)
        {   spotStatus.setBackgroundResource(R.drawable.signal);
            btn_booking.setVisibility(View.GONE);
        }
        else if(allowBooking)
        {
            btn_booking.setVisibility(View.VISIBLE);
            spotStatus.setBackgroundResource(R.drawable.available);
        }

        binding.backgroundDialog.setVisibility(View.VISIBLE);
        @SuppressLint("ResourceType")
        Animation animation = AnimationUtils.loadAnimation(ParkingMapFragment.this.getActivity(), R.animator.animation_dialog);
        dialog_box.startAnimation(animation);
        dialog_box.setVisibility(View.VISIBLE);

    }

    private  int[] getSpotLineAndIndex(int spot_index){

    //        0->8
    //        9->17
    //        18->26
    //        27->32

        int res[]= {-1,-1};
        if(spot_index<9)
        {
            res[0] = R.id.line1;
            res[1] = spot_index;
        }
        else if(spot_index<18)
        {
            res[0] = R.id.line2;
            res[1] = spot_index-9;
        }
       else if(spot_index<27)
       {
            res[0] = R.id.line3;
            res[1] = spot_index - 18;
        }
        else if(spot_index<33)
        {
            res[0] = R.id.line4;
            res[1] = spot_index - 27;
        }

       return res;

    }
    private   void   getAllSpotsInGroup(String groupId) {

        if(spot!=null)
            return;

        binding.progressBar.setVisibility(View.VISIBLE);
        // Get a reference to the questions node
        DatabaseReference ref = mDatabase.getReference().child(TablesName.Spots);
        // Create a query that matches the tag value
        Query query = ref.orderByChild("groupId").equalTo(groupId);
        query.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DataSnapshot dataSnapshot = task.getResult();
                    mySpotsList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        // Get the question object
                        Spot item = snapshot.getValue(Spot.class);
                        if (item != null)
                        {
                            item.setId(snapshot.getKey());
                            mySpotsList.add(item);
                            setSpotColor(item);
                        }
                    }

                    if(isViewNearestSpot && !isViewLocationSpot)
                        getNearestParking();
                }
                else
                {
                    // Handle the error
                    Exception exception = task.getException();
                    if (exception != null) {
                        Log.d("error", exception.getMessage());
                    }
                }
                binding.progressBar.setVisibility(View.GONE);
            }

        });


    }
    private void getNearestParking() {

        if(mySpotsList==null || !isViewNearestSpot)
                 return;
//        int index= Helper.getNearestParkingIndex(AppConfig.DOOR_COORDINATES,mySpotsList;
//        if(index>-1){
//            Spot nSpot = mySpotsList.get(index);
//            if(nSpot!=null && nSpot.getStatus()==ParkingStatus.Available.ordinal())
//            {
//                nearestSpot=spot=spt;
//                InitializationDialogBox();
//                showNearestSpotDialogBox(nearestSpot);
//
//            }
//        }
//

        for(int i=0;i<mySpotsList.size();i++)
        {
           Spot spt = mySpotsList.get(i);
           if(spt.getStatus()==ParkingStatus.Available.ordinal())
           {
                nearestSpot=spot=spt;
                InitializationDialogBox();
                showNearestSpotDialogBox(nearestSpot);
               StartTimer();
              return;
           }
        }

        new AlertDialog(this.getActivity()).Show("!notify","There is no parking available","ok"," ");



    }
    private void onCancel() {
        replaceFregment(new home_fragment());
        if(isViewNearestSpot)
        {
           StopTimer();
        }
    }
    private  void showNearestSpotDialogBox(Spot spot) {
        if(flag)
        {
            nerestParking.setText(spot.getName());
            textViewTimer.setText(String.valueOf(TOTAL_TIME));

            if(binding.backgroundDialogSpots.getVisibility()!=View.VISIBLE) {
                binding.backgroundDialogSpots.setVisibility(View.VISIBLE);
                nearest_spot_dialog_box.setVisibility(View.VISIBLE);

                @SuppressLint("ResourceType")
                Animation animation = AnimationUtils.loadAnimation(ParkingMapFragment.this.getActivity(), R.animator.animation_dialog);
                nearest_spot_dialog_box.startAnimation(animation);
            }
        }


    }
    private  void hiddenNearestSpotDialogBox() {
        binding.backgroundDialogSpots.setVisibility(View.GONE);
        nearest_spot_dialog_box.setVisibility(View.GONE);
    }
    private  void StartTimer() {
        if(nearestSpot==null)
            return;

        DatabaseReference ref = mDatabase.getReference(TablesName.Spots).child(nearestSpot.getId());
        if(nearestSpot.getStatus()==ParkingStatus.Available.ordinal())
        {
            if(ref!=null)
                ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<DataSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            DataSnapshot dataSnapshot = task.getResult();
                            if(dataSnapshot!=null)
                            {
                                Spot spot=dataSnapshot.getValue(Spot.class);
                                spot.setStatus((int)ParkingStatus.TemporarilyReserved.ordinal());
                                String currentTime= CurrentTime.getCurrentTime("hh:mm a");

                                BookingTemp bookTemp=new BookingTemp();
                                bookTemp.setSpotKey(dataSnapshot.getKey());
                                bookTemp.setStartTimeBookingTemp(currentTime);
                                bookTemp.setUserId(firebaseUser.getUid());
                                mDatabase.getReference(TablesName.BookingTemp).child(bookTemp.getSpotKey()).setValue(bookTemp);
                                ref.child("status").setValue((int)ParkingStatus.TemporarilyReserved.ordinal());

                                showNearestSpotDialogBox(nearestSpot);

                                countDownTimer = new CountDownTimer(10000, 1000) {
                                    public void onTick(long millisUntilFinished)
                                    {
                                        if(timeRemaining<=0)
                                              StopTimer();

                                        textViewTimer.setText(String.valueOf(--timeRemaining));
                                        textViewTimer.refreshDrawableState();
                                        int progress = (int) (millisUntilFinished / 1000);
                                        lineProgressBar.setProgress(lineProgressBar.getMax() - progress);
                                    }

                                    public void onFinish()
                                    {
                                        hiddenNearestSpotDialogBox();
                                        spot.setStatus((int)ParkingStatus.Available.ordinal());
                                        ref.child("status").setValue((int)ParkingStatus.Available.ordinal());
                                        mDatabase.getReference(TablesName.BookingTemp).child(bookTemp.getSpotKey()).removeValue();
                                    }
                                };

                                if (countDownTimer != null)
                                    countDownTimer.start();

                            }
                        }
                    }
                });
        }

    }
    private  void StopTimer() {
        if(countDownTimer!=null&&timeRemaining>0)
            countDownTimer.cancel();
    }
    private void onAcceptDefaultBooking() {

        if(isViewNearestSpot)
            StopTimer();

        if(spot==null)
            return;

        Intent intent = new Intent(ParkingMapFragment.this.getActivity(),DirectionActivity.class);
        intent.putExtra("spot", new Gson().toJson(nearestSpot).toString());
        intent.putExtra("spotKey", nearestSpot.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


    }
    private void replaceFregment(Fragment fragment) {
        FragmentManager  fragmentManager=this.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
    private void onCancelDefaultBooking() {

        StopTimer();
        new BookingController().DeleteTemporaryBooking(nearestSpot.getId());
//        mDatabase.getReference(TablesName.Spots).child(nearestSpot.getId()).child("status").setValue(ParkingStatus.Available.ordinal());
        hiddenNearestSpotDialogBox();
//      getAllSpotsInGroup(parkingGroup.getKey());

    }
    private void viewSpotLocationInMap() {
        if(nearestSpot!=null)
        {
            Intent intent = new Intent(ParkingMapFragment.this.getActivity(),DirectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("lat",nearestSpot.getCoordinates().getLatitude());
            intent.putExtra("lng",nearestSpot.getCoordinates().getLongitude());
            startActivity(intent);
        }

    }
    private void viewSpotsInGroup() {


        if(spot==null)
            return;

        Bundle bundle = new Bundle();
        bundle.putSerializable("spot", spot);
        // Create an instance of the target fragment
        ParkingMapFragment spotsFragment = new ParkingMapFragment();
        spotsFragment.setArguments(bundle);
        // Navigate to the target fragment
        FragmentManager fragmentManager=ParkingMapFragment.this.getActivity().getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, spotsFragment);
        transaction.commit();

    }
    @Override
    public void OnResponseCallback(Object response) {

        if(response!=null) {
            if(response.toString().equals(Options.HASBOOKED.getValue())) {

                // has booked
                isViewNearestSpot=false;
                allowBooking=false;
                btn_booking.setVisibility(View.GONE);
                if(ParkingMapFragment.this.getActivity()!=null)
                 new AlertDialog(ParkingMapFragment.this.getActivity()).Show("! Notify","You are already booked. You cannot reserve more than one site at the same time !!","ok","");

            }
            else if(response.toString().equals(Options.NOTHASBOOKED.getValue())) {
                   getCurrentLocation();
            }
        }

        if(parkingGroup!=null)
            getAllSpotsInGroup(parkingGroup.getKey());
        ListenerOfSpotParking();
    }
    @Override
    public void onSuccess(BaseResponse response) {
        if(response!=null && response.getResultCode()==200 )
            if(response.getResult().equals(UserLocationState.INSIDE.getValue())) {
                allowBooking=true;
                isViewNearestSpot=true;
                btn_booking.setVisibility(View.VISIBLE);
            }
           else {
                isViewNearestSpot=false;
                allowBooking=false;
                btn_booking.setVisibility(View.GONE);
                if(ParkingMapFragment.this.getActivity()!=null)
                    new AlertDialog(ParkingMapFragment.this.getActivity()).Show("! Notify","You cannot reserve any site, it must be within the university boundaries","ok","");
            }
        if(parkingGroup!=null)
            getAllSpotsInGroup(parkingGroup.getKey());
    }

    @Override
    public void onFailure(String call) {
        isViewNearestSpot=false;
        allowBooking=false;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }

    private void getCurrentLocation() {

        if(ParkingMapFragment.this.getActivity()==null)
            return;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ParkingMapFragment.this.getActivity());

        if (ActivityCompat.checkSelfPermission(ParkingMapFragment.this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ParkingMapFragment.this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
            // Request the last known location
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null)
                        {

                            UserLocationStateModel userLocationState=new UserLocationStateModel();
                            userLocationState.setLatitude(location.getLatitude());
                            userLocationState.setLongitude(location.getLongitude());
                            userLocationState.setLatitude(21.582138751365104);
                            userLocationState.setLongitude(39.18204463095501);
                            trackingUserControll.checkUserLocationState(userLocationState,ParkingMapFragment.this );
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                    });
        }

    }
