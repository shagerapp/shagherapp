package com.example.parking;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.parking.ApiClient.Controllers.BookingController;
import com.example.parking.Helpers.AlertDialog;
import com.example.parking.Helpers.CurrentTime;
import com.example.parking.Helpers.IAlertDialog;
import com.example.parking.Models.Booking;
import com.example.parking.Models.ParkingGroups;
import com.example.parking.Models.Spot;
import com.example.parking.Models.TablesName;
import com.example.parking.databinding.FragmentShowBookingBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link showBookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class showBookingFragment extends Fragment implements IAlertDialog {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentShowBookingBinding binding;
    private FirebaseDatabase mDatabase;
    FirebaseUser firebaseUser;
//    DialogBox _DialogBox;
    private FirebaseAuth mAuth;
   private Booking currentBooking;
    Spot spotBook;
    String group_name;


    public showBookingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment showBookingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static showBookingFragment newInstance(String param1, String param2) {
        showBookingFragment fragment = new showBookingFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentShowBookingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //here data must be an instance of the class MarsDataProvider
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();


        this.getActivity().setTitle("");
        mAuth = FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        binding.cancelBookingBth.setOnClickListener(v->onCancelBooking());
        binding.extensionBookingBtn.setOnClickListener(v->onExtensionBookingTime());
        binding.btnBack.setOnClickListener(v->OnBack());
        binding.creatBooking.setOnClickListener(v->onCreateBooking());
//        binding.btnDiasplaySpotInMap.setOnClickListener(v->onShowLocationInMap());
        laodCurrentBooking();

    }

    private void OnBack()
    {
        replaceFregment(new home_fragment());
    }

    private  void onCancelBooking() {

        new AlertDialog(showBookingFragment.this.getActivity(),this,"CancelBooking").Show("warning !!","Do you want to cancel your reservation already?","Yes","No");
    }

    private  void onExtensionBookingTime(){


        if(currentBooking==null || spotBook==null)
            return;

        Bundle bundle = new Bundle();
        bundle.putSerializable("booking", currentBooking);
        bundle.putString("spot",spotBook.getName());
        bundle.putString("group",group_name);
        // Create an instance of the target fragment
        ExtenationBookingFragment fregment = new ExtenationBookingFragment();
        fregment.setArguments(bundle);
        // Navigate to the target fragment
        FragmentManager fragmentManager=showBookingFragment.this.getActivity().getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fregment);
        transaction.commit();
    }
    private  void laodCurrentBooking()
    {
        binding.progressBar.setVisibility(View.VISIBLE);
        DatabaseReference ref = mDatabase.getReference().child(TablesName.Bookings);
        Query query = ref.orderByChild("userId").equalTo(firebaseUser.getUid());
        query.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public  void  onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful())
                {
                    DataSnapshot dataSnapshot = task.getResult();
                    boolean flag=false;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        // Get the question object
                        Booking item = snapshot.getValue(Booking.class);
                        if (item != null && item.getStatus())
                        {
                            flag=true;
                            item.setKey(snapshot.getKey());
                            currentBooking = item;
                            binding.date.setText(item.getDate());
                            binding.carPlate.setText(item.getCarPlate());
//                            String endTime=item.getEndTime();
                            int duration=CurrentTime.getDurationTime(item.getStartTime(),item.getEndTime());
                            binding.duration.setText(duration+" hours");
                            binding.hours.setText(item.getStartTime() +" - "+item.getEndTime());

                             Task<DataSnapshot> taskSpot= mDatabase.getReference().child(TablesName.Spots).child(item.getSpotId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@androidx.annotation.NonNull Task<DataSnapshot> task)
                                {
                                    if (task.isSuccessful() && task.getResult() != null)
                                    {

                                            spotBook = task.getResult().getValue(Spot.class);
                                            if(spotBook!=null && spotBook.getName()!=null)
                                            binding.parkingSpot.setText(spotBook.getName());
                                    }else
                                    {
//                                        binding.layoutBookDetails.setVisibility(View.GONE);
                                        binding.layoutEmpty.setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                             mDatabase.getReference().child(TablesName.ParkingGroups).child(item.getGroupId())
                                     .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@androidx.annotation.NonNull Task<DataSnapshot> task) {

                                    if (task.isSuccessful() && task.getResult() != null)
                                    {
                                        group_name=task.getResult().getValue(ParkingGroups.class).getName();
                                        binding.parkingGroup.setText(group_name);
                                    }


                                    binding.progressBar.setVisibility(View.GONE);
                                }
                            });

                            Log.d("View Booking",item.getSpotId());

                            break;
                        }
                    }

                    if(!flag)
                    {
                        binding.layoutEmpty.setVisibility(View.VISIBLE);
                        binding.layoutBookDetails.setVisibility(View.GONE);
                    }
                    else
                    {
                        binding.layoutBookDetails.setVisibility(View.VISIBLE);
                        binding.layoutEmpty.setVisibility(View.GONE);
                    }
                  }
                else
                {

                    binding.layoutBookDetails.setVisibility(View.GONE);
                    binding.layoutEmpty.setVisibility(View.VISIBLE);

                    // Handle the error
                    Exception exception = task.getException();
                    if (exception != null) {
                        Log.d("error", exception.getMessage());
                    }
                }
            }
        });
    }



    public  void onCreateBooking()
    {
        replaceFregment(new home_fragment());
    }

    private void replaceFregment(Fragment fragment)
    {
        FragmentManager fragmentManager=showBookingFragment.this.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
    @Override
    public void onAlertClickOK(Object value) {

        if(value=="CancelBooking" && currentBooking!=null && currentBooking.getKey()!=null)
        {
            try
            {
                new BookingController().DeleteBooking(showBookingFragment.this.getActivity(),currentBooking);
                replaceFregment(new home_fragment());
            }
            catch (DatabaseException ex)
            {

                Log.d("message : ",ex.getMessage());

                new AlertDialog(showBookingFragment.this.getActivity()).Show("Error","Failed operation!!","","Cancel");
            }


        }

    }

    @Override
    public void onAlertClickCancel(Object value) {

    }
}