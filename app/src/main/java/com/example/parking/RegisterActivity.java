package com.example.parking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parking.Models.Account;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.auth.User;

public class RegisterActivity extends AppCompatActivity {


    private Button btnLogIn,btnRegister;
    private TextView VPass1_error,VConfirmPass_error;
    private  String fnametxt,lnametxt,phonetxt,pass1txt,pass2txt;
    private  EditText fname,lname,phone,pass1,pass2;
    Handler mainHandeler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(this);
        Firebase.InitFirebase();


        btnLogIn= (Button) findViewById(R.id.BtnLogin);
        btnRegister= (Button) findViewById(R.id.BtnRegister);

        fname= (EditText) findViewById(R.id.Fname);
        lname= (EditText) findViewById(R.id.Lname);
        phone= (EditText) findViewById(R.id.Phone);
        pass1= (EditText) findViewById(R.id.Pass1);
        pass2= (EditText) findViewById(R.id.Pass2);
        VPass1_error=(TextView) findViewById(R.id.VPass1_error);
        VConfirmPass_error=(TextView) findViewById(R.id.VPass2_error);

        pass1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                EditText item=(EditText)view;
                String value=String.valueOf(item.getText()).trim();
                if(!value.isEmpty())
                {
                    if (!b)
                    {

                        if (!VerifyInputs.VerifyPassword(value))
                            VPass1_error.setText(VerifyInputs.PasswordError);
                        else
                            VPass1_error.setText("");
                    }
                    else
                    {
                        VPass1_error.setText("");
                    }

                }

            }} );
        pass2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                EditText item=(EditText)view;
                String value=String.valueOf(item.getText()).trim();
                if(!value.isEmpty())
                {
                    if (!b)
                    {

                        if (!value.equalsIgnoreCase(String.valueOf(pass1.getText()).trim()))
                            VConfirmPass_error.setText(VerifyInputs.ConfirmPasswordError);
                        else
                            VConfirmPass_error.setText(" ");
                    }
                    else
                    {
                        VConfirmPass_error.setText("");
                    }

                }

            }} );


        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {


                mainHandeler.post(new Runnable() {
                    @Override
                    public void run() {

                        fnametxt = String.valueOf(fname.getText()).trim();
                        lnametxt = String.valueOf(lname.getText()).trim();
                        phonetxt = String.valueOf(phone.getText()).trim();
                        pass1txt = String.valueOf(pass1.getText()).trim();
                        pass2txt = String.valueOf(pass2.getText()).trim();


                        if (CheckInputs())
                        {
                            if (pass1txt.equalsIgnoreCase(pass2txt))
                            {
                                if(!VerifyInputs.VerifyPassword(pass1txt))
                                    VPass1_error.setText(VerifyInputs.PasswordError);
                                else
                                {
                                    Firebase.col_account
                                            .whereEqualTo("phoneNum",phonetxt)
                                            .get()
                                            .addOnCompleteListener(task->{

                                                        if(task.isSuccessful())
                                                        {
                                                            if(task.getResult().isEmpty())
                                                            {
                                                                Account account=new Account(fnametxt,lnametxt,phonetxt,pass1txt);
                                                                Firebase.col_account.add(account);

                                                                new AlertDialog(RegisterActivity.this).Show("", "تم التسجيل بنجاح !","نعم","");

                                                                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);


                                                            }
                                                            else
                                                            {
                                                                Toast.makeText(RegisterActivity.this, "the phone number is not a valide", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                    }
                                            );
                                }

                            }
                            else
                                new AlertDialog(RegisterActivity.this).Show("  password is not matched !!", "error", "نعم", "");
                        } else
                        {
                            new AlertDialog(RegisterActivity.this).Show("لايمكنك ترك اي حقول فارغة !!", "error", "نعم", "");
                        }
                    }


                });



            }
        });

        // btnLogIn
        btnLogIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });


    }

    private boolean CheckInputs()
    {
        return  (!fnametxt.isEmpty() && !lnametxt.isEmpty()  && !phonetxt.isEmpty() && !pass1txt.isEmpty());
    }






}
