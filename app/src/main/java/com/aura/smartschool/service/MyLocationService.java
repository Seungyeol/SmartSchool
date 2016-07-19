package com.aura.smartschool.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.database.DBStepCounter;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.vo.WalkingVO;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MyLocationService extends Service {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;

    private ArrayList<Geofence> mGeofenceList;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    private AQuery mAq;

    public MyLocationService() {
    }

    GoogleApiClient.ConnectionCallbacks mCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //if (mLastLocation != null) {
            //    postLocation();
            //}

            //연결이 되면 위치 추적
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);

            //Geofencing 시작
            try {
                //GeoPencing setting
                String strLat = PreferenceUtil.getInstance(MyLocationService.this).getSchool_lat();
                String strLng = PreferenceUtil.getInstance(MyLocationService.this).getSchool_lng();

                if(TextUtils.isEmpty(strLat)) {
                    return;
                }
                double lat = 0;
                double lng = 0;

                lat = Double.parseDouble(strLat);
                lng = Double.parseDouble(strLng);

                mGeofenceList.add(new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                        .setRequestId("School")

                                // Set the circular region of this geofence.
                        .setCircularRegion(
                                lat,   //latitude
                                lng,  //longitude
                                100   //meter
                        )

                                // Set the expiration duration of the geofence. This geofence gets automatically
                                // removed after this period of time.
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)

                                // Set the transition types of interest. Alerts are only generated for these
                                // transition. We track entry and exit transitions in this sample.
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)

                                // Create the geofence.
                        .build());

                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        // The GeofenceRequest object.
                        getGeofencingRequest(),
                        // A pending intent that that is reused when calling removeGeofences(). This
                        // pending intent is used to generate an intent when a matched geofence
                        // transition is observed.
                        getGeofencePendingIntent()
                ).setResultCallback(mResultCallback); // Result processed in onResult().
            } catch (NumberFormatException e) {

            } catch (IllegalArgumentException e) {

            } catch (SecurityException securityException) {
                // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
                logSecurityException(securityException);
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    };

    GoogleApiClient.OnConnectionFailedListener mFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }
    };

    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLastLocation = location;
            //위치값 저장(사진 업로드시 사용)
            PreferenceUtil.getInstance(MyLocationService.this).setLat(mLastLocation.getLatitude());
            PreferenceUtil.getInstance(MyLocationService.this).setLng(mLastLocation.getLongitude());

            //업로드 시간이 5분이 경과하지 않으면 서버에 업로드 하지 않는다.
            int lastTime = PreferenceUtil.getInstance(MyLocationService.this).getLocationTime();
            int currentTime = (int)(System.currentTimeMillis()/1000);
            if ((currentTime-lastTime) > 60 * 5) {
                PreferenceUtil.getInstance(MyLocationService.this).setLocationTime(currentTime);
                postLocation();
                syncWalkingHistory();
            }
        }
    };

    ResultCallback<Status> mResultCallback = new ResultCallback<Status>() {
        @Override
        public void onResult(Status status) {

        }
    };

    @Override
    public void onCreate() {
        mAq = new AQuery(this);

        mGeofenceList = new ArrayList<Geofence>();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mCallbacks)
                .addOnConnectionFailedListener(mFailedListener)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000 * 60 * 10); //10 minutes
        mLocationRequest.setFastestInterval(1000 * 60 * 10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e("LDK", "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void postLocation() {
        String url = Constant.HOST + Constant.API_ADD_LOCATION;
        JSONObject json = new JSONObject();
        try {
            int member_id = PreferenceUtil.getInstance(this).getMemberId();
            if (member_id == 0) {
                return;
            }

            json.put("member_id", member_id);
            json.put("lat", String.valueOf(mLastLocation.getLatitude()));
            json.put("lng", String.valueOf(mLastLocation.getLongitude()));
            //json.put("created_date", System.currentTimeMillis()); //timestamp

            /*List<Address> addresses = null;
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                addresses = geocoder.getFromLocation(
                        mLastLocation.getLatitude(),
                        mLastLocation.getLongitude(), 1);
            } catch (IOException ioException) {
                Log.d("LDK", ioException.getMessage());
            } catch (IllegalArgumentException illegalArgumentException) {
                Log.d("LDK", illegalArgumentException.getMessage());
            } catch (NullPointerException e) {

            }
            if(addresses != null && addresses.size() > 0) {
                json.put("address", addresses.get(0).getAddressLine(0));
            } else {
                json.put("address", "");
            }*/

            //Log.d("LDK", "url:" + url);
            //Log.d("LDK", json.toString(1));

            mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    //update or insert
                    try {
                        if(object.getInt("result") == 0) {
                            //Log.d("LDK", "result:" + object.toString(1));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void syncWalkingHistory() {
        final WalkingVO walkingData = DBStepCounter.getInstance(this).getWalkingDataForUpdate();
        if (walkingData != null) {
            String url = Constant.HOST + Constant.API_ADD_ACTIVITY;
            JSONObject json = new JSONObject();
            try {
                int member_id = PreferenceUtil.getInstance(this).getMemberId();
                if (member_id == 0) {
                    return;
                }
                json.put("member_id", member_id);

                json.put("activity_date", new SimpleDateFormat("yyyy-MM-dd").format(new Date(walkingData.date)));
                json.put("step", walkingData.count);
                json.put("calory", walkingData.calories);
                json.put("distance", walkingData.distance);
                json.put("time", walkingData.activeTime);

                mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {
                        //update or insert
                        try {
                            if(object.getInt("result") == 0) {
                                DBStepCounter.getInstance(MyLocationService.this).completeSync(walkingData.date);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
