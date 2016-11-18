package momomo00.receivinglocationupdates;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
        , MyPermissionManager.WhenGrantedListener
        , LocationListener {

    private final static int UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private final static int FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private MyLocationUpdateListener    mMyLocationUpdateListener;

    private boolean mRequestingLocationUpdates;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Location    mCurrentLocation;
    private Date    mLastUpdateTime;

    private Context mContext;

    public MyLocationUpdate(Context context) {
        Log.d(MyLog.TAG, "MyLocationUpdate: MyLocationUpdate");
        mContext = context;
        mMyLocationUpdateListener = null;
        mRequestingLocationUpdates = false;
        mCurrentLocation = null;
        mLastUpdateTime = null;

        buildGoogleApiClient();
    }

    private void buildGoogleApiClient() {
        Log.d(MyLog.TAG, "MyLocationUpdate: buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();
    }

    private void createLocationRequest() {
        Log.d(MyLog.TAG, "MyLocationUpdate: createLocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(MyLog.TAG, "MyLocationUpdate: onConnected");

        if(mCurrentLocation == null) {
            if(MyPermissionManager.checkPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                mLastUpdateTime = new Date();
                executionOnLocationUpdate(mCurrentLocation, mLastUpdateTime);
            }
        }

        if(mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(MyLog.TAG, "MyLocationUpdate: onConnectionSuspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(MyLog.TAG, "MyLocationUpdate: onConnectionSuspended");
    }

    public void startLocationUpdates() {
        Log.d(MyLog.TAG, "MyLocationUpdate: startLocationUpdates");
        boolean result
                = MyPermissionManager.checkPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if(!result) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(MyLog.TAG, "MyLocationUpdate: onLocationChanged");
        mCurrentLocation = location;
        mLastUpdateTime = new Date();
        executionOnLocationUpdate(location, mLastUpdateTime);
    }

    public void stopLocationUpdates() {
        Log.d(MyLog.TAG, "MyLocationUpdate: stopLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void onStart() {
        mGoogleApiClient.connect();
    }

    public void onResume() {
        if(mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    public void onPause() {
        if(mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
    }

    public interface MyLocationUpdateListener {
        void onLocationUpdate(Location location, Date date);
    }

    public void setMyLocationUpdateListener(MyLocationUpdateListener listener) {
        Log.d(MyLog.TAG, "MyLocationUpdate: setMyLocationUpdateListener");
        mMyLocationUpdateListener = listener;
    }

    private void executionOnLocationUpdate(Location location, Date date) {
        if(mMyLocationUpdateListener == null) {
            return;
        }
        mMyLocationUpdateListener.onLocationUpdate(location, date);
    }

    @Override
    public void whenGranted() {
        Log.d(MyLog.TAG, "MyLocationUpdate: whenGranted");
    }

    public boolean getRequestingLocationUpdates() {
        return mRequestingLocationUpdates;
    }

    public void setRequestingLocationUpdates(boolean request) {
        mRequestingLocationUpdates = request;
    }
}
