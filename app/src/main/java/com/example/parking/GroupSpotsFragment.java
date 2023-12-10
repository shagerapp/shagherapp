package com.example.parking;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.parking.ApiClient.Controllers.BookingController;
import com.example.parking.ApiClient.Controllers.LocationHandlerController;
import com.example.parking.Enum.ParkingStatus;
import com.example.parking.Helpers.CustomAdapter;
import com.example.parking.Helpers.ICallback;
import com.example.parking.Helpers.IViewOptions;
import com.example.parking.Models.Spot;
import com.example.parking.Models.StatisticsParkingGroup;
import com.example.parking.Models.TablesName;
import com.example.parking.databinding.FragmentGroupSpotsBinding;
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

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupSpotsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupSpotsFragment extends Fragment implements ICallback, IViewOptions<Spot>
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LocationHandlerController   locationHandlerController;
    private FirebaseDatabase mDatabase;
    public FirebaseUser firebaseUser;
    private FragmentGroupSpotsBinding binding;
    ArrayList<Spot> mySpotsList;
    private StatisticsParkingGroup parkingGroup;

    private Handler handler=new Handler();
    private ListView listView;
    private  boolean isViewNearestSpot=true;
    private LinearLayout dialog_box;
    private Button btn_close_dialog;
    ImageView spotStatus,btn_booking,btn_view_spots ,btn_view_map;
    private Spot selectedSpot;

    public GroupSpotsFragment() {
        // Required empty public constructor


    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupSpotsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupSpotsFragment newInstance(String param1, String param2) {

        GroupSpotsFragment fragment = new GroupSpotsFragment();
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
            parkingGroup = (StatisticsParkingGroup)getArguments().getSerializable("group");
   

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding= FragmentGroupSpotsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //here data must be an instance of the class MarsDataProvider
        return view;
    }

    @Override
    public void onStart() {

        super.onStart();
        mDatabase= FirebaseDatabase.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        binding.progressBar.setVisibility(View.VISIBLE);
        mDatabase.getReference().child(TablesName.Spots).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                Log.d("Change Parking Status !! ",dataSnapshot.getKey());

                if(dataSnapshot!=null)
                {

                    mySpotsList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        // Get the question object
                        Spot item = snapshot.getValue(Spot.class);
                        if (item != null)
                        {
                            item.setId(snapshot.getKey());
                            mySpotsList.add(item);

                        }
                    }

                    if (mySpotsList.size() > 0)
                        AppendItemToCostumListView(mySpotsList);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("", "loadPost:onCancelled", databaseError.toException());
            }
        });

        spotStatus=binding.getRoot().findViewById(R.id.spotStatus);
        dialog_box=binding.getRoot().findViewById(R.id.group_dialog_box);
        btn_close_dialog=binding.getRoot().findViewById(R.id.btn_close_dialog);
        btn_close_dialog.setOnClickListener(v->hiddenDialog());
        binding.btnDisplaySpotsMap.setOnClickListener(v->viewSpotsInGroup());
        btn_booking=binding.getRoot().findViewById(R.id.btn_booking);
        btn_view_spots=binding.getRoot().findViewById(R.id.btn_view_spots);
        btn_view_map=binding.getRoot().findViewById(R.id.btn_view_map);
        binding.btnDisplaySpotsMap.setOnClickListener(v->viewAllSpotsInGroup());
//        btn_booking.setOnClickListener(v->onAcceptDefaultBooking());
        btn_view_spots.setOnClickListener(v->viewSpotsInGroup());
        btn_view_map.setOnClickListener(v->viewSpotLocationInMap());

        binding.progressBar.setVisibility(View.VISIBLE);
