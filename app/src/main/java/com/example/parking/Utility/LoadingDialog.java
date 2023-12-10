package com.example.parking.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.parking.R;
import com.example.parking.databinding.DialogLayoutBinding;


public class LoadingDialog {

    private Activity activity;
    private AlertDialog alertDialog;
    private  boolean isStarted,isStoped;

    public boolean started() { return isStarted;}
    public boolean stoped() { return isStoped;}

    public void  setStarted(boolean value) {isStarted=value;}

    public LoadingDialog(Activity activity) {

        this.activity = activity;
        isStarted=false;
        isStoped=false;
    }

    public void startLoading() {

        if(!isStarted)
        {
            isStarted=true;
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.loadingDialogStyle);
            DialogLayoutBinding binding = DialogLayoutBinding.inflate(LayoutInflater.from(activity), null, false);
            builder.setView(binding.getRoot());
            builder.setCancelable(false);
            alertDialog = builder.create();
            binding.rotateLoading.start();
            alertDialog.show();
        }


    }

    public void stopLoading() {
        if(!isStoped)
        {
            isStoped=true;
            alertDialog.dismiss();
        }

    }
}
