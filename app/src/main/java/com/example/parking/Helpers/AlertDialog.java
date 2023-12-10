package com.example.parking.Helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.FragmentActivity;

import com.example.parking.R;


public class AlertDialog {


   private IAlertDialog context;
    private Object flag;
    private   androidx.appcompat.app.AlertDialog.Builder builder;
    Context _Activity;
//    Context _context;
    private static boolean isOk=false;
    private String message;
    private Context title;


    public AlertDialog(Context activity)
    {

        _Activity=activity;
         builder = new androidx.appcompat.app.AlertDialog.Builder(_Activity);

    }
    public AlertDialog(FragmentActivity activity,IAlertDialog context,Object flag)
    {
        this(activity);
        this.context=context;
        this.flag=flag;

    }
    public boolean IsOk(){ return  isOk;}
    public boolean IsCancel(){return  !isOk;}



    public  void show()
    {
        builder.show();
    }

    public AlertDialog setAction(String actionName,final DialogInterface.OnClickListener listener)
    {
        builder.setPositiveButton(actionName,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onClick(dialogInterface,i);
            }
        });
        return this;
    }
    public   AlertDialog setContext(String _title,String body,@DrawableRes int iconId)
    {
        builder.setTitle(_title);
        builder.setMessage(body);
        builder.setIcon(iconId);
        builder.setCancelable(true);
        return this;
    }

    public void Show(String _title,String _message,String buttonName1,String buttonName2)
    {

        setContext(_title,_message, R.drawable.ic_baseline_access_time_24);

        if(buttonName1!="")
        {
            builder.setPositiveButton(buttonName1,  new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    if(context!=null)
                        context.onAlertClickOK(flag);

                   isOk=true;
                    Toast.makeText(_Activity,"yes",Toast.LENGTH_SHORT).show();
                }

            });
        }

        if(buttonName2!="")
        {
            builder.setNegativeButton(buttonName2,  new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    if(context!=null)
                        context.onAlertClickCancel(flag);

                    isOk=false;
                    Toast.makeText(_Activity,"No",Toast.LENGTH_SHORT).show();
                }

            });
        }


        builder.show();

//        return this;
    }

    public EditText  setEditText(String placeholder,int id)
    {

        // Create an EditText object
        EditText editText = new EditText(_Activity);
        editText.setId(id);
        // Set the layout parameters
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        // Customize the EditText appearance and behavior
        editText.setHint(placeholder);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Set the EditText as the view of the AlertDialog.Builder
        builder.setView(editText);

        // Create and show the AlertDialog
        //androidx.appcompat.app.AlertDialog dialog = builder.create();
//        dialog.show();

        return  editText;
    }



}
