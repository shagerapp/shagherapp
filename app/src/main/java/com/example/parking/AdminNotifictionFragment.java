package com.example.parking;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.parking.Enum.NotifictionStatus;
import com.example.parking.Enum.ParkingStatus;
import com.example.parking.Helpers.AlertDialog;
import com.example.parking.Helpers.CustomAdapter;
import com.example.parking.Helpers.IAlertDialog;
import com.example.parking.Helpers.IViewOptions;
import com.example.parking.Models.ResponseNotifiction;
import com.example.parking.Models.Spot;
import com.example.parking.Models.TablesName;
import com.example.parking.databinding.FragmentAdminNotifictionBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminNotifictionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminNotifictionFragment extends Fragment implements IAlertDialog,IViewOptions<ResponseNotifiction> {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentAdminNotifictionBinding binding;
    private FirebaseDatabase mDatabase;
    private FirebaseUser firebaseUser;
    private ArrayList<ResponseNotifiction> notifictionsList;
    private ResponseNotifiction selectedNotify;

    public AdminNotifictionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminNotifictionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminNotifictionFragment newInstance(String param1, String param2) {
        AdminNotifictionFragment fragment = new AdminNotifictionFragment();
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
        binding= FragmentAdminNotifictionBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //here data must be an instance of the class MarsDataProvider
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDatabase= FirebaseDatabase.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        getAllNotifictions(NotifictionStatus.NEW);

//        getAllNotifictions(NotifictionStatus.ISVIEW);

    }

    @Override
    public View getAdapterView(LayoutInflater inflater, ResponseNotifiction item) {
        View convertView=inflater.inflate(R.layout.notifiy_item,null);
        //===============================================================================
        LinearLayout parentBox =(LinearLayout)convertView.findViewById(R.id.notifiy_item);
        LinearLayout continer_clcik =(LinearLayout) convertView.findViewById(R.id.notifiy_item_click);
        TextView title =(TextView)convertView.findViewById(R.id.notifiy_item_title);
        TextView body =(TextView)convertView.findViewById(R.id.notifiy_item_body);
        TextView date =(TextView)convertView.findViewById(R.id.notifiy_item_date);
        ImageView btnDelete =(ImageView)convertView.findViewById(R.id.notifiy_btn_delete);


        //===============================================================================

        title.setText(item.getTitle());
        body.setText(item.getBody());
        date.setText(item.getDate());

        continer_clcik.setOnClickListener(v->  onSelectedCostumItem(item));


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new AlertDialog(AdminNotifictionFragment.this.getActivity(),AdminNotifictionFragment.this,item).Show("notifiction!","Do you agree to delete the current notice?","yes","no");
            }});

        parentBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    onSelectedCostumItem(item);
                }});


        binding.btnCancel.setOnClickListener(v->{  binding.backgroundControlDialogBox.setVisibility(View.GONE);});
            return convertView;
    }

    private void onChangedState(Spot spot) {

        int status=ParkingStatus.Available.ordinal();
        if(spot.getStatus()==ParkingStatus.Available.ordinal())
            status=ParkingStatus.BookedUp.ordinal();

        DatabaseReference ref = mDatabase.getReference().child(TablesName.Spots).child(spot.getId());
        ref.child("status").setValue(status).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                if(selectedNotify!=null)
                {
                    DatabaseReference refNot = mDatabase.getReference().child(TablesName.Notifictions);
                    refNot.child(selectedNotify.getKey()).removeValue();
                    getAllNotifictions(NotifictionStatus.NEW);
                }
                binding.backgroundControlDialogBox.setVisibility(View.GONE);
            }

            getAllNotifictions(NotifictionStatus.NEW);
        });
    }

    @Override
    public void onSelectedCostumItem(ResponseNotifiction notify)
    {

        selectedNotify=notify;
        DatabaseReference ref = mDatabase.getReference().child(TablesName.Spots).child(notify.getTag());
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DataSnapshot dataSnapshot = task.getResult();
                    if(dataSnapshot!=null)
                    {
                        Spot spot=dataSnapshot.getValue(Spot.class);
                        if(dataSnapshot.getKey()!=null && spot!=null) {
                            spot.setId(dataSnapshot.getKey());
                            if (spot != null) {
                                if (spot.getStatus() != ParkingStatus.Available.ordinal()) {
                                    binding.spotState.setText("Current State: occupied");
                                    binding.spotState.setTextColor(Color.RED);
                                } else {
                                    binding.spotState.setText("Current State: Unoccupied");
                                    binding.spotState.setTextColor(Color.GREEN);
                                }


                                binding.spotName.setText(spot.getName());
                                binding.backgroundControlDialogBox.setVisibility(View.VISIBLE);
                                @SuppressLint("ResourceType")
                                Animation animation = AnimationUtils.loadAnimation(AdminNotifictionFragment.this.getActivity(), R.animator.animation_dialog);
                                binding.controlDialogBox.startAnimation(animation);
                                binding.controlDialogBox.setVisibility(View.VISIBLE);
                                binding.btnChangedState.setOnClickListener(v -> onChangedState(spot));
                            }
                        }
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

    @Override
    public void AppendItemToCostumListView(ArrayList<ResponseNotifiction> items) {

        CustomAdapter<ResponseNotifiction> customItems = new CustomAdapter<ResponseNotifiction>((AdminNotifictionFragment.this.getActivity()), this, items);
//        binding.listViewAdminNotifictions= binding.getRoot().findViewById(R.id.listView_admin_notifictions);
        binding.listViewAdminNotifictions.setAdapter(customItems);
        binding.adminNotifictionsProgressBar.setVisibility(View.GONE);

    }

    private   void   getAllNotifictions(NotifictionStatus status)
    {
        binding.adminNotifictionsProgressBar.setVisibility(View.VISIBLE);
        DatabaseReference ref = mDatabase.getReference().child(TablesName.Notifictions);
//        Query query = ref.orderByChild("status").equalTo(NotifictionStatus.NEW.ordinal());
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DataSnapshot dataSnapshot = task.getResult();
                    notifictionsList = new ArrayList<ResponseNotifiction>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        // Get the question object
                        ResponseNotifiction notify =  snapshot.getValue(ResponseNotifiction.class);
                        if (notify != null && notify.getTopic() !=null && notify.getTopic().equals(firebaseUser.getUid()))
                        {
                            notify.setKey(snapshot.getKey());
                            notifictionsList.add(notify);
                        }
                    }

                    if(notifictionsList!=null)
                        AppendItemToCostumListView(notifictionsList);

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

                binding.adminNotifictionsProgressBar.setVisibility(View.GONE);
            }
        });


    }

    @Override
    public void onAlertClickOK(Object value) {

        if(value!=null)
        {
            DatabaseReference ref = mDatabase.getReference().child(TablesName.Notifictions);
            ref.child(((ResponseNotifiction)value).getKey()).removeValue();
            getAllNotifictions(NotifictionStatus.NEW);
        }

    }

    @Override
    public void onAlertClickCancel(Object value) {

    }
}