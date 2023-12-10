package com.example.parking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parking.ApiClient.Controllers.UserController;
import com.example.parking.ApiClient.models.SignUpRequest;
import com.example.parking.Enum.Roles;
import com.example.parking.Helpers.AlertDialog;
import com.example.parking.Helpers.Helper;
import com.example.parking.Helpers.IAlertDialog;
import com.example.parking.Helpers.VerifyInputs;
import com.example.parking.Models.TablesName;
import com.example.parking.Models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;


import java.util.*;

public class RegisterActivity extends AppCompatActivity implements IAlertDialog {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private Button btnLogIn,btnRegister;
    private ProgressBar progressBar;
    private TextView Vpass_error,VConfirmPass_error;
    public static final String TAG = "RegisterActivity";
    private  EditText fname,lname,email,carId,phone,pass,rpass;
    Handler mainHandeler=new Handler();
    private FirebaseUser firebaseUser;


    LinearLayout alert_back;
    TextView  alert_title,alert_body;
    Button alert_btn_ok,alert_btn_close;
    TextView alert_btn_cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance();
        FirebaseApp.initializeApp(this);



        btnLogIn = (Button) findViewById(R.id.BtnLogin);
        btnRegister = (Button) findViewById(R.id.BtnRegister);

        fname = (EditText) findViewById(R.id.Fname);
        lname = (EditText) findViewById(R.id.Lname);
        phone = (EditText) findViewById(R.id.Phone);
        email = (EditText) findViewById(R.id.Email);
        carId = (EditText) findViewById(R.id.CarId);
        pass = (EditText) findViewById(R.id.Pass);
        rpass = (EditText) findViewById(R.id.Rpass);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Vpass_error = (TextView) findViewById(R.id.VPass_error);
        VConfirmPass_error = (TextView) findViewById(R.id.VRpass_error);



