package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.BuildConfig;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission_group.CAMERA;
import static com.example.myapplication.FilterActivity.mypreference1;
import static com.example.myapplication.FilterAdapter.Name1;
import static com.example.myapplication.FilterAdapter.Name2;
import static com.example.myapplication.MapDetailActivity.REQUEST_LOCATION_PERMISSION;

public class MainActivity extends AppCompatActivity
         {

    public static final String TAG="Main";

    RecyclerView recyclerView;
    SearchView searchView;
    ArrayList<String> namePlace = new ArrayList<>();
    ArrayList<String> addressPlace = new ArrayList<>();
    ArrayList<String> faxNumberList = new ArrayList<>();
    ArrayList<Double> latList = new ArrayList<>();
    ArrayList<Double> lonList = new ArrayList<>();

    MainAdapter filterAdapter;
    SharedPreferences sharedpreferences1;
    String sortData,filterState;


   /**
    * Code used in requesting runtime permissions.
    */
   private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

   /**
    * Constant used in the location settings dialog.
    */
   private static final int REQUEST_CHECK_SETTINGS = 0x1;

   /**
    * The desired interval for location updates. Inexact. Updates may be more or less frequent.
    */
   private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

   /**
    * The fastest rate for active location updates. Exact. Updates will never be more frequent
    * than this value.
    */
   private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
           UPDATE_INTERVAL_IN_MILLISECONDS / 2;

   // Keys for storing activity state in the Bundle.
   private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
   private final static String KEY_LOCATION = "location";


   /**
    * Provides access to the Fused Location Provider API.
    */
   private FusedLocationProviderClient mFusedLocationClient;

   /**
    * Provides access to the Location Settings API.
    */
   private SettingsClient mSettingsClient;

   /**
    * Stores parameters for requests to the FusedLocationProviderApi.
    */
   private LocationRequest mLocationRequest;

   /**
    * Stores the types of location services the client is interested in using. Used for checking
    * settings to determine if the device has optimal location settings.
    */
   private LocationSettingsRequest mLocationSettingsRequest;

   /**
    * Callback for Location events.
    */
   private LocationCallback mLocationCallback;

   /**
    * Represents a geographical location.
    */
   private Location mCurrentLocation;



   /**
    * Tracks the status of the location updates request. Value changes when the user presses the
    * Start Updates and Stop Updates buttons.
    */
   private Boolean mRequestingLocationUpdates;
             private TextView mLatitudeTextView;
             private TextView mLongitudeTextView;
             ImageView closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
        mLongitudeTextView = (TextView) findViewById(R.id.longitude_text);

        mLatitudeTextView.setVisibility(View.INVISIBLE);
        mLongitudeTextView.setVisibility(View.INVISIBLE);

        searchView = (SearchView) findViewById(R.id.searchView);


        //sorting data
        sharedpreferences1 = getSharedPreferences(mypreference1,
                Context.MODE_PRIVATE);
        sortData = sharedpreferences1.getString(Name2,"");
        Log.i(TAG,"MainActivitySP"+sortData);

       //sorting state
        sharedpreferences1 = getSharedPreferences(mypreference1,
                Context.MODE_PRIVATE);
        filterState = sharedpreferences1.getString(Name1,"");

        Log.i(TAG,"FilterState"+filterState);



        // get the reference of RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);




// Catch event on [x] button inside search view
        int searchCloseButtonId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        closeButton = (ImageView) this.searchView.findViewById(searchCloseButtonId);
// Set on click listener




// Update values using data stored in the Bundle.
      //  updateValuesFromBundle(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();


    }





    //otp problem serialnum
    private void fetchJson(final double latitude, final double longitude, final String search){


            try {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("ios","100");
                jsonBody.put("lan","EN");
                jsonBody.put("ver","100");
                final String mRequestBody = jsonBody.toString();

                      JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                           "https://secure.kwsp.gov.my/m2/postBranchLocation", null,
                            new Response.Listener<JSONObject>() {


                                @Override
                    public void onResponse(JSONObject  response) {
                        Log.e(TAG, "OTP Response: " + response);

                        try {


                            JSONArray arr = response.getJSONArray("lis");
                            JSONObject element;


                                //clear to avoid duplication
                                namePlace.clear();
                                addressPlace.clear();


                                for(int i = 0; i < arr.length(); i++) {
                                element = arr.getJSONObject(i);



                                    if(filterState==null){

                                        namePlace.add("EPF "+element.getString("nam")+" Office");
                                        addressPlace.add(element.getString("ads"));
                                        faxNumberList.add(element.getString("fax"));

                                        latList.add((double) element.getDouble("lat"));
                                        lonList.add((double) element.getDouble("lon"));



                                    }

                                   else if(filterState==element.getString("nam")) {

                                        namePlace.add("EPF " + element.getString("nam") + " Office");
                                        addressPlace.add(element.getString("ads"));
                                        faxNumberList.add(element.getString("fax"));

                                        break;
                                    }
                                    else{
                                       //add into  each list
                                        namePlace.add("EPF "+element.getString("nam")+" Office");
                                        addressPlace.add(element.getString("ads"));
                                        faxNumberList.add(element.getString("fax"));
                                        latList.add((double) element.getDouble("lat"));
                                        lonList.add((double) element.getDouble("lon"));


                                    }




                                        //sorting
                                    if(sortData.contains("Name")){
                                        Collections.sort(namePlace);
                                        Collections.sort(addressPlace);

                                    }
                                    else if(sortData.contains("Distance")){
                                        Collections.sort(namePlace);
                                        Collections.sort(addressPlace);

                                    }











                                Log.i(TAG, "PersonName: " + namePlace);
                                    Log.i(TAG, "LatLon: " + latList);


                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }




             filterAdapter = new MainAdapter(MainActivity.this, namePlace, addressPlace,faxNumberList,latList,lonList, latitude, longitude);
            recyclerView.setAdapter(filterAdapter); // set the Adapter to RecyclerView



                                    if(search.contains( "update")){

                                        // refreshing recycler view
                                        filterAdapter.notifyDataSetChanged();
                                    }






                                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                        @Override
                                        public boolean onQueryTextSubmit(String query) {

                                            filterAdapter.getFilter().filter(query);

//                                            if(query==null||query.isEmpty()){
//                                                String search="search";
//                                                fetchJson(latitude,longitude,search);
//                                            }

                                            String search="search";

                                             getUpdateUI(search);

                                            return false;
                                        }

                                        @Override
                                        public boolean onQueryTextChange(String newText) {
                                            filterAdapter.getFilter().filter(newText);

//                                            if(newText==null||newText.isEmpty()){
//                                                String search="search";
//                                                fetchJson(latitude,longitude,search);
//                                            }

                                            return false;
                                        }


                                    });


                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "OTP Error : " + error.getMessage());
                        Toast.makeText(getApplicationContext()," OTP Error ",
                                Toast.LENGTH_LONG).show();

                    }
                }){

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        try {
                            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                            return null;
                        }
                    }

                    };

                //Adding request to request queue

                requestQueue.add(jsonObjReq);

            }catch (JSONException e) {
                e.printStackTrace();
            }




    }
             String data;

    public String getUpdateUI(String search){


        if(search.contains("search")){

            data =search;
        }

        return  data;
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);


   
      

        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent mainIntent = new Intent(MainActivity.this, FilterActivity.class);
        startActivity(mainIntent);

        return super.onOptionsItemSelected(item);
    }




             /**
              * Sets up the location request. Android has two location request settings:
              * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
              * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
              * the AndroidManifest.xml.
              * <p/>
              * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
              * interval (5 seconds), the Fused Location Provider API returns location updates that are
              * accurate to within a few feet.
              * <p/>
              * These settings are appropriate for mapping applications that show real-time location
              * updates.
              */
             private void createLocationRequest() {
                 mLocationRequest = new LocationRequest();

                 // Sets the desired interval for active location updates. This interval is
                 // inexact. You may not receive updates at all if no location sources are available, or
                 // you may receive them slower than requested. You may also receive updates faster than
                 // requested if other applications are requesting location at a faster interval.
                 mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

                 // Sets the fastest rate for active location updates. This interval is exact, and your
                 // application will never receive updates faster than this value.
                 mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

                 mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
             }

             /**
              * Creates a callback for receiving location events.
              */
             private void createLocationCallback() {
                 mLocationCallback = new LocationCallback() {
                     @Override
                     public void onLocationResult(LocationResult locationResult) {
                         super.onLocationResult(locationResult);

                         mCurrentLocation = locationResult.getLastLocation();
                         updateLocationUI();
                     }
                 };
             }

             /**
              * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
              * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
              * if a device has the needed location settings.
              */
             private void buildLocationSettingsRequest() {
                 LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                 builder.addLocationRequest(mLocationRequest);
                 mLocationSettingsRequest = builder.build();
             }

             @Override
             protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                 super.onActivityResult(requestCode, resultCode, data);
                 switch (requestCode) {
                     // Check for the integer request code originally supplied to startResolutionForResult().
                     case REQUEST_CHECK_SETTINGS:
                         switch (resultCode) {
                             case Activity.RESULT_OK:
                                 Log.i(TAG, "User agreed to make required location settings changes.");
                                 // Nothing to do. startLocationupdates() gets called in onResume again.
                                 break;
                             case Activity.RESULT_CANCELED:
                                 Log.i(TAG, "User chose not to make required location settings changes.");
                                 mRequestingLocationUpdates = false;
                                 updateUI();
                                 break;
                         }
                         break;
                 }
             }


             /**
              * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
              * runtime permission has been granted.
              */
             private void startLocationUpdates() {
                 // Begin by checking if the device has the necessary location settings.
                 mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                         .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                             @Override
                             public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                                 Log.i(TAG, "All location settings are satisfied.");

                                 //noinspection MissingPermission
                                 mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                         mLocationCallback, Looper.myLooper());

                                 updateUI();
                             }
                         })
                         .addOnFailureListener(this, new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 int statusCode = ((ApiException) e).getStatusCode();
                                 switch (statusCode) {
                                     case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                         Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                                 "location settings ");
                                         try {
                                             // Show the dialog by calling startResolutionForResult(), and check the
                                             // result in onActivityResult().
                                             ResolvableApiException rae = (ResolvableApiException) e;
                                             rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                         } catch (IntentSender.SendIntentException sie) {
                                             Log.i(TAG, "PendingIntent unable to execute request.");
                                         }
                                         break;
                                     case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                         String errorMessage = "Location settings are inadequate, and cannot be " +
                                                 "fixed here. Fix in Settings.";
                                         Log.e(TAG, errorMessage);
                                         Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                         mRequestingLocationUpdates = false;
                                 }

                                 updateUI();
                             }
                         });
             }

             /**
              * Updates all UI fields.
              */
             private void updateUI() {

                 updateLocationUI();
             }


             /**
              * Sets the value of the UI fields for the location latitude, longitude and last update time.
              */
             private void updateLocationUI() {
                 if (mCurrentLocation != null) {
                     mLatitudeTextView.setText(String.format(Locale.ENGLISH,  "%f",
                             mCurrentLocation.getLatitude()));

                     mLongitudeTextView.setText(String.format(Locale.ENGLISH,  "%f",
                             mCurrentLocation.getLongitude()));

                     String search="update";
                     fetchJson( mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),search);



                     closeButton.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {

                             String search="update";
                             fetchJson( mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),search);
                             searchView.setQuery("", false);
                             searchView.setIconified(true);


                         }
                     });

                 }
             }


