package com.example.parking.Services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.parking.ApiClient.Controllers.BookingController;
import com.example.parking.ApiClient.Controllers.TrackinUserController;
import com.example.parking.ApiClient.Enums.UserLocationState;
import com.example.parking.ApiClient.interfaces.ITrackingUserResponse;
import com.example.parking.ApiClient.models.BaseResponse;
import com.example.parking.Enum.Options;
import com.example.parking.Helpers.ICallback;
import com.example.parking.Helpers.LocalStorage;
import com.example.parking.Models.UserLocationStateModel;
import com.example.parking.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LocationTrackingService extends Service  implements ITrackingUserResponse, ICallback {


    private final IBinder mBinder = new MyBinder();
    private static final String CHANNEL_ID ="2" ;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private UserLocationStateModel userLocationStateModel;
    private FirebaseUser firebaseUser;
    private TrackinUserController trackingUser;
    private LocationTrackingService  myContext;
    private BookingController bookingController;

    @Override
    public void onCreate() {

        super.onCreate();
        Log.d("MyService","Created !!");
        myContext = this;
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        trackingUser=new TrackinUserController(this);
        bookingController=new BookingController();
        requestLocationUpdates();

    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Log.d("MyService","Started !!");
        userLocationStateModel = new UserLocationStateModel();
        if(LocalStorage.existing(this,"BookingId"))
        {
            String bookId = LocalStorage.getValue(this, LocalStorage.getDefaultStorageName(), "BookingId").toString();
            if (bookId != null) {
                userLocationStateModel.setUserId(firebaseUser.getUid());
                userLocationStateModel.setBookingId(bookId);
            }
        }

        buildNotification();
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

     try {
         if (fusedLocationProviderClient != null && locationCallback != null)
             fusedLocationProviderClient.removeLocationUpdates(locationCallback);
     }catch (Exception ex)
     {
         Toast.makeText(LocationTrackingService.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
     }

    }


    private void buildNotification() {

        String stop = "stop";
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(

                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the persistent notification

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Location tracking is working")
                .setOngoing(true)
                .setContentIntent(broadcastIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(false);
            channel.setDescription("Location tracking is working");
            channel.setSound(null, null);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
        startForeground(1, builder.build());
    }

    private void requestLocationUpdates() {

        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(3000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(myContext);
        int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permission1 == PackageManager.PERMISSION_GRANTED
                && permission2 == PackageManager.PERMISSION_GRANTED) {

            // Request location updates and when an update is
            // received, store the location in Firebase
            Task<Void> voidTask = fusedLocationProviderClient.requestLocationUpdates(request, locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                userLocationStateModel.setLatitude(locationResult.getLastLocation().getLatitude());
                userLocationStateModel.setLongitude(locationResult.getLastLocation().getLongitude());
                    userLocationStateModel.setLatitude(21.582138751365104);
                    userLocationStateModel.setLongitude(39.18204463095501);
                    bookingController.checkIfUserHasBooking(myContext);
                    trackingUser.checkUserLocationState(userLocationStateModel, myContext);

                }

            }, null);

        } else {

            stopSelf();

        }

    }

    @Override
    public void OnResponseCallback(Object response){


        if (response.equals(Options.NOTHASBOOKED.getValue())) {
            if (LocalStorage.existing(myContext, "BookingId"))
                LocalStorage.remove(myContext, "BookingId");
            stopSelf();
        }
    }
        @Override
    public void onSuccess(BaseResponse response) {

    try{
            if(response!=null && response.getResultCode()==200 )
            {
                if (response.getResult().equals(UserLocationState.OUTSIDE.getValue())) {
                    stopSelf();
                }

                Log.d("LocationState->", response.getResult());
//                Toast.makeText(myContext, "LocationState->"+response.getResult(), Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(myContext, "Error->"+response.getResultMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
        Toast.makeText(LocationTrackingService.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
    }

    }



    @Override
    public void onFailure(String call) {

    }

    public class MyBinder extends Binder {

        public LocationTrackingService getService() {

            return myContext;

        }

    }

}