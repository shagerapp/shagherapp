package com.example.parking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parking.Helpers.AlertDialog;
import com.example.parking.Helpers.DialogBox;
import com.example.parking.Helpers.ICallback;
import com.example.parking.Models.Booking;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;

public class ViewBooking extends AppCompatActivity  implements ICallback
{



    DialogBox _DialogBox;
    TextView  txt_bookId,txt_spotName,txt_groupName,txt_startTime,txt_endTime;
    private  String bookId=null;
    private  Booking book;

    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_bookings);
        Intent intent=getIntent();
        getSupportActionBar().hide();

        FirebaseApp.initializeApp(this);



        _DialogBox=new DialogBox(ViewBooking.this);
        txt_bookId = (TextView) ViewBooking.this.findViewById(R.id.bookId);
        txt_spotName = (TextView) ViewBooking.this.findViewById(R.id.spotName);
        txt_groupName = (TextView) ViewBooking.this.findViewById(R.id.groupName);
        txt_startTime = (TextView) ViewBooking.this.findViewById(R.id.startTime);
        txt_endTime = (TextView) ViewBooking.this.findViewById(R.id.endTime);
        _DialogBox.SetDataText("Message","Do you really want to Remove the booking ?");
        _DialogBox.BtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bookId!=null && !bookId.isEmpty() && book!=null)
                {
                  
                }


            }
        });
        _DialogBox.BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _DialogBox.setInVisible();
            }
        });




    }



    public void OnClickDialogBtnOK(View v)
    {

    }

    public void OnClickDialogBtnCancel(View v)
    {
        _DialogBox.setInVisible();
    }

    public   void OnClickHomeBtn(View v)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public   void OnClickBackBtn(View v)
    {
        this.finish();
    }

    public void OnClickFinishBtn(View v)
    {
       // new AlertDialog(ViewBooking.this).Show("Message", "  The reservation has been successfully cancelled ", "ok", "");
        Intent intent = new Intent(ViewBooking.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    public void OnClickDeleteBtn(View v) {
        _DialogBox.setVisible();
    }




    @Override
    public void OnResponseCallback(Object response) {

        if(response!=null && (boolean)response)
        {
            _DialogBox.setInVisible();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else
        {
            new AlertDialog(ViewBooking.this).Show("info", "! error ", "ok", "");
        }

    }

    @Override
    public void OnResponseCallback(Object response, Object data) {

    }

    @Override
    public void OnResponseCallback(Object response, ArrayList data) {

    }
}