package momomo00.receivinglocationupdates;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by songo_000 on 2016/11/11.
 */

public class MyPermissionChecker {

    private final int REQUEST_CODE_LOCATION_PERMISSION = 1000;

    private Activity mActivity;
    private WhenGrantedListener mWhenGrantedListener;

    public MyPermissionChecker() {
        Log.d(MyLog.TAG, "MyPermissionChecker: MyPermissionChecker");
        mActivity = null;
        mWhenGrantedListener = null;
    }

    public void requestPermissions() {
        Log.d(MyLog.TAG, "MyPermissionChecker: myRequestPermission");
        if(ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            ExecutionWhenGranted();
            return;
        }
        if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(mActivity)
                    .setTitle("パーミッションの追加説明")
                    .setMessage("このアプリは位置情報を必要とします")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(mActivity
                                    , new String[] {Manifest.permission.ACCESS_FINE_LOCATION}
                                    , REQUEST_CODE_LOCATION_PERMISSION);
                        }
                    })
                    .create()
                    .show();
            return;
        }
        ActivityCompat.requestPermissions(mActivity
            , new String[] {Manifest.permission.ACCESS_FINE_LOCATION}
            , REQUEST_CODE_LOCATION_PERMISSION);
    }

    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(MyLog.TAG, "MyPermissionChecker: myOnRequestPermissionsResult");
        boolean result = true;

        switch(requestCode) {
            case REQUEST_CODE_LOCATION_PERMISSION:
                if((grantResults.length == 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    ExecutionWhenGranted();
                    return true;
                }

                if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(mActivity)
                            .setTitle("パーミッション取得エラー")
                            .setMessage("再試行する場合は、再度Requestボタンを押してください")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create()
                            .show();
                } else {
                    new AlertDialog.Builder(mActivity)
                            .setTitle("パーミッション取得エラー")
                            .setMessage("今後は許可しないが選択されました。アプリ設定＞権限をチェックしてください（権限をON/OFFすることで状態はリセットされます）")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    openSettings();
                                }
                            })
                            .create()
                            .show();
                }
                break;
            default:
                result = false;
                break;
        }

        return result;
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
        intent.setData(uri);
        mActivity.startActivity(intent);
    }

    public MyPermissionChecker setActivity(Activity activity) {
        mActivity = activity;
        return this;
    }

    public interface WhenGrantedListener {
        void whenGranted();
    }

    public MyPermissionChecker setWhenGrantedListener(WhenGrantedListener whenGrantedListener) {
        mWhenGrantedListener = whenGrantedListener;
        return this;
    }

    private void ExecutionWhenGranted() {
        Log.d(MyLog.TAG, "MyPermissionChecker: ExecutionWhenGranted");

        if(mWhenGrantedListener == null) {
            return;
        }
        mWhenGrantedListener.whenGranted();
    }
}
