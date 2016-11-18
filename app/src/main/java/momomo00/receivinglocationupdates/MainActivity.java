package momomo00.receivinglocationupdates;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private DisplayLocation     mDisplayLocation;
    private MyLocationUpdate    mMyLocationUpdate;
    private MyPermissionManager mMyPermissionChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDisplayLocation = new DisplayLocation(this);

        mMyLocationUpdate = new MyLocationUpdate(this);
        mMyLocationUpdate.setMyLocationUpdateListener(mDisplayLocation);

        mMyPermissionChecker = new MyPermissionManager(this)
                .setWhenGrantedListener(mMyLocationUpdate)
                .setNotGetPermissionListener(new MyPermissionManager.NotGetPermissionListener() {
                    @Override
                    public void notGetPermission() {
                        Log.d(MyLog.TAG, "MainActivity: notGetPermission()");
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
        mMyPermissionChecker.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
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
