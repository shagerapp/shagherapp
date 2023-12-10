package com.example.parking;

import static androidx.constraintlayout.widget.StateSet.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parking.Helpers.AlertDialog;
import com.example.parking.Helpers.Helper;
import com.example.parking.Helpers.VerifyInputs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    private Button btnLogIn,btnRegister,btnForgetPassword;
    private ProgressBar progressBar;
    private TextView Vphone_rror,VPass_error;
    private  String _phone,_password;
    private EditText phone,password,email;
    Handler mainHandeler=new Handler();
    private LinearLayout reset_back;
    private EditText sendEmailText;
    private TextView reset_btn_send,reset_btn_cancel;
    private  Button btnResendEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        btnLogIn= (Button) findViewById(R.id.B1);
        btnRegister= (Button) findViewById(R.id.B2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if(btnRegister!=null)
             btnRegister.setPaintFlags(btnRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        email= (EditText) findViewById(R.id.E1);
        password= (EditText) findViewById(R.id.E2);
        Vphone_rror= (TextView) findViewById(R.id.V1_error);
        VPass_error= (TextView) findViewById(R.id.V2_error);
        btnForgetPassword= (Button) findViewById(R.id.btnForgetPassword);
        btnForgetPassword.setOnClickListener(v -> onForgetPassword());

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

                   onLogin();

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

    private void onForgetPassword() {


        reset_back=findViewById(R.id.back_resetPassword_dialog_box);
        @SuppressLint("ResourceType")
        Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.animator.animation_dialog);
        reset_back.startAnimation(animation);
        reset_back.setVisibility(View.VISIBLE);
        sendEmailText= findViewById(R.id.reset_pass_email);
        reset_btn_send= findViewById(R.id.btnSendEmail);
        reset_btn_cancel= findViewById(R.id.btnCancel);
        reset_btn_send.setOnClickListener(V->onResetPassword());
        reset_btn_cancel.setOnClickListener(V->reset_back.setVisibility(View.GONE));

    }

    private void onResetPassword() {

        progressBar.setVisibility(View.VISIBLE);
        String email=sendEmailText.getText().toString();
        if(email.isEmpty()) {
            Helper.setEditTextError(sendEmailText,VerifyInputs.inputEmpty);
            return;}
        if(!VerifyInputs.VerifyEmail(email)) {
            Helper.setEditTextError(sendEmailText, VerifyInputs.EmailError);
            return;}
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Password reset email sent successfully
                        new AlertDialog(LoginActivity.this).Show("Success", "Password reset email sent successfully", getString(R.string.btn_yes), "");
                        reset_back.setVisibility(View.GONE);
                        // You can show a success message to the user or handle it as desired
                    } else {
                        //
                        new AlertDialog(LoginActivity.this).Show("Error", "Error occurred while sending password reset email !!", getString(R.string.btn_yes), "");
                        // You can show an error message to the user or handle it as desired
                    }
                    progressBar.setVisibility(View.GONE);

                });

    }

    private  boolean checkInputs(String emailtxt,String passtxt)
    {
        int countError=0;
        if (emailtxt.isEmpty())
        {
            countError++;
            Helper.setEditTextError(email, VerifyInputs.inputEmpty);
        }
        else if (!VerifyInputs.VerifyEmail(emailtxt)) {
            Helper.setEditTextError(email, VerifyInputs.EmailError);
        }
        if (passtxt.isEmpty()) {
            countError++;
            Helper.setEditTextError(password, VerifyInputs.inputEmpty);
        }
        else if(!VerifyInputs.VerifyPassword(passtxt)) {
            countError++;
            Helper.setEditTextError(password, VerifyInputs.PasswordError);
        }
        return  countError==0;
    }

    void onLogin()
    {
        String emailtxt = String.valueOf(email.getText()).trim();
        String pswtxt = String.valueOf(password.getText()).trim();
        if(checkInputs(emailtxt,pswtxt)) {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(emailtxt, pswtxt).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser.isEmailVerified())
                        {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            new AlertDialog(LoginActivity.this).Show("notice ", "Please confirm your account by clicking on the verification link sent via email!! ", getString(R.string.btn_yes), "");
                        }
                    } else {

                        try
                        {
                            throw task.getException();

                        } catch (FirebaseAuthWeakPasswordException e) {
                            new AlertDialog(LoginActivity.this).Show(VerifyInputs.PasswordError, "Error", getString(R.string.btn_yes), "");
                            Helper.setEditTextError(password, VerifyInputs.PasswordError);
                        } catch (FirebaseAuthInvalidUserException e) {
                            new AlertDialog(LoginActivity.this).Show("Error", "! The user does not exist. Please create a new account", getString(R.string.btn_yes), "");
                            Helper.setEditTextError(email, "! Email is invalid ");
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            new AlertDialog(LoginActivity.this).Show("Error", "! Invalid user verify email", getString(R.string.btn_yes), "");
                            Helper.setEditTextError(email, "! Email is invalid ");
                        } catch (Exception e) {
                            Log.w(TAG, e.getMessage());
                            new AlertDialog(LoginActivity.this).Show("Error", "! The email or password is incorrect", getString(R.string.btn_yes), "");
                            Helper.setEditTextError(email, "! Email is invalid ");
                            Helper.setEditTextError(password, "! The password is incorrect ");
                            // Toast.makeText(getApplicationContext()غير صالح ");
                            // Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                }
            });

          }
        }

}