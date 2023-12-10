package com.example.parking.Services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.parking.ApiClient.Controllers.UserController;
import com.example.parking.Enum.NotifictionMsgType;
import com.example.parking.Enum.NotifictionType;
import com.example.parking.Models.NotifictionModel;
import com.example.parking.Models.RegisterationToken;
import com.example.parking.NotifiyActivity;
import com.example.parking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "FirebaseMessage";
    private static final String CHANNEL_ID = R.string.default_notification_channel_id+"" ;
    public static final int NOTIFICATION_ID = 1 ;

    private Context context;


    public  MyFirebaseMessagingService()
    {
        onTokenRefresh();
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        onTokenRefresh();
    }

    public void onTokenRefresh() {
        // Get updated InstanceID token.
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {

                            try {
                                String token = task.getResult();
                                if (FirebaseAuth.getInstance() != null) {

                                    RegisterationToken newToken = new RegisterationToken();
                                    newToken.setUserId(FirebaseAuth.getInstance().getUid());
                                    newToken.setToken(token);
                                    new UserController().setToken(newToken, MyFirebaseMessagingService.this);

                                }
                                // Handle the token here

                                else {
                                    // Handle the error
                                }
                            } catch (Exception ex) {

                                Toast.makeText(MyFirebaseMessagingService.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        context = MyFirebaseMessagingService.this.getApplicationContext();
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("From: ", remoteMessage.getFrom());

        try
        {
           if (remoteMessage != null) {

            RemoteMessage.Notification notification = remoteMessage.getNotification();

            // Check if message contains a notification payload.
            if (notification != null)
            {

                String topic = remoteMessage.getData().getOrDefault("topic", NotifictionMsgType.General.getValue() + "");
                showNotification(topic, notification.getTitle(), notification.getBody(),remoteMessage.getData().getOrDefault("tag",""));

            }


        }
        } catch (Exception ex) {
            Toast.makeText(MyFirebaseMessagingService.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void showNotification(String topic,String title, String body,String tag) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                try{

                    if(topic.equals(NotifictionMsgType.General.getValue()) ||  (FirebaseAuth.getInstance().getUid()!=null && topic.equals(FirebaseAuth.getInstance().getUid())))
                    {


                        NotifictionModel notify=new NotifictionModel();
                        notify.setTitle(title);
                        notify.setBody(body);
                        notify.setTopic(topic);

                        if(tag!=null)
                        {
                            if (tag.trim().equals("Extend"))
                                notify.setType(NotifictionType.NOTIFY_2.ordinal());
    //                        else if(tag.trim().equals("CancelBooking") && Helper.isServiceRunning(LocationTrackingService.class, MyFirebaseMessagingService.this))
    //                        {
    //                            if(LocalStorage.existing(MyFirebaseMessagingService.this,"BookingId"))
    //                                LocalStorage.remove(MyFirebaseMessagingService.this,"BookingId");
    //
    //                            Intent intentService=new Intent(MyFirebaseMessagingService.this, LocationTrackingService.class);
    //                            stopService(intentService) ;
    //                        }
                        }


                        Intent intent=new Intent(MyFirebaseMessagingService.this, NotifiyActivity.class);
                        intent.putExtra("title", title);
                        intent.putExtra("body",  body);
                        intent.putExtra("topic",topic);
                        intent.putExtra("type",NotifictionType.NOTIFY_2.ordinal());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, NOTIFICATION_ID, intent, PendingIntent.FLAG_ONE_SHOT);



                        Bitmap bitmap_icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo1);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyFirebaseMessagingService.this.getApplicationContext(), "channel_id")
                                .setContentTitle(title)
                                .setContentText(body)
                                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                                .setContentIntent(pendingIntent)
                                .setLargeIcon(bitmap_icon)
                                .setAutoCancel(true)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setSound(Settings.System.DEFAULT_RINGTONE_URI);
//                                .addAction(R.drawable.baseline_open_in_browser_24,"Open",lukePIntent)
//                                .addAction(R.drawable.baseline_close_24,"Close",pendingIntent);




                        // إظهار الإشعار
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyFirebaseMessagingService.this.getApplicationContext());
                        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }

                        notificationManager.notify(NOTIFICATION_ID, builder.build());

                        MyFirebaseMessagingService.this.startActivities(new Intent[]{intent});

//                        new NotificationsHandler(notify,context).Handler(builder);


                    }

                } catch (Exception ex) {
                    Toast.makeText(MyFirebaseMessagingService.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

//    private void replaceFregment(AppCompatActivity context,Fragment fragment)
//    {
//        FragmentManager fragmentManager=context.getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout,fragment);
//        fragmentTransaction.commit();
//    }

}
