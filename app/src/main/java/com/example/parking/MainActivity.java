package com.example.parking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.parking.ApiClient.Authorize;
import com.example.parking.ApiClient.Controllers.BookingController;
import com.example.parking.ApiClient.Controllers.UserController;
import com.example.parking.ApiClient.IClaims;
import com.example.parking.Constant.AllConstant;
import com.example.parking.Enum.Options;
import com.example.parking.Helpers.DialogBox;
import com.example.parking.Helpers.ICallback;
import com.example.parking.Helpers.LocalStorage;
import com.example.parking.Models.RegisterationToken;
import com.example.parking.Models.Spot;
import com.example.parking.Permissions.AppPermissions;
import com.example.parking.Services.LocationTrackingService;
import com.example.parking.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ICallback, IClaims {


    public static long countTime = 0;
    private Handler handler;

    public static AppCompatActivity mainContext;
    private static boolean StartChecked = false;

    private DialogBox _DialogBox;
    private static CountDownTimer countDownTimer;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRefrence;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private ActionBarDrawerToggle drawerToggle;
    private FirebaseUser firebaseUser;
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private int JOB_ID=1;
    private boolean isLocationPermissionOk;
    private AppPermissions appPermissions;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


//        int requiredSdkVersion = Build.VERSION_CODES.LOLLIPOP; // تعيين الإصدار المطلوب هنا
//        int currentSdkVersion = Build.VERSION.SDK_INT;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(this, "Your device is not compatible with the application", Toast.LENGTH_SHORT).show();
            finish();
        }
        mainContext = MainActivity.this;
        handler=new Handler();
        drawerLayout=findViewById(R.id.draw_layout);
        navigationView=findViewById(R.id.navigationView);
        drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(item->onNavigationItemSelected(item));

        setTitle(R.string.home);

        FirebaseApp.initializeApp(MainActivity.this);
        appPermissions=new AppPermissions();

//        this.getMenuInflater().().getCustomView().setTextDirection(View.TEXT_DIRECTION_RTL);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
        else
        {
            Intent intent=getIntent();
            if(intent.hasExtra("fregment"))
            {
                String fregment = intent.getStringExtra("fregment");
                if(fregment.equals("showBooking"))
                    replaceFregment(new showBookingFragment());
            }
            else if(intent.hasExtra("spot") && intent.hasExtra("spotKey")) {
                String data = getIntent().getStringExtra("spot");
                Spot spot = new Gson().fromJson(data, Spot.class);
                String spotKey = getIntent().getStringExtra("spotKey");
                bookingFrame(spot,spotKey);
            }
            else
            {

                replaceFregment(new home_fragment());
                Initialization();
                InitializationEvents();
            }

        }

    }

    private void bookingFrame(Spot spot,String spotKey){

        if(spot==null || spotKey==null || spotKey.isEmpty())
            return;

        Bundle bundle = new Bundle();
        spot.setId(spotKey);
        bundle.putSerializable("spot",spot);

        // Create an instance of the target fragment
        BookingFragment sbookingFragment = new BookingFragment();
        sbookingFragment.setArguments(bundle);
        // Navigate to the target fragment
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, sbookingFragment);

        transaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();

//        new BookingController().checkIfUserHasBooking(this,AllConstant.CHECK_EXISTENCE_BOOKINGS);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.navigation_bar_item, menu);
        return true;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        checkExistenceBookings();

//        if(!Helper.isServiceRunning(LocationTrackingService.class,this))
//        {
//
//        }
//        else if(Helper.isServiceRunning(LocationTrackingService.class,this))
//        {
//            this.stopService(new Intent( this, LocationTrackingService.class)) ;
//        }
    }
    private void  checkExistenceBookings ()
    {
        if(LocalStorage.existing(this,"BookingId")){
            new BookingController().checkIfUserHasBooking(this,AllConstant.CHECK_EXISTENCE_BOOKINGS);
        }
    }
    @Override
    protected void onStop () {

        super .onStop() ;
//        new BookingController().checkIfUserHasBooking(this,AllConstant.CHECK_EXISTENCE_BOOKINGS);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(this, "onRequestPermissionsResult in Main ", Toast.LENGTH_SHORT).show();
        if (requestCode == AllConstant.LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]== PackageManager.PERMISSION_GRANTED ) {
                isLocationPermissionOk = true;

                 Intent intentService=new Intent(MainActivity.this, LocationTrackingService.class);
                 startForegroundService(intentService) ;
            } else {
                isLocationPermissionOk = false;
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void InitializationEvents() {

    }

    private void Initialization() {


        Authorize.Initialization(this);
        if(firebaseUser!=null)
        {
            TextView display_name= (TextView) binding.navigationView.getHeaderView(0).findViewById(R.id.displayName);
            TextView user_email= (TextView) binding.navigationView.getHeaderView(0).findViewById(R.id.userEmail);
            if(display_name!=null && user_email!=null)
            {
                if(!firebaseUser.getDisplayName().isEmpty() && !firebaseUser.getEmail().isEmpty())
                {
                    display_name.setText(firebaseUser.getDisplayName());
                    user_email.setText(firebaseUser.getEmail());
                }

            }

        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful())
                        {
                            String token = task.getResult();
                            if(FirebaseAuth.getInstance()!=null) {

                                RegisterationToken newToken = new RegisterationToken();
                                newToken.setUserId(FirebaseAuth.getInstance().getUid());
                                newToken.setToken(token);
                                new UserController().setToken(newToken,MainActivity.this);

                            }

                        }
                    }
                });




