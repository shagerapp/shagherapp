package com.example.parking.ApiClient.Controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.parking.ApiClient.AppConfig;
import com.example.parking.ApiClient.interfaces.IBookingStatistics;
import com.example.parking.ApiClient.interfaces.IResponce;
import com.example.parking.ApiClient.models.BaseResponse;
import com.example.parking.Enum.Options;
import com.example.parking.Enum.ParkingStatus;
import com.example.parking.Helpers.AlertDialog;
import com.example.parking.Helpers.ICallback;
import com.example.parking.Helpers.LocalStorage;
import com.example.parking.Models.Booking;
import com.example.parking.Models.Spot;
import com.example.parking.Models.TablesName;
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

import java.util.HashMap;

import retrofit2.Call;


public class BookingController implements IResponce<BaseResponse> {

    private final FirebaseDatabase mDatabase;
    IBookingStatistics callback;
    public BookingController()
    {
        mDatabase= FirebaseDatabase.getInstance();
    }

    public  void checkIfUserHasBooking(ICallback callback)
    {
        checkIfUserHasBooking(callback,null);
    }
    public  void checkIfUserHasBooking(ICallback callback,Object flag)
    {


        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null)
        {
            DatabaseReference ref = mDatabase.getReference().child(TablesName.Bookings);
            Query query = ref.orderByChild("userId").equalTo(firebaseUser.getUid());
            query.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @SuppressLint("SuspiciousIndentation")
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        DataSnapshot dataSnapshot = task.getResult();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Get the question object
                            Booking item = snapshot.getValue(Booking.class);
                            if (item != null && item.getStatus())
                            {
                                if(callback==null)
                                    return;

                                if(flag!=null)
                                    callback.OnResponseCallback(Options.HASBOOKED.getValue(),flag);
                                else
                                    callback.OnResponseCallback(Options.HASBOOKED.getValue());
                                return;
                            }
                        }
                    }

                    if(callback==null)
                        return;

                    if(flag!=null)
                        callback.OnResponseCallback(Options.NOTHASBOOKED.getValue(),flag);
                    else
                        callback.OnResponseCallback(Options.NOTHASBOOKED.getValue());

                }
            });
        }
    }

    public void sendSimpleNotify(String userId)  {

        StringBuilder tringBuilder=new StringBuilder();
        tringBuilder.append(AppConfig.API_URL).append("Service/").append("sendNotifyAfterUserBooked").append("?");
        tringBuilder.append("userId=").append(userId);

        Call<BaseResponse> call = ApiRequest.getService().sendNotifyAfterUserBooked(tringBuilder.toString());
        new ApiRequest().executeRequest(call,this);
    }

    public  void DeleteBooking(Context context,Booking book)
    {

        mDatabase.getReference().child(TablesName.Spots).child(book.getSpotId())
                .child("status").setValue(ParkingStatus.Available.ordinal());

        mDatabase.getReference().child(TablesName.Bookings).child(book.getKey()).child("status").setValue(false)
                .addOnCompleteListener(btask->{
                    if(btask.isSuccessful())
                        LocalStorage.remove(context, "BookingId");
                });

        LocalStorage.remove(context, "BookingId");
        new AlertDialog(context).Show("Notify!"," Your reservation has been successfully cancelled","Yes","");


    }

    public  void DeleteBooking(Context context,String bookId)
    {
        DatabaseReference ref = mDatabase.getReference().child(TablesName.Bookings).child(bookId);
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    Booking item = dataSnapshot.getValue(Booking.class);
                    Log.d("DeleteBooking",String.valueOf(item));
                    if(item!=null)
                    {
                        mDatabase.getReference().child(TablesName.Spots).child(item.getSpotId())
                                .child("status").setValue(ParkingStatus.Available.ordinal())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {

                                            mDatabase.getReference().child(TablesName.Bookings).child(bookId).child("status").setValue(false)
                                                    .addOnCompleteListener(btask->{
                                                        if(btask.isSuccessful())
                                                            LocalStorage.remove(context, "BookingId");
                                                    });

                                        }
                                    }
                                });
                    }
                }
            }
        });

    }

    public  void AdminDeleteBooking(Context context, Spot spot,ICallback callback)
    {
        if(spot==null || spot.getId()==null)
            return;

        mDatabase.getReference().child(TablesName.Spots).child(spot.getId())
                .child("status").setValue(ParkingStatus.Available.ordinal())
                .addOnCompleteListener(btask->{
                    if (btask.isSuccessful()) {

                        mDatabase.getReference().child(TablesName.Bookings).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DataSnapshot dataSnapshot = task.getResult();
                                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                                    {
                                        Booking item = snapshot.getValue(Booking.class);
                                        if (item != null && item.getSpotId()!=null && item.getSpotId().equals(spot.getId()) && item.getStatus())
                                        {
                                            item.setKey(snapshot.getKey());
                                            if(LocalStorage.existing(context,"BookingId"))
                                            {
                                                LocalStorage.remove(context, "BookingId");
                                                callback.OnResponseCallback("CancelBooked","Cancel booked has Successfull !!");
                                            }
                                            else
                                            callback.OnResponseCallback("CancelBooked","Cancel booked has Error !!");

                                            break;
                                        }

                                    }
                                }
                            }
                        });

                    }
                    else
                        callback.OnResponseCallback("CancelBooked","Cancel booked has Error !!");
                });

    }


    public  void getBookingsStatisticsPerHours(String queryDate, IBookingStatistics callback)
    {
        this.callback=callback;
        Log.d("queryDate",queryDate);
        DatabaseReference databaseRef = mDatabase.getReference().child(TablesName.Bookings);
        Query query = databaseRef.orderByChild("date");
        query = query.startAt(queryDate).endAt(queryDate + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot==null)
                    return;
                HashMap<String,Integer> statistics=new HashMap<String, Integer>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

//                    Log.d("snapshot",new Gson().toJson(snapshot));
                        Booking item=snapshot.getValue(Booking.class);
                        if (item != null)
                        {
                            String[] sTime=item.getStartTime().trim().split(" ");
                            if(sTime.length>1)
                            {
                                String hour= sTime[0].split(":")[0];
                                String key=hour+" "+sTime[1];
                                if(!statistics.containsKey(key))
                                    statistics.put(key,1);
                                else
                                    statistics.put(key,statistics.get(key)+1);
                            }
                        }
                    }
                callback.onBookingStatisticsResponse(statistics);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    public  void DeleteTemporaryBooking(String bookId)
    {
        mDatabase.getReference().child(TablesName.Spots).child(bookId)
                .child("status").setValue(ParkingStatus.Available.ordinal());

        DatabaseReference ref = mDatabase.getReference().child(TablesName.BookingTemp);
        ref.child(bookId).removeValue();

    }


    @Override
    public void onSuccess(BaseResponse call) {

    }

    @Override
    public void onFailure(String call) {

    }
}
