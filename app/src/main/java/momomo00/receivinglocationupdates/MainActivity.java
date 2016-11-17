package momomo00.receivinglocationupdates;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private DisplayLocation     mDisplayLocation;
    private MyLocationUpdate    mMyLocationUpdate;
    private MyPermissionChecker mMyPermissionChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDisplayLocation = new DisplayLocation(this);

        mMyLocationUpdate = new MyLocationUpdate(this);
        mMyLocationUpdate.setMyLocationUpdateListener(mDisplayLocation);

        mMyPermissionChecker = new MyPermissionChecker(this)
                .setWhenGrantedListener(mMyLocationUpdate);
        mMyPermissionChecker.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION});
    }

    @Override
    protected void onStart() {
        Log.d(MyLog.TAG, "MainActivity: onStart");

        super.onStart();
   }

    @Override
    protected void onStop() {
        Log.d(MyLog.TAG, "MainActivity: onStop");

        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(MyLog.TAG, "MainActivity: onPause");

        super.onPause();
        mMyLocationUpdate.stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        Log.d(MyLog.TAG, "MainActivity: onResume");

        super.onResume();
        mMyLocationUpdate.restartLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(MyLog.TAG, "MainActivity: onRequestPermissionsResult");

        if(!mMyPermissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
