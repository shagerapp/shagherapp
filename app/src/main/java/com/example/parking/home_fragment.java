package com.example.parking;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.parking.Helpers.CustomAdapter;
import com.example.parking.Helpers.ICallback;
import com.example.parking.Helpers.IViewOptions;
import com.example.parking.Models.ParkingGroups;
import com.example.parking.Models.TablesName;
import com.example.parking.databinding.FragmentHomeFragmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link home_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home_fragment extends Fragment implements IViewOptions<ParkingGroups>, ICallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private  LinearLayout group_dialog_box;
    private FragmentHomeFragmentBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRefrence;

    private Button btn_close_dialog;
    private  ParkingGroups parkingGroupsSelected;
    private ProgressBar progressBar;
    public home_fragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static home_fragment newInstance(String param1, String param2) {
        home_fragment fragment = new home_fragment();
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            FirebaseApp.initializeApp(home_fragment.this.getContext());


        }
        

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentHomeFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //here data must be an instance of the class MarsDataProvider
        return view;
    }


    @Override
    public void onStart()
    {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance();
        group_dialog_box=binding.getRoot().findViewById(R.id.group_dialog_box);
        btn_close_dialog=binding.getRoot().findViewById(R.id.btn_close_dialog);
        btn_close_dialog.setOnClickListener(v->hiddenDialog());

//        Button btn_show_spots=binding.getRoot().findViewById(R.id.btn_chose1);
//        Button btn_nearest_spot=binding.getRoot().findViewById(R.id.btn_chose2);
//        Button btn_show_spots_map=binding.getRoot().findViewById(R.id.btn_chose3);

//        btn_show_spots.setOnClickListener(v->displayAllSpotsInGroup(false));
//        btn_nearest_spot.setOnClickListener(v->displayAllSpotsInGroup(true));
//        btn_show_spots_map.setOnClickListener(v->viewSpotsMapInGroup());


        binding.progressBar.setVisibility(View.VISIBLE);
        getAllParkingGroups();

    }


    private void displayAllSpotsInGroup(boolean isViewNearSpot)
    {
        if(parkingGroupsSelected!=null)
        {
            Bundle bundle = new Bundle();
            bundle.putSerializable("group", parkingGroupsSelected);
            // Create an instance of the target fragment
            ParkingMapFragment spotsFragment = new ParkingMapFragment();
            spotsFragment.setArguments(bundle);
            // Navigate to the target fragment
            FragmentManager fragmentManager=home_fragment.this.getActivity().getSupportFragmentManager();
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            transaction.replace(R.id.frame_layout, spotsFragment);
            transaction.commit();
        }
    }




    private  void hiddenDialog()
    {
        binding.backgroundDialog.setVisibility(View.GONE);
    }
    private   void   getAllParkingGroups()
    {
        DatabaseReference ref= mDatabase.getReference(TablesName.ParkingGroups);
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<DataSnapshot> task) {

                        if (!task.isSuccessful())
                        {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                       else
                        {
                            ArrayList<ParkingGroups> groups=new ArrayList<>();
                            if(task.getResult().getValue()!=null)
                            {
                                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                                    ParkingGroups item=snapshot.getValue(ParkingGroups.class);
                                    if(item!=null)
                                    {
                                        item.setKey(snapshot.getKey());
                                        groups.add(item);
                                    }
                                }
                                if(groups.size()>0)
                                {
//                                    groupsList=groups;
                                    AppendItemToCostumListView(groups);
                                }
                            }
                            else
                                Log.d("response", "null");
                        }
                    }
                });
    }



    @Override
    public View getAdapterView(LayoutInflater inflater, ParkingGroups item) {
        View convertView=inflater.inflate(R.layout.list_costum_item,null);
        //===============================================================================
        LinearLayout parentBox =(LinearLayout)convertView.findViewById(R.id.listCustomItem);
        ImageView status =(ImageView)convertView.findViewById(R.id.flag_status);
        TextView textlabel1 =(TextView)convertView.findViewById(R.id.label1);
        TextView textlabel2 =(TextView)convertView.findViewById(R.id.status);
        //===============================================================================
        status.setVisibility(View.GONE);
        textlabel1.setText(item.getName());
        textlabel2.setText(item.getAddress());

        parentBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onSelectedCostumItem(item);
            }});
        return convertView;
    }

    @Override
    public void onSelectedCostumItem(ParkingGroups item)
    {
        parkingGroupsSelected=item;
        displayAllSpotsInGroup(true);
        binding.backgroundDialog.setVisibility(View.VISIBLE);
    }

    @Override
    public void AppendItemToCostumListView(ArrayList<ParkingGroups> items) {

        if(home_fragment.this.getActivity()!=null)
        {
            CustomAdapter<ParkingGroups> customAdapter = new CustomAdapter<ParkingGroups>((home_fragment.this.getActivity()), this, items);
            binding.customListViewGroups.setAdapter(customAdapter);
            binding.progressBar.setVisibility(View.GONE);
        }
    }


}