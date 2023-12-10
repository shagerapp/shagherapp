package com.example.parking;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.parking.ApiClient.AppConfig;
import com.example.parking.ApiClient.Controllers.BookingController;
import com.example.parking.ApiClient.Controllers.MapsDirectionsController;
import com.example.parking.ApiClient.Controllers.TrackinUserController;
import com.example.parking.ApiClient.Enums.MapDirectionMode;
import com.example.parking.ApiClient.Enums.UserLocationState;
import com.example.parking.ApiClient.Helpers.MapHelper;
import com.example.parking.ApiClient.IDirectionCallBack;
import com.example.parking.ApiClient.WebServices.RetrofitAPI;
import com.example.parking.ApiClient.WebServices.RetrofitClient;
import com.example.parking.ApiClient.interfaces.ITrackingUserResponse;
import com.example.parking.ApiClient.models.BaseResponse;
import com.example.parking.ApiClient.models.DirectionPlaceModel.DirectionLegModel;
import com.example.parking.ApiClient.models.DirectionPlaceModel.DirectionResponseModel;
import com.example.parking.Constant.AllConstant;
import com.example.parking.Enum.Options;
import com.example.parking.Helpers.Helper;
import com.example.parking.Helpers.ICallback;
import com.example.parking.Helpers.IServiceLocation;
import com.example.parking.Helpers.LocalStorage;
import com.example.parking.Models.Spot;
import com.example.parking.Permissions.AppPermissions;
import com.example.parking.Utility.LoadingDialog;
import com.example.parking.databinding.BottomSheetLayoutBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class DirectionActivity extends AppCompatActivity implements IServiceLocation, ICallback, ITrackingUserResponse, OnMapReadyCallback, IDirectionCallBack {

    private com.example.parking.databinding.ActivityDirectionBinding binding;
    private GoogleMap mGoogleMap;
    private AppPermissions appPermissions;
    private boolean isLocationPermissionOk, isTrafficEnable;
    private BottomSheetBehavior<RelativeLayout> bottomSheetBehavior;
    private BottomSheetLayoutBinding bottomSheetLayoutBinding;
    private RetrofitAPI retrofitAPI;
    private LoadingDialog loadingDialog;
    private Location currentLocation;
    private Double endLat, endLng;
    // المسافة المقبولة للوصول إلى الموقع بالأمتار
    private final double thresholdDistance = 3;
    private String placeId;
    private DirectionStepAdapter adapter;
    private MapsDirectionsController mapsDirectionsController;
    //==============================================

    Timer timer = new Timer();
    List<LatLng> pathPoints = new ArrayList<>();
    int count = 0;
    LatLng startLocation, endLocation;
    //==============================================
    private Handler handlers = new Handler();
    LatLng destination = null;
    LatLng original = null;

    private boolean hasResponse = true;
    private boolean startMoving = false, directionView = true;
    private Marker movingMarker;

    private LinearLayout arrival_notify_dialog_box;
    private Button btn_arrival_notify_ok;
    private TextView btn_arrival_notify_close;
    private PolylineOptions oldOptions;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    private TrackinUserController trackingUserControll;
    private boolean isInsideUnivirsty = false;
    private boolean bookedNotifyIsView = false;
    private boolean arrivalParking = false;
    private boolean startTraking = true;


    private TextView arrivalTextView;
    private Spot spot;
    private String spotKey;
    private boolean hasBooked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.parking.databinding.ActivityDirectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        appPermissions = new AppPermissions();
        mapsDirectionsController = new MapsDirectionsController(this);
        endLat = getIntent().getDoubleExtra("lat", AppConfig.DOOR_COORDINATES.latitude);
        endLng = getIntent().getDoubleExtra("lng", AppConfig.DOOR_COORDINATES.longitude);
        original = new LatLng(0, 0);
        if (getIntent().hasExtra("placeId"))
            placeId = getIntent().getStringExtra("placeId");
        if (getIntent().hasExtra("spot")) {

            String data = getIntent().getStringExtra("spot");
            spotKey = getIntent().getStringExtra("spotKey");
            spot = new Gson().fromJson(data, Spot.class);
            endLat = spot.getCoordinates().latitude;
            endLng = spot.getCoordinates().longitude;

        }
        new BookingController().checkIfUserHasBooking(this);

        if (endLat != null && endLng != null)
            destination = new LatLng(endLat, endLng);

        initializition();
        initializArrivalDialogBox();
    }

    @Override
    public void OnResponseCallback(Object response) {

        if (response.equals(Options.NOTHASBOOKED.getValue())) {
            hasBooked = false;
            if (LocalStorage.existing(this, "BookingId"))
                LocalStorage.remove(this, "BookingId");
        } else if (response.equals(Options.NOTHASBOOKED.getValue()))
            hasBooked = true;
    }

    private void initializition() {


        trackingUserControll = new TrackinUserController(getApplicationContext());
        loadingDialog = new LoadingDialog(this);
        retrofitAPI = RetrofitClient.getRetrofitClient().create(RetrofitAPI.class);
        bottomSheetLayoutBinding = binding.bottomSheet;
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayoutBinding.getRoot());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        adapter = new DirectionStepAdapter();
        bottomSheetLayoutBinding.stepRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bottomSheetLayoutBinding.stepRecyclerView.setAdapter(adapter);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.directionMap);
        mapFragment.getMapAsync(this);
        binding.enableDirections.setOnClickListener(v -> onEnableDirections());
        binding.enableTraffic.setOnClickListener(view -> onEnableTraffic());
        binding.travelMode.setOnCheckedChangeListener((group, id) -> onChangeTravelMode(group, id));
        binding.btnBack.setOnClickListener(v -> onBack());
        binding.enableDirections.setBackgroundTintList(ColorStateList.valueOf(Color.argb(0.5f, 100, 0, 200)));

    }

    private void onBack() {
        Intent intent = new Intent(DirectionActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void onEnableDirections() {
        directionView = !directionView;
        if (directionView) {
            startMoving = false;
            binding.enableDirections.setBackgroundTintList(ColorStateList.valueOf(Color.argb(0.5f, 100, 0, 200)));

        } else {
            binding.enableDirections.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            startMoving = false;
            mGoogleMap.clear();
        }

    }

    private void initializArrivalDialogBox() {

        arrival_notify_dialog_box = findViewById(R.id.arrival_notify_dialog_box);
        btn_arrival_notify_ok = (Button) findViewById(R.id.btnOk);
        btn_arrival_notify_close = (TextView) findViewById(R.id.btnCancel);
        arrivalTextView = (TextView) findViewById(R.id.firstText);
        btn_arrival_notify_ok.setOnClickListener(v -> displayArrivalNotifyDialogBox(View.GONE, R.string.arrival));
        btn_arrival_notify_close.setOnClickListener(v -> displayArrivalNotifyDialogBox(View.GONE, R.string.arrival));

    }

    private void displayArrivalNotifyDialogBox(final int isView, int text) {
        arrivalParking = true;
        arrivalTextView.setText(text);
        binding.backgroundDialogSpots.setVisibility(isView);
        btn_arrival_notify_close.setVisibility(isView);
        // start  location tracking  service

    }


    private Marker showMarker(LatLng lat, float color, String title) {
        return showMarker(lat, color, title, null);
    }

    private Marker showMarker(LatLng lat, float color, String title, Drawable markerIcon) {
        MarkerOptions options = new MarkerOptions();
        options.position(lat);
        options.title(title);
        if (markerIcon == null)
            options.icon(BitmapDescriptorFactory.defaultMarker(color));
        else {
            // Get the Drawable object
            Bitmap bitmap = Helper.DrawableToBitmap(markerIcon);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
            options.icon(icon);
        }
        return mGoogleMap.addMarker(options);
    }

    private void onEnableTraffic() {

        if (isTrafficEnable) {
            binding.enableTraffic.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            if (mGoogleMap != null)
                mGoogleMap.setTrafficEnabled(false);
            isTrafficEnable = false;

        } else {
            isTrafficEnable = true;
            binding.enableTraffic.setBackgroundTintList(ColorStateList.valueOf(Color.argb(0.5f, 100, 0, 200)));

            if (mGoogleMap != null)
                mGoogleMap.setTrafficEnabled(true);

        }


    }

    private void onChangeTravelMode(ChipGroup group, int checkedId) {
        if (checkedId != -1) {
            switch (checkedId) {
                case R.id.btnChipDriving:
                    getDirection(MapDirectionMode.DRIVING);
                    break;
                case R.id.btnChipWalking:
                    getDirection(MapDirectionMode.WALKING);
                    break;
                case R.id.btnChipBike:
                    getDirection(MapDirectionMode.BICYCLING);
                    break;
                case R.id.btnChipTrain:
                    getDirection(MapDirectionMode.TRANSIT);
                    break;
            }
        }
    }

    private void getDirection(MapDirectionMode mode) {

        mapsDirectionsController.getDirections(original, destination, mode, isLocationPermissionOk, DirectionActivity.this);
    }

    @Override
    public void onStartDirection(Object value) {
        hasResponse = false;
        loadingDialog.startLoading();
    }

    @Override
    public void onStopDirection(Object value) {
        hasResponse = true;
        loadingDialog.stopLoading();
    }

    @Override
    public void onDirectionFailure(Object value) {
        loadingDialog.stopLoading();
    }

    @Override
    public void onResponseDirection(DirectionResponseModel dirResponse, DirectionLegModel legModel, PolylineOptions options, LatLng startLoc, LatLng endLoc, double distance) {

        float newBearing = MapHelper.calculateBearingBetweenPoints(startLoc, endLoc);

        bottomSheetLayoutBinding.txtSheetTime.setText(legModel.getDuration().getText());
        bottomSheetLayoutBinding.txtSheetDistance.setText(legModel.getDistance().getText());
        adapter.setDirectionStepModels(legModel.getSteps());
        if (directionView) {
            if (startMoving && oldOptions != null) {
                oldOptions.color(Color.WHITE);
                oldOptions.visible(true);
                mGoogleMap.addPolyline(oldOptions);
            }
            mGoogleMap.addPolyline(options);
            oldOptions = options;
        }

//        if (startLoc.latitude <= endLoc.latitude)
//        {
//            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(startLoc, endLoc),16));
//        }
        if (distance <= thresholdDistance)
            if (!bookedNotifyIsView && spot != null) {
                bookedNotifyIsView = true;
                btn_arrival_notify_ok.setOnClickListener(v ->
                {
                    Intent intent = new Intent(DirectionActivity.this, MainActivity.class);
                    intent.putExtra("spot", new Gson().toJson(spot).toString());
                    intent.putExtra("spotKey", spotKey);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
                displayArrivalNotifyDialogBox(View.VISIBLE, R.string.userِArrivesUniversity);
            }


//        if(distance<=thresholdDistance)
//            displayArrivalNotifyDialogBox(View.VISIBLE,R.string.arrival);
//        else if(startTraking){ // && !LocalStorage.existing(this, "BookingId")){
//
//                UserLocationStateModel userLocationState = new UserLocationStateModel();
//                userLocationState.setUserId(FirebaseAuth.getInstance().getUid());
//                userLocationState.setBookingId("0");
//                userLocationState.setLatitude(original.latitude);
//                userLocationState.setLongitude(original.longitude);
//                trackingUserControll.checkUserLocationState(userLocationState,DirectionActivity.this );
//        }

        // if(mGoogleMap.getCameraPosition().bearing!=newBearing)
        {
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(startLoc)
                    .bearing(newBearing)
                    .zoom(16)
                    .build()));
        }
        if (!startMoving) {
            startMoving = true;
            binding.txtEndLocation.setText(legModel.getEndAddress());
            showMarker(endLoc, BitmapDescriptorFactory.HUE_RED, "Destination location");
        }
//        movingMarker.setPosition(startLoc);
    }

    private void clearUI() {

        mGoogleMap.clear();
        binding.txtStartLocation.setText("");
        binding.txtEndLocation.setText("");
//        getSupportActionBar().setTitle("");
        bottomSheetLayoutBinding.txtSheetDistance.setText("");
        bottomSheetLayoutBinding.txtSheetTime.setText("");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        if (appPermissions.isLocationOk(this)) {
            isLocationPermissionOk = true;
            setupGoogleMap();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("Near Me required location permission to show you near by places")
                        .setIcon(R.drawable.baseline_share_location_24)
                        .setPositiveButton("Ok", (d, w) -> appPermissions.requestLocationPermission(DirectionActivity.this))
                        .create().show();
            } else {
                appPermissions.requestLocationPermission(DirectionActivity.this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AllConstant.LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionOk = true;
                setupGoogleMap();

            } else {
                isLocationPermissionOk = false;
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupGoogleMap() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(false);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
        getCurrentLocation();

    }

    private void getCurrentLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback=new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult!=null && locationResult.getLastLocation() != null) {
                            currentLocation=locationResult.getLastLocation();

                        original= new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        original = destination;// new LatLng(21.58202746649747, 39.17925351007725);
                        getDirection(MapDirectionMode.DRIVING);
                            }
                    }

            }, Looper.myLooper());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(fusedLocationProviderClient!=null && locationCallback!=null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(fusedLocationProviderClient!=null && locationCallback!=null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else
            super.onBackPressed();
    }


    @Override
    public void onSuccess(BaseResponse response) {

      if(response!=null && response.getResultCode()==200 )
          if(response.getResult().equals(UserLocationState.INSIDE.getValue()))
          {
              isInsideUnivirsty=true;
              startTraking=true;

            //  if(fusedLocationProviderClient!=null && locationCallback!=null)
            //      fusedLocationProviderClient.removeLocationUpdates(locationCallback);


              if(!bookedNotifyIsView && spot!=null) {

                  if(!hasBooked) {
                      bookedNotifyIsView = true;
                      btn_arrival_notify_ok.setOnClickListener(v ->
                      {
                          Intent intent = new Intent(DirectionActivity.this, MainActivity.class);
                          intent.putExtra("spot", new Gson().toJson(spot).toString());
                          intent.putExtra("spotKey", spotKey);
                          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                          startActivity(intent);
                      });
                      displayArrivalNotifyDialogBox(View.VISIBLE, R.string.userِArrivesUniversity);
                  }

              }

          } else{
              isInsideUnivirsty=false;
              startTraking=true;
          }
    }

    private void replaceFregment(Fragment fragment)
    {
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFailure(String call) {

    }

    @Override
    public void onServiceLocationResponse(Object response) {

    }
}