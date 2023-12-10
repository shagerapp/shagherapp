package com.example.parking.ApiClient.Controllers;

import androidx.annotation.NonNull;

import com.example.parking.Enum.Options;
import com.example.parking.Enum.ParkingStatus;
import com.example.parking.Helpers.ICallback;
import com.example.parking.Models.ParkingGroups;
import com.example.parking.Models.Spot;
import com.example.parking.Models.StatisticsParkingGroup;
import com.example.parking.Models.TablesName;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class SpotController  {

    private final FirebaseDatabase mDatabase;

    public SpotController()
    {
        mDatabase= FirebaseDatabase.getInstance();
    }

    public void getGroupsWithSpotsStatistics(ICallback callback)
    {

        ArrayList<StatisticsParkingGroup> statistics=new ArrayList<>();
        DatabaseReference ref = mDatabase.getReference().child(TablesName.ParkingGroups);
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataGroups= task.getResult();

                    if(dataGroups!=null)
                    {

                        for (DataSnapshot snapshot:dataGroups.getChildren()) {
                            ParkingGroups group=snapshot.getValue(ParkingGroups.class);
                            if (group != null) {
                                group.setKey(snapshot.getKey());
                                DatabaseReference ref = mDatabase.getReference().child(TablesName.Spots);
                                Query query=ref.orderByChild("groupId").equalTo(snapshot.getKey());
                                query.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DataSnapshot dataSnapshot = task.getResult();

                                            if (dataSnapshot != null)
                                            {

                                                ArrayList<Spot> items=new ArrayList<>();
                                                for (DataSnapshot dt:dataSnapshot.getChildren())
                                                {
                                                    try {
                                                        Spot spot=dt.getValue(Spot.class);
                                                        items.add(spot);

                                                    }catch ( Exception ex){}

                                                }

                                                long[] count = getSpotsStatisticsByGroup(items);
                                                StatisticsParkingGroup stGroup=new StatisticsParkingGroup();
                                                stGroup.setGroupId(group.getKey());
                                                stGroup.setGroupName(group.getName());
                                                stGroup.setCountAvailable((long) count[0]);
                                                stGroup.setCountNotAvailable(((long) count[1]+(long) count[2]));
                                                statistics.add(stGroup);

                                                if(statistics.size()>=dataGroups.getChildrenCount())
                                                    callback.OnResponseCallback(Options.GroupsSpotStatistics.getValue(),statistics);
//                                                else
//                                                    callback.OnResponseCallback(Options.GroupsSpotStatistics.getValue(),new Gson().toJson(stGroup));

                                            }
                                        }
                                    }
                                });
                            }
                            ;
                        }

                        }

                    }
                }



        });

        //        HashMap<ParkingStatus,Long> statistics=new HashMap<ParkingStatus, Long>();
        //        statistics.put(ParkingStatus.Available,getCountSpot(ParkingStatus.Available));
        //        statistics.put(ParkingStatus.BookedUp,getCountSpot(ParkingStatus.BookedUp));
        //        statistics.put(ParkingStatus.TemporarilyReserved,getCountSpot(ParkingStatus.TemporarilyReserved));

    }

    public void getSpotsStatistics(ICallback callback)
    {
        getSpotsStatistics(callback,null);
    }
    public void getSpotsStatistics(ICallback callback,String groupId)
    {

            DatabaseReference ref = mDatabase.getReference().child(TablesName.Spots);
            Query query=ref;
            if(groupId!=null && !groupId.isEmpty())
                 query = ref.orderByChild("groupId").equalTo(groupId);
            query.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();

                    if(dataSnapshot!=null)
                    {
                        ArrayList<Spot> items=new ArrayList<>();
                        for (DataSnapshot dt:dataSnapshot.getChildren())
                        {
                            Spot spot=dt.getValue(Spot.class);
                            items.add(spot);
                        }
                        long[] count = getSpotsStatisticsByGroup(items);
                        callback.OnResponseCallback(Options.SpotStatistics.getValue(),count);

                    }
                }


            }
        });

    }

    private long[] getSpotsStatisticsByGroup(ArrayList<Spot> items) {

        long[] count = new long[]{0, 0, 0};

        for (Spot spot : items)
        {

            if (spot.getStatus() == ParkingStatus.Available.ordinal())
                count[0]++;
            else if (spot.getStatus() == ParkingStatus.TemporarilyReserved.ordinal())
                count[1]++;
            else if (spot.getStatus() == ParkingStatus.BookedUp.ordinal())
                count[2]++;
        }

        return  count;
    }
}
