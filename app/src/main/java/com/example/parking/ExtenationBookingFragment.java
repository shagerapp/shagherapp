package com.example.parking;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.parking.Enum.ParkingStatus;
import com.example.parking.Helpers.AlertDialog;
import com.example.parking.Helpers.CurrentTime;
import com.example.parking.Models.Booking;
import com.example.parking.Models.TablesName;
import com.example.parking.databinding.FragmentExtenationBookingBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExtenationBookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExtenationBookingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private  Handler handler;
    Booking  booking;
    String spotName,groupName;
    FragmentExtenationBookingBinding binding;
    private boolean isTimeChanged;
    private Handler handelr=new Handler();
    private FirebaseDatabase mDatabase;
    private int extenationTime=1;

    private final int minValue = 1; // الحد الأدنى للقيمة
    private final int maxValue = 15; // الحد الأقصى للقيمة
    public ExtenationBookingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExtenationBookingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExtenationBookingFragment newInstance(String param1, String param2) {
        ExtenationBookingFragment fragment = new ExtenationBookingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        handler=new Handler();

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            booking=  (Booking) getArguments().getSerializable("booking");
            spotName = getArguments().getString("spot");
            groupName = getArguments().getString("group");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentExtenationBookingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //here data must be an instance of the class MarsDataProvider
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();


        this.getActivity().setTitle("");
        mDatabase=FirebaseDatabase.getInstance();

        binding.parkingSpot.setText(spotName);
        binding.parkingGroup.setText(groupName);
        binding.date.setText(CurrentTime.getCurrentDate("yyyy-MM-dd"));
        binding.carPlate.setText(booking.getEndTime());
        int duration=CurrentTime.getDurationTime(booking.getEndTime());
        binding.duration.setText(duration+" hours");
        binding.hours.setText(booking.getStartTime() +" - "+booking.getEndTime());

        binding.seekBar.setMax(maxValue - minValue); 
        binding.seekBar.setProgress(1);
        binding.hourLabel.setText("hour");
        binding.extenationHours.setText("1");

        String time=CurrentTime.getTimeAfterAddHours(CurrentTime.getCurrentTime(""),1);
        binding.viewExtenationTime.setText(time);

        binding.seekBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                handler.post(new Runnable() {
                    @Override
                    public void run() {


                        extenationTime= progress + minValue;
                        binding.extenationHours.setText(extenationTime+"");
                        String label=" hour ";
                        if(extenationTime>1)
                        {
                            label=(extenationTime > 10) ? "  hour " : " hours ";
                            binding.hourLabel.setText((extenationTime > 10) ? "  hour " : " hours ");

                        }
                        else
                            binding.hourLabel.setText("hour");

                        String time=CurrentTime.getTimeAfterAddHours(CurrentTime.getCurrentTime(""),extenationTime);
                        binding.viewExtenationTime.setText(time);


                    }
                });


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // معالجة بداية التتبع
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // معالجة نهاية التتبع
            }
        });

        binding.BookingBtn.setOnClickListener(v->onExtensionBookingTime());
        binding.btnBack.setOnClickListener(v->onBack());




    }

    private void onBack()
    {
        replaceFregment(new showBookingFragment());
    }
    private void onExtensionBookingTime() {


        if(booking==null || extenationTime<=0)
            return;


        String exten_time=CurrentTime.getTimeAfterAddHours(booking.getEndTime(),extenationTime);

        Task<Void> endTime1 = mDatabase.getReference().child(TablesName.Bookings).child(booking.getKey())
                .child("endTime").setValue(exten_time).addOnCompleteListener(task->{
                    if (task.isSuccessful())
                    {
                        mDatabase.getReference().child(TablesName.Spots).child(booking.getSpotId())
                        .child("status").setValue(ParkingStatus.Available.ordinal()).addOnCompleteListener(task2->{
                        if (task2.isSuccessful())
                        {
                            new AlertDialog(ExtenationBookingFragment.this.getActivity()).Show("Notify!"," Your reservation period has been successfully extended","Yes","");
                            binding.progressBar.setVisibility(View.GONE);
                            replaceFregment(new showBookingFragment());

                        }});


                    }

                });



    }

    private void replaceFregment(Fragment fragment)
    {
        FragmentManager fragmentManager=ExtenationBookingFragment.this.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }


}