//
//             @Override
             public void onResume() {
                 super.onResume();
                 // Within {@code onPause()}, we remove location updates. Here, we resume receiving
                 // location updates if the user has requested them.
                 if (checkPermissions()) {
                     startLocationUpdates();
                 } else if (!checkPermissions()) {
                     requestPermissions();
                 }

              //   updateUI();
             }
//
//             @Override
//             protected void onPause() {
//                 super.onPause();
//
//                 // Remove location updates to save battery.
//                 //stopLocationUpdates();
//             }

             /**
              * Stores activity data in the Bundle.
              */


             /**
              * Shows a {@link Snackbar}.
              *
              * @param mainTextStringId The id for the string resource for the Snackbar text.
              * @param actionStringId   The text of the action item.
              * @param listener         The listener associated with the Snackbar action.
              */
             private void showSnackbar(final int mainTextStringId, final int actionStringId,
                                       View.OnClickListener listener) {
                 Snackbar.make(
                         findViewById(android.R.id.content),
                         getString(mainTextStringId),
                         Snackbar.LENGTH_INDEFINITE)
                         .setAction(getString(actionStringId), listener).show();
             }

             /**
              * Return the current state of the permissions needed.
              */
             private boolean checkPermissions() {
                 int permissionState = ActivityCompat.checkSelfPermission(this,
                         Manifest.permission.ACCESS_FINE_LOCATION);
                 return permissionState == PackageManager.PERMISSION_GRANTED;
             }

             private void requestPermissions() {
                 boolean shouldProvideRationale =
                         ActivityCompat.shouldShowRequestPermissionRationale(this,
                                 Manifest.permission.ACCESS_FINE_LOCATION);

                 // Provide an additional rationale to the user. This would happen if the user denied the
                 // request previously, but didn't check the "Don't ask again" checkbox.
                 if (shouldProvideRationale) {
                     Log.i(TAG, "Displaying permission rationale to provide additional context.");
                     showSnackbar(R.string.app_name,
                             android.R.string.ok, new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {
                                     // Request permission
                                     ActivityCompat.requestPermissions(MainActivity.this,
                                             new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                             REQUEST_PERMISSIONS_REQUEST_CODE);
                                 }
                             });
                 } else {
                     Log.i(TAG, "Requesting permission");
                     // Request permission. It's possible this can be auto answered if device policy
                     // sets the permission in a given state or the user denied the permission
                     // previously and checked "Never ask again".
                     ActivityCompat.requestPermissions(MainActivity.this,
                             new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                             REQUEST_PERMISSIONS_REQUEST_CODE);
                 }
             }

             /**
              * Callback received when a permissions request has been completed.
              */
             @Override
             public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                    @NonNull int[] grantResults) {
                 Log.i(TAG, "onRequestPermissionResult");
                 if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
                     if (grantResults.length <= 0) {
                         // If user interaction was interrupted, the permission request is cancelled and you
                         // receive empty arrays.
                         Log.i(TAG, "User interaction was cancelled.");
                     } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                         Log.i(TAG, "Permission granted, updates requested, starting location updates");
                         startLocationUpdates();

                     } else {
                         // Permission denied.

                         // Notify the user via a SnackBar that they have rejected a core permission for the
                         // app, which makes the Activity useless. In a real app, core permissions would
                         // typically be best requested during a welcome-screen flow.

                         // Additionally, it is important to remember that a permission might have been
                         // rejected without asking the user for permission (device policy or "Never ask
                         // again" prompts). Therefore, a user interface affordance is typically implemented
                         // when permissions are denied. Otherwise, your app could appear unresponsive to
                         // touches or interactions which have required permissions.
                         showSnackbar(R.string.app_name,
                                 R.string.app_name, new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {
                                         // Build intent that displays the App settings screen.
                                         Intent intent = new Intent();
                                         intent.setAction(
                                                 Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                         Uri uri = Uri.fromParts("package",
                                                 BuildConfig.APPLICATION_ID, null);
                                         intent.setData(uri);
                                         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                         startActivity(intent);
                                     }
                                 });
                     }
                 }
             }




}