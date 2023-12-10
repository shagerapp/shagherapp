package com.example.parking.Helpers;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.fragment.app.FragmentActivity;

import com.example.parking.R;

public class DialogBox {

        public LinearLayout dialogBox;
        public TextView dialogMessage,dialogTitle;
        public Button BtnOk,BtnCancel;
        private ComponentActivity ActivityClass;

        public void setVisible()
        {

              dialogBox.setVisibility(View.VISIBLE);

        }

        public void setInVisible()
        {
            dialogBox.setVisibility(View.INVISIBLE);
        }
        public DialogBox(FragmentActivity activity)
        {

            ActivityClass=(ComponentActivity)activity;
            Set_R_Id(R.id.DialogBox,R.id.DialogBoxTitle,R.id.DialogBoxMessage,R.id.DialogBoxBtnOk,R.id.DialogBoxBtnCancel);

        }

            public void Set_R_Id(int Box_R_Id,int Title_R_Id,int Msg_R_Id,int BtnOk_R_Id,int BtnCancel_R_Id)
        {

            dialogBox = (LinearLayout) ActivityClass.findViewById(Box_R_Id);
            dialogTitle = (TextView) ActivityClass.findViewById(Title_R_Id);
            dialogMessage = (TextView) ActivityClass.findViewById(Msg_R_Id);
            BtnOk = (Button) ActivityClass.findViewById(BtnOk_R_Id);
            BtnCancel = (Button) ActivityClass.findViewById(BtnCancel_R_Id);

        }

    public DialogBox SetDataText(String title,String message)
    {
        if(!title.equals(""))
            dialogTitle.setText(title);

        if(!message.equals(""))
            dialogMessage.setText(message);

        return this;
    }

}