//        new BookingController().checkIfUserHasBooking(this);


    }

    private void viewAllSpotsInGroup() {

        if(parkingGroup!=null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("group", parkingGroup);
            // Create an instance of the target fragment
            ParkingMapFragment spotsFragment = new ParkingMapFragment();
            spotsFragment.setArguments(bundle);
            // Navigate to the target fragment
            FragmentManager fragmentManager = GroupSpotsFragment.this.getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frame_layout, spotsFragment);
            transaction.commit();
        }
    }


    private void viewSpotsInGroup() {


        if(selectedSpot==null)
            return;

            Bundle bundle = new Bundle();
            bundle.putSerializable("spot", selectedSpot);
            // Create an instance of the target fragment
            ParkingMapFragment spotsFragment = new ParkingMapFragment();
            spotsFragment.setArguments(bundle);
            // Navigate to the target fragment
            FragmentManager fragmentManager=GroupSpotsFragment.this.getActivity().getSupportFragmentManager();
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            transaction.replace(R.id.frame_layout, spotsFragment);
            transaction.commit();

    }


    private   void   getAllSpotsInGroup(String groupId)
    {
        DatabaseReference ref = mDatabase.getReference().child(TablesName.Spots);
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
                        }
                    }
                    if (mySpotsList.size() > 0)
                    {
                        AppendItemToCostumListView(mySpotsList);
                  
                    }
                }
                else
                {
                    // Handle the error
                    Exception exception = task.getException();
                    if (exception != null)
                    {
                        Log.d("error", exception.getMessage());
                    }
                }
            }
        });
    }

    private  void hiddenDialog()
    {
        binding.backgroundDialog.setVisibility(View.GONE);
    }


    @Override
    public View getAdapterView(LayoutInflater inflater, Spot item) {

        View convertView=inflater.inflate(R.layout.list_costum_item,null);
        //===============================================================================
        LinearLayout parentBox =(LinearLayout)convertView.findViewById(R.id.listCustomItem);
        ImageView status =(ImageView)convertView.findViewById(R.id.flag_status);
        TextView textlabel1 =(TextView)convertView.findViewById(R.id.label1);
        TextView textlabel2 =(TextView)convertView.findViewById(R.id.status);
        TextView btn_cancel_booked =(TextView)convertView.findViewById(R.id.btn_cancel_booked);



        //===============================================================================
        int color = Color.RED;
        String spotStatus=getString(R.string.available);
        if(item.getStatus()==ParkingStatus.BookedUp.ordinal())
        {   color = Color.RED; spotStatus=getString(R.string.booked);
            btn_cancel_booked.setVisibility(View.VISIBLE);
        }
        if(item.getStatus()==ParkingStatus.TemporarilyReserved.ordinal())
        {   color = Color.YELLOW;spotStatus= getString(R.string.temporarily_booked);
            btn_cancel_booked.setVisibility(View.VISIBLE);
        }
        else  if(item.getStatus()==ParkingStatus.Available.ordinal())
        {  color = Color.GREEN;spotStatus=getString(R.string.available);
            btn_cancel_booked.setVisibility(View.INVISIBLE);
        }

        ColorStateList colorStateList = ColorStateList.valueOf(color);
        status.setBackgroundTintList(colorStateList);
        textlabel1.setText(item.getName());
        textlabel2.setText(spotStatus);

        btn_cancel_booked.setOnClickListener(v->onCancelBooked(item));
        parentBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onSelectedCostumItem(item);
            }});
        return convertView;
    }

    private void onCancelBooked(Spot item) {

        new AlertDialog.Builder(this.getContext())
                .setTitle("Cancel Booked")
                .setMessage("Do you really want to cancel this Reservation?")
                .setIcon(R.drawable.outline_cancel_24)
                .setPositiveButton("Yes",(d,i)->{
                    new BookingController().AdminDeleteBooking(this.getActivity(),item,this);})
                .setNegativeButton("No",(d, i)->{})
                .setCancelable(true)
                .show();
    }

    @Override
    public void OnResponseCallback(Object response,Object data){

        if(response.equals("CancelBooked"))
        {
            new AlertDialog.Builder(this.getContext())
                .setTitle("Cancel Booked")
                    .setMessage(data.toString())
                    .setIcon(R.drawable.outline_cancel_24)
                    .setCancelable(true)
                    .setPositiveButton("Yes",(d, i)->{})
                    .show();
        }
    };
    @Override
    public void onSelectedCostumItem(Spot item) {


        selectedSpot=item;
        if(item.getStatus()!=ParkingStatus.Available.ordinal())
        {   spotStatus.setBackgroundResource(R.drawable.signal);
            btn_booking.setVisibility(View.INVISIBLE);
        }
        else
        {
            btn_booking.setVisibility(View.VISIBLE);
            spotStatus.setBackgroundResource(R.drawable.available);
        }

        binding.backgroundDialog.setVisibility(View.VISIBLE);
        @SuppressLint("ResourceType")
        Animation animation = AnimationUtils.loadAnimation(GroupSpotsFragment.this.getActivity(), R.animator.animation_dialog);
        dialog_box.startAnimation(animation);
        dialog_box.setVisibility(View.VISIBLE);



    }
    @Override
    public void AppendItemToCostumListView(ArrayList<Spot> items) {


        if(GroupSpotsFragment.this.getActivity()!=null)
        {
            CustomAdapter<Spot> customItems = new CustomAdapter<Spot>((GroupSpotsFragment.this.getActivity()), this, items);
            listView= binding.getRoot().findViewById(R.id.list_View_group_spots);
            listView.setAdapter(customItems);
            binding.progressBar.setVisibility(View.GONE);
        }

    }







    private void viewSpotLocationInMap()
    {
    
                Intent intent = new Intent(GroupSpotsFragment.this.getActivity(),DirectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.putExtra("placeId");
                intent.putExtra("lat",selectedSpot.getCoordinates().getLatitude());
                intent.putExtra("lng",selectedSpot.getCoordinates().getLongitude());
                startActivity(intent);
        

    }






}