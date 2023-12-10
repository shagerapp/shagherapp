package com.example.parking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.parking.Enum.LayoutOption;
import com.example.parking.Helpers.AlertDialog;
import com.example.parking.Helpers.App;
import com.example.parking.Helpers.Helper;
import com.example.parking.Helpers.IAlertDialog;
import com.example.parking.Helpers.VerifyInputs;
import com.example.parking.Models.TablesName;
import com.example.parking.Models.UserDetails;
import com.example.parking.databinding.FragmentSettingsFragmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link settings_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class settings_fragment extends Fragment implements IAlertDialog {



    LinearLayout alert_back;
    TextView alert_title,alert_body;
    Button alert_btn_ok,alert_btn_cancel,alert_btn_close;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private  FragmentSettingsFragmentBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser firebaseUser;
    private  int R_Id_codeText=112;
    private  String verificationId;
    private  EditText verificationEditText;
    private  PhoneAuthProvider.ForceResendingToken phoneAuthToken;
    Handler handler;

    public settings_fragment() {
        // Required empty public constructor

        handler=new Handler();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment settings_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static settings_fragment newInstance(String param1, String param2) {
        settings_fragment fragment = new settings_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        settings_fragment.this.getActivity().setTitle(R.string.privacy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentSettingsFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //here data must be an instance of the class MarsDataProvider
        return view;

    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseApp.initializeApp(settings_fragment.this.getContext());


        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        isDisplay(false);

        binding.progressBar.setVisibility(View.GONE);

        binding.btnActiveSettingUpdate.setOnClickListener(v->isDisplay(true));
        binding.btnUpdateEmail.setOnClickListener(v->onClickUpdateEmail());
        binding.btnUpdatePhone.setOnClickListener(v->onClickUpdatePhoneNumber());
        binding.btnUpdatePass.setOnClickListener(v->onClickUpdatePassword());
        binding.btnBack.setOnClickListener(v->onBack());

//        binding.btnUpdatePhone.setOnClickListener(v->updateEmail());
//        binding.btnUpdatePass.setOnClickListener(v->updateEmail());

        if(firebaseUser!=null)
        {
            binding.updateEmail.setText(firebaseUser.getEmail());
            getPhoneNumber(firebaseUser.getUid());
        }


    }

    private void onBack() {
        Intent intent = new Intent(this.getActivity(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private  void InitializationAlert(){

        alert_back=this.getActivity().findViewById(R.id.background_alert);


        @SuppressLint("ResourceType")
        Animation animation = AnimationUtils.loadAnimation(this.getActivity(), R.animator.animation_dialog);
        alert_back.startAnimation(animation);
        alert_back.setVisibility(View.VISIBLE);

        alert_title=this.getActivity().findViewById(R.id.alert_title);
        alert_body= this.getActivity().findViewById(R.id.alert_message);
        alert_btn_ok= this.getActivity().findViewById(R.id.alert_btn_ok);
        alert_btn_cancel= this.getActivity().findViewById(R.id.alert_btn_cancel);;
        alert_btn_close= this.getActivity().findViewById(R.id.alert_btn_close);;


        alert_title.setText("التحقق من البريد الالكتروني");
        alert_body.setText("تم ارسال رابط التحقق من البريد الالكتروني الرجاء النقر على الرابط لتأكيد العملية");

        alert_btn_close.setOnClickListener(v->
        {


            alert_back.setVisibility(View.GONE);
            FirebaseAuth.getInstance().signOut();

        });
        alert_btn_ok.setOnClickListener(v-> {

            alert_back.setVisibility(View.GONE);
            FirebaseAuth.getInstance().signOut();
            moveToLoginPage();
        });
        alert_btn_cancel.setOnClickListener(v->{

            FirebaseAuth.getInstance().signOut();
            alert_back.setVisibility(View.GONE);
        });
        ;
    }


    private  void moveToLoginPage()
    {
        Intent intent = new Intent(this.getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private void getPhoneNumber(String userId)
    {
        mDatabase.getReference(TablesName.Profile).child(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful())
                        {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else
                        {
                            try {
                                if (task.getResult().getValue() != null) {

                                    UserDetails details = task.getResult().getValue(UserDetails.class);
                                    if (details != null) {
                                        Log.d("phoneNumber", details.getPhone(), task.getException());
                                        binding.progressBar.setVisibility(View.GONE);
                                        binding.updatePhone.setText(details.getPhone());
                                    }

                                }
                            }catch (Exception ex)
                            {
                                Log.e("Error",ex.getMessage());
                            }

                        }
                    }
                });
    }



    @Override
    public void onAlertClickOK(Object value) {

        if(value!=null)
        {
            switch ((LayoutOption)value)
            {
                case EDIT:
                    break;

                case LOGOUT:
                    App.logOut(settings_fragment.this.getActivity());
                    break;

                case RELOAD:
                    isDisplay(false);
                    break;

            }
        }

    }

    @Override
    public void onAlertClickCancel(Object value)
    {

    }


    private void onClickUpdateEmail()
    {
        String passtxt=binding.emailPassword.getText().toString();
        String emailtxt=binding.updateEmail.getText().toString();
        boolean isError=false;

        if (isError=emailtxt.isEmpty())
            Helper.setEditTextError(binding.updateEmail, VerifyInputs.inputEmpty);
        else if ((isError=!VerifyInputs.VerifyEmail(emailtxt)))
            Helper.setEditTextError(binding.updateEmail, VerifyInputs.EmailError);

        if (isError=passtxt.isEmpty())
            Helper.setEditTextError(binding.emailPassword, VerifyInputs.inputEmpty);
        else if((isError=!VerifyInputs.VerifyPassword(passtxt)))
            Helper.setEditTextError(binding.emailPassword, VerifyInputs.PasswordError);


        if(!isError)
        {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),passtxt ); // Current Login Credentials \\
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Log.d("", "User re-authenticated.");
                                //Now change your email address \\
                                //----------------Code for Changing Email Address----------\\
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                if(emailtxt.equals(user.getEmail())) {
                                    new AlertDialog(settings_fragment.this.getActivity(),settings_fragment.this, LayoutOption.LOGOUT).Show("Notifiy !!","البريد الالكتروني المدخل هو نفس البريد المستخدم حاليا ادخل بريد الكتروني اخر من فضلك !!", "yes", "");
                                }
                                else
                                {

                                    if (user != null)
                                    {
                                        user.verifyBeforeUpdateEmail(emailtxt)
                                                .addOnCompleteListener(settings_fragment.this.getActivity(), new OnCompleteListener<Void>() {
                                                    @Override
                                                    public synchronized  void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                        {
                                                            new AlertDialog(settings_fragment.this.getActivity()).Show("تعديل البريد الالكتروني !!", "تم  ارسال رابط تحقق عبر البريد الالكتروني قم بالنقر على الرابط لتحقق من صحة البريد واكمال العملية   ", "yes", "");
                                                            // Email verification link sent successfully
                                                            updateEmail(emailtxt);



                                                            InitializationAlert();




                                                            //user.reload();
                                                            //new taskCheckEmailVerify(user).start();
                                                        }
                                                        else
                                                        {
                                                            // Email verification link couldn't be sent
                                                            Toast.makeText(settings_fragment.this.getActivity(), "Failed to send verification email.", Toast.LENGTH_LONG

                                                            ).show();
                                                        }
                                                    }
                                                });
                                    }

                                }
                            }
                            else
                                new AlertDialog(settings_fragment.this.getActivity()).Show( "اشعار!!"," كلمة السر غير صالحة ", "yes", "");
                            //----------------------------------------------------------\\
                        }
                    });
        }

    }
    private void onClickUpdatePassword()
    {
        String passtxt=binding.updatePass.getText().toString();
        String newPasstxt=binding.updateNewPass.getText().toString();
        boolean isError=false;


        if (isError=passtxt.isEmpty())
            Helper.setEditTextError(binding.updatePass, VerifyInputs.inputEmpty);
        else if((isError=!VerifyInputs.VerifyPassword(passtxt)))
            Helper.setEditTextError(binding.updatePass, VerifyInputs.PasswordError);

        if (isError=newPasstxt.isEmpty())
            Helper.setEditTextError(binding.updateNewPass, VerifyInputs.inputEmpty);
        else if ((isError=!VerifyInputs.VerifyPassword(newPasstxt)))
            Helper.setEditTextError(binding.updateNewPass, VerifyInputs.PasswordError);




        if(!isError && !newPasstxt.equals(passtxt)) {


            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // Get auth credentials from the user for re-authentication
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),passtxt); // Current Login Credentials \\
            // Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                firebaseUser.updatePassword(newPasstxt)
                                        .addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    new AlertDialog(settings_fragment.this.getActivity(),settings_fragment.this,LayoutOption.LOGOUT).Show("Notify !!", "Modified. Log in", "yes", "");

                                                }
                                                else
                                                {
                                                    new AlertDialog(settings_fragment.this.getActivity()).Show("Error !!", " Valid", "yes", "");
                                                }
                                            }
                                        });
                            }
                        }});

        }

    }

    private void onClickUpdatePhoneNumber()
    {
        String passtxt=binding.phonePassword.getText().toString();
        String phonetxt=binding.updatePhone.getText().toString();
        boolean isError=false;

        if (isError=phonetxt.isEmpty())
            Helper.setEditTextError(binding.updatePhone, VerifyInputs.inputEmpty);
        else if ((isError=!VerifyInputs.VerifyPhoneNumber(phonetxt)))
            Helper.setEditTextError(binding.updatePhone, VerifyInputs.PhoneError);

        if (isError=passtxt.isEmpty())
            Helper.setEditTextError(binding.phonePassword, VerifyInputs.inputEmpty);
        else if((isError=!VerifyInputs.VerifyPassword(passtxt)))
            Helper.setEditTextError(binding.phonePassword, VerifyInputs.PasswordError);


        if(!isError)
        {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // Get auth credentials from the user for re-authentication
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),passtxt ); // Current Login Credentials \\
            // Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful())
                               {
                                   DatabaseReference database = mDatabase.getReference();
                                   database.child(TablesName.Profile).child(firebaseUser.getUid()).child("phone").setValue(phonetxt).addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@androidx.annotation.NonNull Task<Void> task) {

                                           if (task.isSuccessful()) {
                                               new AlertDialog(settings_fragment.this.getActivity(),settings_fragment.this,LayoutOption.RELOAD).Show("Notify !!", "تم التعديل بنجاح", "yes", "");
                                           }
                                           else
                                           {
                                               new AlertDialog(settings_fragment.this.getActivity()).Show("Notify !!", " ! فشـــل ", "yes", "");
                                           }
                                       }
                                   });

                               }
                                else {

                               }
                           }
                       });



        }


