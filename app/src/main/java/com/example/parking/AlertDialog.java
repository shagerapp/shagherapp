package com.example.parking;

import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AlertDialog {

    AppCompatActivity _Activity;
    public AlertDialog(AppCompatActivity activity){
        _Activity=activity;
    }

    public void Show(String _title,String _message,String buttonName1,String buttonName2)
    {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(_Activity);

        builder.setTitle(_title);
        builder.setMessage(_message);
        builder.setIcon(R.drawable.ic_baseline_access_time_24);


        builder.setCancelable(false);
        if(buttonName1!="")
        {
            builder.setPositiveButton(buttonName1,  new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(_Activity,"yes",Toast.LENGTH_SHORT).show();
                }

            });
        }

        if(buttonName2!="") {
            builder.setNegativeButton(buttonName1,  new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(_Activity,"No",Toast.LENGTH_SHORT).show();
                }

            });
        }


        builder.show();
    }

}
