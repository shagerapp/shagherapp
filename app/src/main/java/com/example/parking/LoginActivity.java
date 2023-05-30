package com.example.parking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parking.Models.Account;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginActivity extends AppCompatActivity {


    private Button btnLogIn,btnRegister;
    private TextView Vphone_rror,VPass_error;
    private  String _phone,_password;
    private EditText phone,password;
    Handler mainHandeler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        Firebase.InitFirebase();

        btnLogIn= (Button) findViewById(R.id.B1);
        btnRegister= (Button) findViewById(R.id.B2);
        btnRegister.setPaintFlags(btnRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        phone= (EditText) findViewById(R.id.E1);
        password= (EditText) findViewById(R.id.E2);
        Vphone_rror= (TextView) findViewById(R.id.V1_error);
        VPass_error= (TextView) findViewById(R.id.V2_error);


//        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                EditText item=(EditText)view;
//                String value=String.valueOf(item.getText()).trim();
//                if(!value.isEmpty())
//                {
//                    if (!b)
//                    {
//                        if (!VerifyInputs.VerifyEmail(value))
//                            Vphone_rror.setText(VerifyInputs.EmailError);
//                        else
//                            Vphone_rror.setText(" ");
//                    }
//                    else
//                    {
//                        Vphone_rror.setText("");
//                    }
//
//                }
//
//            }} );

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                EditText item=(EditText)view;
                String value=String.valueOf(item.getText()).trim();
                if(!value.isEmpty())
                {
                    if (!b)
                    {

                        if (!VerifyInputs.VerifyPassword(value))
                            VPass_error.setText(VerifyInputs.PasswordError);
                        else
                            VPass_error.setText(" ");
                    }
                    else
                    {
                        VPass_error.setText("");
                    }

                }

            }} );

        btnLogIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {


                mainHandeler.post(new Runnable() {
                    @Override
                    public void run() {


                        _phone = String.valueOf(phone.getText()).trim();
                        _password = String.valueOf(password.getText()).trim();

                        if(!_phone.isEmpty() && !_password.isEmpty())
                        {



                            if(!VerifyInputs.VerifyPassword(_password))
                                Vphone_rror.setText(VerifyInputs.PasswordError);
                            else
                            {
                                Firebase.col_account
                                        .whereEqualTo("phoneNum",_phone)
                                        .whereEqualTo("pass",_password)
                                        .get()
                                        .addOnCompleteListener(task->{

                                                    if(task.isSuccessful())
                                                    {
                                                         //for(QueryDocumentSnapshot document: task.getResult())
                                                        if(task.getResult().isEmpty())
                                                        {
                                                            Toast.makeText(LoginActivity.this, "the user name or password is wrrong !!", Toast.LENGTH_SHORT).show();

                                                        }
                                                        else
                                                        {

                                                            new AlertDialog(LoginActivity.this).Show("", "تم التسجيل بنجاح !", "نعم", "");
                                                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);
                                                        }
                                                    }

                                                }
                                        );
                            }
                        }
                        else
                        {
                            new AlertDialog(LoginActivity.this).Show("خـــطــاء", "تأكد من صحة البيانات المدخلة لايمكن ترك احد الحقول فارغة !", "نعم", "");
                        }
                    }
                });



            }
        });

        // Register
        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

    }
}