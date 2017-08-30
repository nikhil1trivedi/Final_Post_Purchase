package com.example.trivedi_dell.final_app_post_purchase;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.adobe.mobile.Target;
import com.adobe.mobile.TargetLocationRequest;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.HashMap;
import java.util.Map;


public class Welcome_page extends AppCompatActivity{
    //integration of Geolocation to change the picture
    //potential for several pushbuttons navigating to subscription activity

    static final int REQUEST_CHECK_SETTINGS = 0x1; // Request code for "startResolutionforResult" to resolve location setings
    static final int REQUEST_FINE_LOCATION = 17; // Request code for fine location permissions request

    LocationRequest locRequest;
    FusedLocationProviderClient FusedLocationClient;
    LocationCallback locationCallback;

    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome_page);

        img = (ImageView) findViewById(R.id.imageView);
        Button welcome_button = (Button) findViewById(R.id.automated_personalization);
        final Button about_us = (Button) findViewById(R.id.about_us_button) ;
        Button survey_button = (Button) findViewById(R.id.survey_button);

        // Start activity for Automated Personalization content (from Target server)
        welcome_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Welcome_page.this, ap_activity.class));
                finish();
            }
        });

        // Start "About Us" activity
        about_us.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Welcome_page.this , about_us.class));
                finish();
            }
        });

        // Start survey activity (result used to sort user into gender-based audience
        // for Target AB test elsewhere in app
        survey_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Welcome_page.this, survey_activity.class));
                finish();
            }
        });

        Config.setContext(this.getApplicationContext());
        Config.setDebugLogging(true);
        Config.collectLifecycleData(this);

        // Checking if app already has fine location permissions; if not, user must be prompted
        // to provide/deny permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION);
        } else {
            // Setting up fused location provider, creating pending location request,
            // and checking location settings
            setUpFusedLocationProvider();
        }

        // Target calls placed here in case Target calls never occur on this activity (this
        // would be the case if location permission/settings issues are never resolved by user
        // while on this activity). This way, we ensure that Target can still collect session info
        // from this activity.
        TargetLocationRequest targetRequest = Target.createRequest("geofence-mbox-ex-01", "default content",
                null);
        Target.loadRequest(targetRequest, new Target.TargetCallback<String>() {
            @Override
            public void call(final String content) {
                // Perform image retrieval here!
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d("onPause", "is being called");
    }
    @Override
    public void onStop() {
        super.onStop();

        // Stopping location updates when activity is hidden to save power
        FusedLocationClient.removeLocationUpdates(locationCallback);
    }
    @Override
    public void onResume() {
        super.onResume();

        // Checking to see that proper location permissions/settings requirements are
        // still satisfied after "onPause()" or "onStop()" callbacks are called
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION);
        } else {
            // Setting up fused location provider, creating pending location request,
            // and checking location settings
            setUpFusedLocationProvider();

            // CALL ABOVE IS WASTEFUL AS PROVIDER IS RE-INITIALIZED. HOWEVER, WE STILL NEED
            // TO DO LOCATION SETTINGS CHECK AGAIN, SO ABOVE METHOD CALLED AGAIN FOR
            // COMPACTNESS SAKE, RE-INITIALIZING FUSED LOCATION PROVIDER IN THE PROCESS
        }
    }

    // Method called when user "responds" to coarse location permissions request prompt
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpFusedLocationProvider();
            } else {
                // Handle denied permissions here
            }
        }
    }

    // Sets up fused location provider to check location settings, handle user response,
    // and if setting requirements are met track location for Analytics POI data
    // and related Target geofence offers
    public void setUpFusedLocationProvider() {
        // Initializing fused location provider to handle location updates
        Log.d("setUpFusedProvider", "We are in the setting up method!");
        FusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Creating location request
        locRequest = new LocationRequest();
        locRequest.setInterval(5000);
        locRequest.setFastestInterval(7000);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locRequest);

        // Asynchronous task to determine if location settings are met for location request
        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        // Callback for when all location settings requirements are met
        result.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d("onSuccess", "all location settings were met!");
                beginLocationUpdates();
            }
        });

        // Callback for when location settings requirements are not met
        result.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("onFailure", "location settings were NOT met!");
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(Welcome_page.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    // Method called when "startResolutionForResult" completes, with result of user choice passed in
    // as "resultCode" parameter
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case RESULT_OK: {
                    // Locaion settings issue resolved by user
                    beginLocationUpdates();
                    break;
                }
                case RESULT_CANCELED: {
                    // User chose not to resolve location settings issue
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    // Request location updates with provider, provide callbacks
    // for when location data is available
    protected void beginLocationUpdates() {
        Log.d("beginLocationUpdates", "We are in location updates method!");
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d("locCallback", "inside location callback on result!");

                // Sending location/POI data via Analytics for geofencing
                Analytics.trackLocation(locationResult.getLastLocation(), null);

                // Retrieving location specific image offer (POI audience) from Target server
                TargetLocationRequest targetRequest = Target.createRequest("geofence-mbox-ex-01", "default content",
                        null);
                Target.loadRequest(targetRequest, new Target.TargetCallback<String>() {
                    @Override
                    public void call(final String content) {
                        if (!content.equals("no location")) {
                            final RequestCreator rq = Picasso.with(getApplicationContext()).load(content)
                                    .resize(700, 736);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rq.into(img);
                                }
                            });
                        } else {
                            final RequestCreator rq = Picasso.with(getApplicationContext()).load(R.drawable.background_main)
                                    .resize(500, 736);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rq.into(img);
                                }
                            });
                        }
                    }
                });
            }
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                Log.d("onLocAvailability", "in location availability callback!");
                if (locationAvailability.isLocationAvailable()) {
                    Log.d("onLocAvailability", "current device location is available!");
                } else {
                    Log.d("onLocAvailability", "current device location is NOT available!");
                }
            }
        };

        try {
            Log.d("try location updates", "trying to set up damn updates!");
            Task<Void> t1 = FusedLocationClient.requestLocationUpdates(locRequest, locationCallback, null);

            t1.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void a) {
                    Log.d("setup task", "location update request task is successful!");

                    // Following "Try" block attempts to get initial location update to load offer
                    // right on activity start, as FusedLocationProviderClient's "onResult" callback
                    // is only run on location changes (i.e. not when app is first loaded in a geofence).
                    try {
                        Task<Location> initLocationUpdate = FusedLocationClient.getLastLocation();

                        initLocationUpdate.addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                Log.d("setup task", "Initial location update request task is successful!");
                                Analytics.trackLocation(location, null);
                                TargetLocationRequest targetRequest = Target.createRequest("geofence-mbox-ex-01", "default content",
                                        null);
                                Target.loadRequest(targetRequest, new Target.TargetCallback<String>() {
                                    @Override
                                    public void call(final String content) {
                                        if (!content.equals("no location")) {
                                            final RequestCreator rq = Picasso.with(getApplicationContext()).load(content)
                                                    .resize(700, 736);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    rq.into(img);
                                                }
                                            });
                                        } else {
                                            final RequestCreator rq = Picasso.with(getApplicationContext()).load(R.drawable.background_main)
                                                    .resize(500, 736);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    rq.into(img);
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });
                        initLocationUpdate.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Log.e("Initial location update", "exception", e);
                            }
                        });

                    } catch (SecurityException f){
                        Log.d("reqLocationUpdates()", "Error that you should never be seeing!!!");
                    }
                }
            });

            t1.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception g) {
                    Log.e("setup task", "exception", g);
                }
            });

        } catch (SecurityException g) {
            Log.d("reqLocationUpdates()", "Error that you should never be seeing!!!");
        }
    }

}

