package momomo00.receivinglocationupdates;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private DisplayLocation     mDisplayLocation;
    private MyLocationUpdate    mMyLocationUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDisplayLocation = new DisplayLocation(this);
        mMyLocationUpdate = new MyLocationUpdate(this);
        mMyLocationUpdate.setMyLocationUpdateListener(mDisplayLocation);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMyLocationUpdate.stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMyLocationUpdate.restartLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
