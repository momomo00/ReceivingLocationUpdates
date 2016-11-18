package momomo00.receivinglocationupdates;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by songo_000 on 2016/11/07.
 */

public class DisplayLocation
    implements MyLocationUpdate.MyLocationUpdateListener {

    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private TextView mLastUpdateTimeTextView;
    private Button  mStartUpdateButton;
    private Button  mStopUpdateButton;

    private MyLocationUpdate    mMyLocationUpdate;

    public DisplayLocation(AppCompatActivity activity) {
        Log.d(MyLog.TAG, "DisplayLocation: DisplayLocation");
        mLastUpdateTimeTextView = (TextView)activity.findViewById(R.id.last_update_time_text);
        mLongitudeTextView = (TextView)activity.findViewById(R.id.longitude_text);
        mLatitudeTextView = (TextView)activity.findViewById(R.id.latitude_text);

        mStartUpdateButton = (Button)activity.findViewById(R.id.start_update_button);
        mStartUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mMyLocationUpdate.getRequestingLocationUpdates()) {
                    mMyLocationUpdate.setRequestingLocationUpdates(true);
                    setButtonsEnableState(false, true);
                    mMyLocationUpdate.startLocationUpdates();
                }
            }
        });

        mStopUpdateButton = (Button)activity.findViewById(R.id.stop_update_button);
        mStopUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMyLocationUpdate.getRequestingLocationUpdates()) {
                    mMyLocationUpdate.setRequestingLocationUpdates(false);
                    setButtonsEnableState(true, false);
                    mMyLocationUpdate.stopLocationUpdates();
                }
            }
        });

        mMyLocationUpdate = null;
    }

    @Override
    public void onLocationUpdate(Location location, Date date) {
        Log.d(MyLog.TAG, "DisplayLocation: onLocationUpdate");
        mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
        mLongitudeTextView.setText(String.valueOf(location.getLongitude()));
        String lastUpdateTime = DateFormat.getInstance().format(date);
        mLastUpdateTimeTextView.setText(lastUpdateTime);
    }

    private void setButtonsEnableState(boolean start, boolean stop) {
        mStartUpdateButton.setEnabled(start);
        mStopUpdateButton.setEnabled(stop);
    }

    public void setMyLocationUpdate(MyLocationUpdate myLocationUpdate) {
        mMyLocationUpdate = myLocationUpdate;
    }
}