//        SignUpRequest sign=new SignUpRequest();
//        sign.setUserId(firebaseUser.getUid());
//        sign.setRole(Roles.ADMIN.getValue());
//        sign.setToken(Roles.ADMIN.getValue());
//        new UserController().setToken(sign, getApplicationContext());

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (drawerToggle != null && drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {



        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()){

            case R.id.home:
                replaceFregment(new home_fragment());
                break;
            case R.id.profile:
                replaceFregment(new profile_fragment());
                break;

            case R.id.privacy:
                replaceFregment(new settings_fragment());
                break;

            case R.id.logout:

                new android.app.AlertDialog.Builder(this)
                        .setTitle("Register")
                        .setMessage("Do you want to log out already?")
                        .setIcon(R.drawable.baseline_share_location_24)
                        .setPositiveButton("Ok",(d,w)-> onClickLogOut())
                        .create().show();
                break;

            case R.id.viewBooking:
                replaceFregment(new showBookingFragment());
                break;
            case R.id.statistics:
                replaceFregment(new StatisticsFragment());
                break;
            case R.id.googleMaps:
                displayDirectionGoogleMaps();

            case R.id.parkings:
                displayAllSpotsInGroup();
                break;
            case R.id.notifications:
                replaceFregment(new AdminNotifictionFragment());
                break;

            case R.id.parkingGroups:
                replaceFregment(new StatisticsGroupsFragment());
                break;

        }
        return false;
    }
    private void displayAllSpotsInGroup()
    {
            Bundle bundle = new Bundle();
            ParkingMapFragment spotsFragment = new ParkingMapFragment();
            spotsFragment.setArguments(bundle);
            replaceFregment(spotsFragment);
    }



    public void displayDirectionGoogleMaps()
    {
        Intent intent = new Intent(MainActivity.this,DirectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
    private void replaceFregment(Fragment fragment)
    {
        FragmentManager  fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
    public  void   onClickLogOut()
    {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
    @Override
    public void OnResponseCallback(Object response) {

    }
    @Override
    public void OnResponseCallback(Object response, Object data) {

        if(response!=null && data!=null)
        {
            if (Integer.parseInt(data.toString()) == AllConstant.CHECK_EXISTENCE_BOOKINGS) {

                if (response.toString().equals(Options.NOTHASBOOKED.getValue()))
                {
                    if (LocalStorage.existing(this, "BookingId"))
                    {
                        Log.d(" LocalStorage.remove","BookingId");
                        LocalStorage.remove(this, "BookingId");
                    }
//                    if (Helper.isServiceRunning(LocationTrackingService.class, MainActivity.this)) {
//                        Intent intentService = new Intent(MainActivity.this, LocationTrackingService.class);
//                        stopService(intentService);
//                    }

                }
//                else if(response.toString().equals(Options.HASBOOKED.getValue()) && !Helper.isServiceRunning(LocationTrackingService.class, MainActivity.this)) {
//                    intilizeLocationService();
//                }
            }
        }
    }
    @Override
    public void OnResponseCallback(Object response, ArrayList data) {

    }




    @Override
    public void hasRole(boolean hasRole)
    {

    }

    @Override
    public void AdminRole() {
        setDisplay(true);

    }
    void setDisplay(boolean isVisible) {

        navigationView.getMenu().findItem(R.id.notifications).setVisible(isVisible);
        navigationView.getMenu().findItem(R.id.statistics).setVisible(isVisible);
        navigationView.getMenu().findItem(R.id.parkingGroups).setVisible(isVisible);
//        navigationView.getMenu().findItem(R.id.parkings).setVisible(isVisible);
    }

    @Override
    public void UserRole() {

        setDisplay(false);

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        backgroundTimer.stopTimer();
//    }



}