        btnRegister.setOnClickListener(view->onClickRegister(view));

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
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //  onRestart();
        }
    }



    @SuppressLint("WrongViewCast")
    private  void InitializationAlert(){

        alert_back=findViewById(R.id.background_alert);

        @SuppressLint("ResourceType")
        Animation animation = AnimationUtils.loadAnimation(RegisterActivity.this, R.animator.animation_dialog);
        alert_back.startAnimation(animation);
        alert_back.setVisibility(View.VISIBLE);

        alert_title=findViewById(R.id.alert_title);
        alert_body= findViewById(R.id.alert_message);
        alert_btn_ok= findViewById(R.id.alert_btn_ok);
        alert_btn_cancel= findViewById(R.id.alert_btn_cancel);
        alert_btn_close= findViewById(R.id.alert_btn_close);
        Button btnResendEmail= findViewById(R.id.BtnResendEmail);
        btnResendEmail.setVisibility(View.VISIBLE);

        alert_title.setText("نأكيد أنشاء الحساب");
        alert_body.setText("تم ارسال رابط التحقق عبر البريد الالكتروني");

        alert_btn_close.setOnClickListener(v->alert_back.setVisibility(View.GONE));
        btnResendEmail.setOnClickListener(v-> {
            if(firebaseUser!=null)
                firebaseUser.sendEmailVerification();
        });
        alert_btn_ok.setOnClickListener(v-> {

            alert_back.setVisibility(View.GONE);
            moveToLoginPage();
        });
        alert_btn_cancel.setOnClickListener(v->{
            alert_back.setVisibility(View.GONE);
        });
;
    }

    private  boolean hiddenKeyboard(View view, int actionId, KeyEvent event)
    {


        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(((EditText)view).getWindowToken(), 0);
            return true;
        }
        return false;
    }



    private  void onClickRegister(View v)
    {
        String  fnametxt = String.valueOf(fname.getText()).trim();
        String  lnametxt = String.valueOf(lname.getText()).trim();
        String  phonetxt = String.valueOf(phone.getText()).trim();
        String  emailtxt = String.valueOf(email.getText()).trim();
        String  carIdtxt = String.valueOf(carId.getText()).trim();
        String  passtxt  = String.valueOf(pass.getText()).trim();
        String  rpasstxt = String.valueOf(rpass.getText()).trim();

        if (CheckInputs(fnametxt,lnametxt,emailtxt,phonetxt,carIdtxt,passtxt,rpasstxt))
        {
            progressBar.setVisibility(View.VISIBLE);
            register(fnametxt,lnametxt,emailtxt,phonetxt,carIdtxt,passtxt);
        }
//        else
//        {
//            new AlertDialog(RegisterActivity.this).Show("لايمكنك ترك اي حقول فارغة !!", "error", "Yes", "");
//        }

    }

    private void register(String fname,String lname,String emailtxt,String phone,String carId,String password) {
        mAuth.createUserWithEmailAndPassword(emailtxt,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                             firebaseUser = mAuth.getCurrentUser();
                            firebaseUser.sendEmailVerification();


                            String userId=firebaseUser.getUid();
                            UserProfileChangeRequest profileUser= new UserProfileChangeRequest.Builder().setDisplayName(fname+" "+lname)
                                    .setPhotoUri(null).build();
                            firebaseUser.updateProfile(profileUser);
                            if(userId!=null && !userId.isEmpty())
                            {

                                UserDetails user=new UserDetails();
                                user.setPhone(phone);
                                user.setRole(Roles.USER.getValue());
                                List<String> carPlate= new ArrayList<String>();
                                carPlate.add(carId);
                                user.setCarNumbers(carPlate);
                                createUserProfile(firebaseUser,userId,user);
                                FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(new OnCompleteListener<String>() {
                                            @Override
                                            public void onComplete(@NonNull Task<String> task) {
                                                if (task.isSuccessful())
                                                {
                                                    // create user Role
                                                    SignUpRequest sign=new SignUpRequest();
                                                    sign.setUserId(userId);
                                                    sign.setRole(Roles.USER.getValue());

                                                    String token = task.getResult();
                                                    if(FirebaseAuth.getInstance()!=null) {
                                                        sign.setToken(token);
                                                        Log.d("MessagingToken", token);
                                                    }
                                                    new UserController().signUp(sign, RegisterActivity.this);
                                                    // Handle the token here
                                                } else {
                                                    // Handle the error
                                                }
                                            }
                                        });

                                InitializationAlert();
                            }
                        }
                        else {
                            try
                            {
                                throw  task.getException();
                            } catch (FirebaseAuthWeakPasswordException e)
                            {
                                new AlertDialog(RegisterActivity.this).Show(VerifyInputs.PasswordError, "Error", "Yes", "");
                                Helper.setEditTextError(pass,VerifyInputs.PasswordError);
                            } catch (FirebaseAuthInvalidCredentialsException e)
                            {
                                new AlertDialog(RegisterActivity.this).Show("! The email is invalid or used by someone else", "Error", "Yes", "");
                                Helper.setEditTextError(email,"! The email is invalid or used by someone else");
                            } catch (FirebaseAuthUserCollisionException e)
                            {
                                new AlertDialog(RegisterActivity.this).Show("User already exists. Use another email address! ", "Error", "Yes", "");
                                Helper.setEditTextError(email,"User already exists. Use another email address! " );
                            } catch (Exception e)
                            {
                                Log.w(TAG, e.getMessage());
                                Toast.makeText(RegisterActivity.this, e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }






    private  void ResendEmailVerification()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email verification link sent.");
                            }
                        }
                    });
        }


    }
    private void createUserProfile(FirebaseUser firebaseUser, String userId, UserDetails user)
    {
        DatabaseReference database = mDatabase.getReference();
        database.child(TablesName.Profile).child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {

                progressBar.setVisibility(View.GONE);

                if(task.isSuccessful())
                {

                    clearInputs();
                    new AlertDialog(RegisterActivity.this).Show( "Notifiy !!","A confirmation link has been sent via email. Please click on the link to complete the registration process, then log in to the account", "Yes", "");
                }
                else
                {

                    new AlertDialog(RegisterActivity.this).Show("Registration was not successful, please try again !!", "Error", "Yes", "");
                }

            }
        });

    }


    private boolean CheckInputs(String fnametxt,String lnametxt,String emailtxt,String phonetxt,
                              String carIdtxt,String passtxt,String rpasstxt) {
        int countError = 0;
        if (fnametxt.isEmpty())
        {
            countError++;
            Helper.setEditTextError(fname, VerifyInputs.inputEmpty);
        }
        if (lnametxt.isEmpty())
        {
            countError++;
            Helper.setEditTextError(lname, VerifyInputs.inputEmpty);
        }
        if (emailtxt.isEmpty())
        {
            countError++;
            Helper.setEditTextError(email, VerifyInputs.inputEmpty);
         }
        else if (!VerifyInputs.VerifyEmail(emailtxt)) {
            countError++;
            Helper.setEditTextError(email, VerifyInputs.EmailError);
        }

        if (carIdtxt.isEmpty())
        {
            countError++;
            Helper.setEditTextError(carId, VerifyInputs.inputEmpty);
        }
        else if (!VerifyInputs.VerifyCarPlate(carIdtxt)) {
            countError++;
            Helper.setEditTextError(carId, VerifyInputs.CarPlateError);
        }

        if (phonetxt.isEmpty())
        {
            countError++;
            Helper.setEditTextError(phone, VerifyInputs.inputEmpty);
        }
        else if(!VerifyInputs.VerifyPhoneNumber(phonetxt)) {
            countError++;
            Helper.setEditTextError(phone, VerifyInputs.PhoneError);
        }


        if (passtxt.isEmpty()) {
            countError++;
            Helper.setEditTextError(pass, VerifyInputs.inputEmpty);
        }
        else if(!VerifyInputs.VerifyPassword(passtxt)) {
            countError++;
            Helper.setEditTextError(pass, VerifyInputs.PasswordError);
        }

        if (rpasstxt.isEmpty()) {
            countError++;
            Helper.setEditTextError(rpass, VerifyInputs.inputEmpty);
        }
        else if(!VerifyInputs.VerifyPassword(passtxt)) {
            countError++;
            Helper.setEditTextError(rpass, VerifyInputs.PasswordError);
        }
        else if(!passtxt.equalsIgnoreCase(rpasstxt)) {
            countError++;
            Helper.setEditTextError(rpass, VerifyInputs.ConfirmPasswordError);
        }

        return  countError==0;
    }


    private void clearInputs()
    {
        fname.setText(null);
        lname.setText(null);
        email.setText(null);
        phone.setText(null);
        carId.setText(null);
        pass.setText(null);
        rpass.setText(null);
    }


    @Override
    public void onAlertClickOK(Object value)
    {
        moveToLoginPage();
    }

    private  void moveToLoginPage()
    {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onAlertClickCancel(Object value) {

    }
}
