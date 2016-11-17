package momomo00.receivinglocationupdates;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
        , MyPermissionChecker.WhenGrantedListener
        , LocationListener {

    private final static int UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private final static int FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private MyLocationUpdateListener    mMyLocationUpdateListener;

    private boolean mRequestingLocationUpdates;
    private boolean mConnectionFlag;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Context mContext;

    public MyLocationUpdate(Context context) {
        Log.d(MyLog.TAG, "MyLocationUpdate: MyLocationUpdate");
        mContext = context;
        mMyLocationUpdateListener = null;
        mRequestingLocationUpdates = false;

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
        if(!mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(MyLog.TAG, "MyLocationUpdate: onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(MyLog.TAG, "MyLocationUpdate: onConnectionSuspended");
    }

    private void startLocationUpdates() {
        Log.d(MyLog.TAG, "MyLocationUpdate: startLocationUpdates");
//        if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
        boolean result
                = MyPermissionChecker.checkPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if(!result) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(MyLog.TAG, "MyLocationUpdate: onLocationChanged");
        executionOnLocationUpdate(location, new Date());
    }

    public void stopLocationUpdates() {
        Log.d(MyLog.TAG, "MyLocationUpdate: stopLocationUpdates");
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    public void restartLocationUpdates() {
        Log.d(MyLog.TAG, "MyLocationUpdate: restartLocationUpdates");
        if(mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    public interface MyLocationUpdateListener {
        void onLocationUpdate(Location location, Date date);
    }

    public void setMyLocationUpdateListener(MyLocationUpdateListener listener) {
        Log.d(MyLog.TAG, "MyLocationUpdate: setMyLocationUpdateListener");
        mMyLocationUpdateListener = listener;
    }

    public void executionOnLocationUpdate(Location location, Date date) {
        if(mMyLocationUpdateListener == null) {
            return;
        }
        mMyLocationUpdateListener.onLocationUpdate(location, date);
    }

    @Override
    public void whenGranted() {
        Log.d(MyLog.TAG, "MyLocationUpdate: whenGranted");
        if(mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }
}
