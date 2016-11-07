package momomo00.receivinglocationupdates;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

/**
 * Created by songo_000 on 2016/11/07.
 */

public class MyLocationUpdate
    implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , LocationListener {

    private final static int REQUEST_CODE_LOCATION_PERMISSION = 1000;
    private final static String TAG = "MyLocationUpdate";

    private final static int UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private final static int FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private MyLocationUpdateListener    mMyLocationUpdateListener;

    private boolean mRequestingLocationUpdates;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Location    mCurrentLocation;
    private Date        mDate;

    private AppCompatActivity mActivity;

    public MyLocationUpdate(AppCompatActivity activity) {
        mActivity = activity;
        mMyLocationUpdateListener = null;
        mRequestingLocationUpdates = false;

        buildGoogleApiClient();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: start");
        if(!mRequestingLocationUpdates) {
            Log.d(TAG, "onConnected: mRequestingLocationUpdates: false");
            return;
        }
        Log.d(TAG, "onConnected: mRequestingLocationUpdates: true");
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if(!checkPermission()) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mDate = new Date();

        mMyLocationUpdateListener.onLocationUpdate(mCurrentLocation, mDate);
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    public void restartLocationUpdates() {
        if(mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private boolean checkPermission() {
        int  selfPermission = ActivityCompat.checkSelfPermission(
                mActivity, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(selfPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity
                , new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION}
                , REQUEST_CODE_LOCATION_PERMISSION);
        }
        return true;
    }

    public void setMyLocationUpdateListener(MyLocationUpdateListener listener) {
        mMyLocationUpdateListener = listener;
    }

}
