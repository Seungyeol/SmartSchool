package com.aura.smartschool.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.utils.PreferenceUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

public class MyLocationService extends Service {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;

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
            //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

            //업로드 시간이 5분이 경과하지 않으면 서버에 업로드 하지 않는다.
            int lastTime = PreferenceUtil.getInstance(MyLocationService.this).getLocationTime();
            int currentTime = (int)(System.currentTimeMillis()/1000);
            if ((currentTime-lastTime) > 60 * 5) {
                PreferenceUtil.getInstance(MyLocationService.this).setLocationTime(currentTime);
                postLocation();
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAq = new AQuery(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mCallbacks)
                .addOnConnectionFailedListener(mFailedListener)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000 * 60 * 10); //10 minutes
        mLocationRequest.setFastestInterval(1000 * 60 * 10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

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

            Log.d("LDK", "url:" + url);
            Log.d("LDK", json.toString(1));

            mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    //update or insert
                    try {
                        if(object.getInt("result") == 0) {
                            Log.d("LDK", "result:" + object.toString(1));
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
