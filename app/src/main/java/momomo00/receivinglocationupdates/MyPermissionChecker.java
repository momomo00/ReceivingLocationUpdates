package momomo00.receivinglocationupdates;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by songo_000 on 2016/11/11.
 */

public class MyPermissionChecker {

    private final int REQUEST_CODE_LOCATION_PERMISSION = 1000;

    private Activity mActivity;
    private WhenGrantedListener mWhenGrantedListener;
    private NotGetPermissionListener mNotGetPermissionListener;

    public MyPermissionChecker() {
        Log.d(MyLog.TAG, "MyPermissionChecker: MyPermissionChecker()");
        mActivity = null;
        mWhenGrantedListener = null;
    }

    public MyPermissionChecker(Activity activity) {
        this();
        Log.d(MyLog.TAG, "MyPermissionChecker: MyPermissionChecker(Activity)");
        mActivity = activity;
    }

    public boolean checkPermission(String permission) {
        Log.d(MyLog.TAG, "MyPermissionChecker: checkPermission(String)");

        return checkPermission(mActivity, permission);
    }

    public static boolean checkPermission(Context context, String permission) {
        Log.d(MyLog.TAG, "MyPermissionChecker: checkPermission(Context, String)");

        boolean result = true;

        if(ActivityCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_DENIED) {
            result = false;
        }

        return result;
    }

    public void requestPermissions(String[] permissions) {
        Log.d(MyLog.TAG, "MyPermissionChecker: requestPermissions(String[])");

        List<String> deniedPermissionList = new ArrayList<>();
        boolean requestPermissionsResult = true;

        for(String permission: permissions) {
            boolean checkPermissionResult = checkPermission(permission);
            if(!checkPermissionResult) {
                deniedPermissionList.add(permission);
                requestPermissionsResult = false;
            }
        }

        if(requestPermissionsResult) {
            executionWhenGranted();
            return;
        }

        String[] deniedPermissions = deniedPermissionList.toArray(new String[0]);
        for(String deniedPermission: deniedPermissions) {
            boolean rationaleResult = ActivityCompat.shouldShowRequestPermissionRationale(mActivity, deniedPermission);
            if(rationaleResult) {
                new AlertDialog.Builder(mActivity)
                        .setTitle(getRequestPermissionDialogTitle(deniedPermission))
                        .setMessage(getRequestPermissionDialogMessage(deniedPermission))
                        .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create()
                        .show();
            }
        }

        ActivityCompat.requestPermissions(mActivity
            , deniedPermissions
            , REQUEST_CODE_LOCATION_PERMISSION);
    }

    private String getRequestPermissionDialogTitle(String permission) {
        Log.d(MyLog.TAG, "MyPermissionChecker: getRequestPermissionDialogTitle(String)");
        String title = "unknown";

        boolean result = permission.equals(Manifest.permission.ACCESS_FINE_LOCATION);
        if(result){
            return mActivity.getResources().getString(R.string.request_permission_dialog_title);
        }

        return title;
    }

    private String getRequestPermissionDialogMessage(String permission) {
        Log.d(MyLog.TAG, "MyPermissionChecker: getRequestPermissionDialogMessage(String)");
        String Message = "unknown";

        boolean result = permission.equals(Manifest.permission.ACCESS_FINE_LOCATION);
        if(result){
            return mActivity.getResources().getString(R.string.request_permission_dialog_message);
        }

        return Message;
    }

    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(MyLog.TAG, "MyPermissionChecker: onRequestPermissionsResult(int, String[], int[])");
        boolean result = true;

        switch(requestCode) {
            case REQUEST_CODE_LOCATION_PERMISSION:
                if(checkGrantResults(grantResults)) {
                    executionWhenGranted();
                    return true;
                }

                if(!checkRequestPermissionsResult(permissions)) {
                    new AlertDialog.Builder(mActivity)
                            .setTitle("パーミッション取得エラー")
                            .setMessage("今後は許可しないが選択されました。アプリ設定＞権限をチェックしてください（権限をON/OFFすることで状態はリセットされます）")
                            .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    executionNotGetPermission();
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

    private boolean checkGrantResults(int[] grantResults) {
        Log.d(MyLog.TAG, "MyPermissionChecker: checkGrantResults(int[])");

        boolean result = true;
        for(int grantResult: grantResults) {
            if(grantResult != PackageManager.PERMISSION_GRANTED) {
                result = false;
                break;
            }
        }
        return result;
    }

    private boolean checkRequestPermissionsResult(String[] permissions) {
        Log.d(MyLog.TAG, "MyPermissionChecker: checkRequestPermissionsResult(String[])");
        boolean result = true;

        for(String permission: permissions) {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
                result = false;
                break;
            }
        }

        if(result) {
            new AlertDialog.Builder(mActivity)
                    .setTitle("パーミッション取得エラー")
                    .setMessage("再試行する場合は、再度Requestボタンを押してください")
                    .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create()
                    .show();
        }

        return result;
    }

    public interface WhenGrantedListener {
        void whenGranted();
    }

    public MyPermissionChecker setWhenGrantedListener(WhenGrantedListener listener) {
        Log.d(MyLog.TAG, "MyPermissionChecker: setWhenGrantedListener(WhenGrantedListener)");
        mWhenGrantedListener = listener;
        return this;
    }

    private void executionWhenGranted() {
        Log.d(MyLog.TAG, "MyPermissionChecker: executionWhenGranted()");

        if(mWhenGrantedListener == null) {
            return;
        }
        mWhenGrantedListener.whenGranted();
    }

    public interface NotGetPermissionListener {
        void notGetPermission();
    }

    public MyPermissionChecker setNotGetPermissionListener(NotGetPermissionListener listener) {
        mNotGetPermissionListener = listener;
        return this;
    }

    private void executionNotGetPermission() {
        if(mNotGetPermissionListener == null) {
            return;
        }
        mNotGetPermissionListener.notGetPermission();
    }
}
