package com.example.parking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.parking.ApiClient.Controllers.SpotController;
import com.example.parking.Enum.Options;
import com.example.parking.Enum.ParkingStatus;
import com.example.parking.Helpers.AlertDialog;
import com.example.parking.Helpers.CustomAdapter;
import com.example.parking.Helpers.IAlertDialog;
import com.example.parking.Helpers.ICallback;
import com.example.parking.Helpers.IViewOptions;
import com.example.parking.Models.Spot;
import com.example.parking.Models.StatisticsParkingGroup;
import com.example.parking.Models.TablesName;
import com.example.parking.databinding.FragmentStatisticsGroupsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsGroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsGroupsFragment extends Fragment implements IViewOptions<StatisticsParkingGroup>, IAlertDialog, ICallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final FirebaseDatabase mDatabase;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    StatisticsParkingGroup selectedItem;
    private FragmentStatisticsGroupsBinding binding;
    private ListView listView;
//    ArrayList<StatisticsParkingGroup> arrayList =new ArrayList<>();
    public StatisticsGroupsFragment() {
        // Required empty public constructor
        mDatabase= FirebaseDatabase.getInstance();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticsGroupsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticsGroupsFragment newInstance(String param1, String param2) {
        StatisticsGroupsFragment fragment = new StatisticsGroupsFragment();
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

        binding= FragmentStatisticsGroupsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //here data must be an instance of the class MarsDataProvider
        return view;
    }
    @Override
    public void onStart()
    {
        super.onStart();

        StatisticsGroupsFragment.this.getActivity().setTitle(R.string.parkingGroups);
        binding.progressBar.setVisibility(View.VISIBLE);
        new SpotController().getGroupsWithSpotsStatistics(this);

    }

    @Override
    public void OnResponseCallback(Object response) {

    }

    @Override
    public void OnResponseCallback(Object response, Object data) {


    }

    private void displayAllSpotsInGroup(StatisticsParkingGroup group)
    {
            Bundle bundle = new Bundle();
            bundle.putSerializable("group", group);
            bundle.putBoolean("isViewNearSpot", false);
            // Create an instance of the target fragment
            GroupSpotsFragment spotsFragment = new GroupSpotsFragment();
            spotsFragment.setArguments(bundle);
            // Navigate to the target fragment
            FragmentManager fragmentManager=StatisticsGroupsFragment.this.getActivity().getSupportFragmentManager();
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            transaction.replace(R.id.frame_layout, spotsFragment);
            transaction.commit();

    }

    @Override
    public void OnResponseCallback(Object response, ArrayList data) {

        if(response!=null ) {
            if (data != null && response.toString().equals(Options.GroupsSpotStatistics.getValue())) {
                ArrayList<StatisticsParkingGroup> arrayList = (ArrayList<StatisticsParkingGroup>) data;
                AppendItemToCostumListView(arrayList);
            }
        }

    }
    @Override
    public View getAdapterView(LayoutInflater inflater, StatisticsParkingGroup item) {



        View convertView=inflater.inflate(R.layout.statistics_group_spot_item,null);
        Toast.makeText(StatisticsGroupsFragment.this.getActivity(), item.getGroupName(), Toast.LENGTH_SHORT).show();
        //===============================================================================
        LinearLayout parentBox =(LinearLayout)convertView.findViewById(R.id.statistics_group_spot_item);
        TextView groupName =(TextView)convertView.findViewById(R.id.statistics_group_name);
        TextView countAvailable =(TextView)convertView.findViewById(R.id.statistics_count_available);
        TextView countNotAvailable =(TextView)convertView.findViewById(R.id.statistics_count_notAvailable);
        ImageButton btnUpdate =(ImageButton)convertView.findViewById(R.id.statistics_group_btn_edit);
        ImageButton btnDelete =(ImageButton)convertView.findViewById(R.id.statistics_group_btn_delete);
        //===============================================================================

        groupName.setText(item.getGroupName());
        countAvailable.setText(item.getCountAvailable()+"");
        countNotAvailable.setText(item.getCountNotAvailable()+"");

        btnDelete.setOnClickListener(v-> onDelete(item));
        btnUpdate.setOnClickListener(v-> onUpdate(item));
        parentBox.setOnClickListener(v->onSelectedCostumItem(item));

        return convertView;
    }

    private void onUpdate(StatisticsParkingGroup item) {
        selectedItem=item;

        displayAllSpotsInGroup(item);
    }

    @Override
    public void onSelectedCostumItem(StatisticsParkingGroup item) {
    }
    @Override
    public void AppendItemToCostumListView(ArrayList<StatisticsParkingGroup> items) {

        binding.progressBar.setVisibility(View.GONE);
        CustomAdapter<StatisticsParkingGroup> customItems = new CustomAdapter<StatisticsParkingGroup>((StatisticsGroupsFragment.this.getActivity()), this, items);
        binding.listViewStatisticsGroupSpots.setAdapter(customItems);
    }


    private void deleteParkingGroup(StatisticsParkingGroup item)
    {
        DatabaseReference refSpot = mDatabase.getReference(TablesName.Spots);
        refSpot.orderByChild("groupId").equalTo(item.getGroupId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot spots= task.getResult();
                    if(spots!=null) {


                        int count=0;
                        for (DataSnapshot snapshot : spots.getChildren())
                        {
                            Spot spot=snapshot.getValue(Spot.class);
                            if(spot.getStatus()!=ParkingStatus.Available.ordinal())
                            {
                                count++;
                                new AlertDialog(StatisticsGroupsFragment.this.getActivity(),
                                        StatisticsGroupsFragment.this,null)
                                        .Show("Notify!"," Sorry, you cannot delete a parking group at this time due to reserved parking !!", "Ok", "");
                                break;
                            }
                        }

                        if(count==0)
                        {
                            DatabaseReference ref = mDatabase.getReference(TablesName.ParkingGroups);
//                        ref.child(selectedItem.getGroupId()).removeValue();
                            new AlertDialog(StatisticsGroupsFragment.this.getActivity(), StatisticsGroupsFragment.this, "").Show("!", " Successfully deleted!! ", "Ok", "Cancel");
                            new SpotController().getGroupsWithSpotsStatistics(StatisticsGroupsFragment.this);
                        }
                    }}
            }});

    }
    private void onDelete(StatisticsParkingGroup item)
    {
        selectedItem=item;
        new AlertDialog(StatisticsGroupsFragment.this.getActivity(),this,"Delete").Show("Notify!","Do you want to delete the parking group already?\n" +
                "\n" +
                "  Note: Will all positions belonging to the group be deleted?", "Yes", "Cancel");
    }

    @Override
    public void onAlertClickOK(Object value) {

        if(value!=null && value.toString().equals("Delete"))
        {
            deleteParkingGroup(selectedItem);
        }
    }

    @Override
    public void onAlertClickCancel(Object value) {

    }
}