//
//
//// Reauthenticate the user with the current phone number credential
////                            firebaseUser.reauthenticate(credential)
////                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
////                                        @Override
////                                        public void onComplete(@NonNull Task<Void> task) {
////                                            if (task.isSuccessful()) {
////                                                // Update the phone number with the new credential
////                                                firebaseUser.updatePhoneNumber(updatedCredential)
////                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                            @Override
////                                                            public void onComplete(@NonNull Task<Void> task) {
////                                                                if (task.isSuccessful()) {
////                                                                    // Phone number updated successfully
////                                                                    // You can perform any additional actions here
////                                                                } else {
////                                                                    // Failed to update phone number
////                                                                    // Handle the error
////                                                                }
////                                                            }
////                                                        });
////                                            } else {
////                                                // Failed to reauthenticate with the current phone number credential
////                                                // Handle the error
////                                            }
////                                        }
////                                    });
//
//
//        UserProfileChangeRequest profileUser= new UserProfileChangeRequest.Builder().setDisplayName(fname+" "+lname).setPhotoUri(null).build();
//
//        firebaseUser.updateProfile(profileUser);
//        // Update the phone number with the new credential
//        firebaseUser.updatePhoneNumber(updatedCredential);

    }




    private void updateEmail(String newEmail) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.updateEmail(newEmail).addOnCompleteListener(settings_fragment.this.getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                // Email updated successfully
                               Toast.makeText(settings_fragment.this.getActivity(), "Email updated.", Toast.LENGTH_LONG).show();

                                App.logOut(settings_fragment.this.getActivity());
                            }
                            else
                            {
                                // Email update failed
                                Toast.makeText(settings_fragment.this.getActivity(), "Failed to update email.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            // User is not authenticated
            Toast.makeText(settings_fragment.this.getActivity(), "User not authenticated.", Toast.LENGTH_LONG).show();
        }
    }
    private void isDisplay(boolean value)
    {
        Helper.setReadOnly(binding.updateEmail,value);
        Helper.setReadOnly(binding.updatePhone,value);

        final int flag=(!value)?View.GONE:View.VISIBLE;
        binding.legendPhone.setVisibility(flag);
        binding.legendEmail.setVisibility(flag);
        binding.legendPass.setVisibility(flag);
        binding.layoutPass.setVisibility(flag);

        binding.emailPassword.setVisibility(flag);
        binding.phonePassword.setVisibility(flag);

        binding.btnActiveSettingUpdate.setVisibility((value)?View.GONE:View.VISIBLE);

        binding.btnUpdateEmail.setVisibility(flag);
        binding.btnUpdatePhone.setVisibility(flag);
        binding.btnUpdatePass.setVisibility(flag);


//        RecaptchaVerifier

    }

   private void updatePhoneNumber(@NonNull String verificationId,PhoneAuthProvider.ForceResendingToken token)
   {
       // The verification code has been sent to the user's phone.
       // Prompt the user to enter the verification code.
       // Get SMS code from user
       // Create credential

       if(verificationEditText!=null && !verificationEditText.getText().toString().trim().isEmpty()) {

           String smsCode=verificationEditText.getText().toString().trim();

           PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, smsCode);
           // Sign in user
           FirebaseAuth.getInstance().signInWithCredential(credential)
                   .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if (task.isSuccessful()) {
                               // User signed in successfully
                               // Update phone number
                               FirebaseAuth.getInstance().getCurrentUser().updatePhoneNumber(credential)
                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()) {

                                                   new AlertDialog(settings_fragment.this.getActivity()).Show(" Notify !!", "  Phone number updated successfully  ", "yes", "");
                                                   // Phone number updated successfully
                                                   // Update database document
//                                                       FirebaseFirestore.getInstance().collection("users")
//                                                               .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                                               .update("phoneNumber", "+15551234567");
                                               } else {

                                                   new AlertDialog(settings_fragment.this.getActivity()).Show(" Notify !!", "  Phone number update failed  ", "yes", "");
                                                   // Phone number update failed
                                               }
                                           }
                                       });
                           } else {
                               // User sign in failed
                           }
                       }
                   });
       }
       else
           new AlertDialog(settings_fragment.this.getActivity()).Show(" Notify !!", "You must enter the verification code sent to the new phone number", "yes", "");

   }
   private void VerificationPhoneNumber(String newPhoneNumber)
   {
       newPhoneNumber="+967 736576887";
       // Get the current user.
       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Create a PhoneAuthProvider object.
       PhoneAuthProvider phoneAuthProvider = PhoneAuthProvider.getInstance();

        // Set up a PhoneAuthProvider callback to listen for the verification code.
            PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new
           PhoneAuthProvider.OnVerificationStateChangedCallbacks()
           {
               @Override
               public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                   // The phone number has been verified.
                   user.updatePhoneNumber(credential).addOnCompleteListener(task -> {
                       if (task.isSuccessful())
                       {
                           // The phone number has been updated successfully.
                           new AlertDialog(settings_fragment.this.getActivity()).Show("Update phone Number !!", "Modified successfully  ", "yes", "");
                       }
                       else
                       {
                           // An error occurred while updating the phone number.
                           new AlertDialog(settings_fragment.this.getActivity()).Show("Edit phone number !!", " Valid !! ", "yes", "");
                       }

                   });
               }

               @Override
               public void onVerificationFailed(@androidx.annotation.NonNull FirebaseException e) {

            }


           @Override
           public void onCodeSent(@NonNull String verificationId,PhoneAuthProvider.ForceResendingToken token) {

               settings_fragment.this.verificationId=verificationId;
               phoneAuthToken=token;
               AlertDialog dialog=new AlertDialog(settings_fragment.this.getActivity(),settings_fragment.this,"verificationCode");
               verificationEditText=dialog.setEditText("Enter the verification code",R_Id_codeText);
               dialog.Show("رمز التحقق"," Enter the verification code sent via phone number","Next","Cancel");

           }
       };



// Start the phone number verification process.
   phoneAuthProvider.verifyPhoneNumber(newPhoneNumber,60, TimeUnit.SECONDS,settings_fragment.this.getActivity(),callbacks);

   }



}