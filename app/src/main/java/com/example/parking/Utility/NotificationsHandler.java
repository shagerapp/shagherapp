package com.example.parking.Utility;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.parking.Enum.NotifictionMsgType;
import com.example.parking.Models.NotifictionModel;
import com.example.parking.NotifiyActivity;

public class NotificationsHandler {

    private final NotifictionModel notifiction;
    private Context context;
    private NotificationCompat.Builder builder;

    public  NotificationsHandler(NotifictionModel notifiction, Context context){
        this.notifiction = notifiction;
        this.context = context;
    }

    public void Handler(NotificationCompat.Builder builder){
        this.builder = builder;

        if( this.notifiction!=null)
       {
           int type=this.notifiction.getType();
            if (type == NotifictionMsgType.General.getValue())
                general();
            else if (type ==  NotifictionMsgType.Notice.getValue())
                notice();
            else if (type ==  NotifictionMsgType.Message.getValue())
                message();
            else if (type ==  NotifictionMsgType.EndBookingTimeApproaching.getValue())
                endBookingTimeApproaching();
            else if (type ==  NotifictionMsgType.FinishedBooking.getValue())
                finishedBooking();
            else if (type ==  NotifictionMsgType.EndTemporaryVirtualBooking.getValue())
                endTemporaryVirtualBooking();
            else
                   throw new IllegalStateException("Unexpected value: " + this.notifiction.getType());
           }
       }

       private  void startActivity(Intent intent)
       {
           intent.putExtra("title", notifiction.getTitle());
           intent.putExtra("body",  notifiction.getBody());
           intent.putExtra("topic",  notifiction.getTopic());
           intent.putExtra("msgType",  notifiction.getType());
           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
           PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
           builder.setContentIntent(pendingIntent);
           context.startActivity(intent);
       }
       public void general(){  startActivity(new Intent(this.context,NotifiyActivity.class)); }
       public void notice()
       {
           startActivity(new Intent(this.context,NotifiyActivity.class));
       }
       public void message(){
           startActivity(new Intent(this.context,NotifiyActivity.class));
       }
       public void endBookingTimeApproaching(){

           startActivity(new Intent(this.context,NotifiyActivity.class));

       }
       public void finishedBooking(){
           startActivity(new Intent(this.context,NotifiyActivity.class));
       }
       public void endTemporaryVirtualBooking(){
           startActivity(new Intent(this.context,NotifiyActivity.class));
       }


}
