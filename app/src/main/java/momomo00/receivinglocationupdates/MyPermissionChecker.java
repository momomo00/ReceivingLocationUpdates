package momomo00.receivinglocationupdates;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * ランタイムパーミッションを管理する
 * Created by songo_000 on 2016/11/11.
 */

public class MyPermissionChecker {

    // パーミッションの要求番号
    private final int REQUEST_CODE_LOCATION_PERMISSION = 1000;

    // アクティビティ
    private Activity mActivity;
    // 許可得られた時の動作
    private WhenGrantedListener mWhenGrantedListener;
    // 許可を得られず「二度と表示しない」を押された場合の動作
    private NotGetPermissionListener mNotGetPermissionListener;

    /**
     * コンストラクタ
     */
    public MyPermissionChecker() {
        Log.d(MyLog.TAG, "MyPermissionChecker: MyPermissionChecker()");
        mActivity = null;
        mWhenGrantedListener = null;
    }

    /**
     * コンストラクタ
     *
     * @param activity アクティビティ
     */
    public MyPermissionChecker(Activity activity) {
        this();
        Log.d(MyLog.TAG, "MyPermissionChecker: MyPermissionChecker(Activity)");
        mActivity = activity;
    }

    /**
     * パーミッションの許可有無を確認
     *
     * @param permission パーミッション
     * @return 許可されている場合はtrue
     */
    public boolean checkPermission(String permission) {
        Log.d(MyLog.TAG, "MyPermissionChecker: checkPermission(String)");

        return checkPermission(mActivity, permission);
    }

    /**
     * パーミッションの許可有無を確認
     *
     * @param context コンテキスト
     * @param permission パーミッション
     * @return 許可されている場合はtrue
     */
    public static boolean checkPermission(Context context, String permission) {
        Log.d(MyLog.TAG, "MyPermissionChecker: checkPermission(Context, String)");

        boolean result = true;

        if(ActivityCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_DENIED) {
            result = false;
        }

        return result;
    }

    /**
     * パーミッションの許可有無を確認した上で許可されていないパーミッションを要求する
     *
     * @param permissions 必要なパーミッション
     */
    public void requestPermissions(String[] permissions) {
        Log.d(MyLog.TAG, "MyPermissionChecker: requestPermissions(String[])");

        List<String> deniedPermissionList = new ArrayList<>();
        boolean requestPermissionsResult = true;

        // パーミッションの許可有無の確認
        for(String permission: permissions) {
            boolean checkPermissionResult = checkPermission(permission);
            if(!checkPermissionResult) {
                deniedPermissionList.add(permission);
                requestPermissionsResult = false;
            }
        }

        // すべて許可されていた場合は登録されている処理を実行
        if(requestPermissionsResult) {
            executionWhenGranted();
            return;
        }

        // パーミッションの要求
        // 二回目以降はダイアログを表示
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

    /**
     * パーミッション要求時のダイアログタイトルを取得
     *
     * @param permission パーミッション
     * @return タイトル
     */
    private String getRequestPermissionDialogTitle(String permission) {
        Log.d(MyLog.TAG, "MyPermissionChecker: getRequestPermissionDialogTitle(String)");
        String title = "unknown";

        boolean result = permission.equals(Manifest.permission.ACCESS_FINE_LOCATION);
        if(result){
            return mActivity.getResources().getString(R.string.request_permission_dialog_title);
        }

        return title;
    }

    /**
     * パーミッション要求時のダイアログメッセージを取得
     *
     * @param permission パーミッション
     * @return メッセージ
     */
    private String getRequestPermissionDialogMessage(String permission) {
        Log.d(MyLog.TAG, "MyPermissionChecker: getRequestPermissionDialogMessage(String)");
        String Message = "unknown";

        boolean result = permission.equals(Manifest.permission.ACCESS_FINE_LOCATION);
        if(result){
            return mActivity.getResources().getString(R.string.request_permission_dialog_message);
        }

        return Message;
    }

    /**
     * パーミッションの要求結果
     *
     * @param requestCode リクエストコード
     * @param permissions 要求したパーミッション
     * @param grantResults 要求結果
     * @return 処理すべきリクエストコードの場合true
     */
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(MyLog.TAG, "MyPermissionChecker: onRequestPermissionsResult(int, String[], int[])");
        boolean result = true;

        switch(requestCode) {
            case REQUEST_CODE_LOCATION_PERMISSION:
                // 要求したパーミッションすべてが許可された場合は登録されている処理を実行
                if(checkGrantResults(grantResults)) {
                    executionWhenGranted();
                    return true;
                }

                // 要求したパーミッションの内、一つでも「今後表示しない」を選択された場合
                // ダイアログを表示したのち登録されたエラー処理に移行する
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

    /**
     * 要求したパーミッションの許可有無を確認
     *
     * @param grantResults 要求結果
     * @return 要求したパーミッションが全て許可されている場合はtrue
     */
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

    /**
     * 要求したパーミッションに許可されなかったものが一つでもある場合の処理
     *
     * @param permissions 要求したパーミッション
     * @return 一つでも要求したパーミッションで「今後表示しない」を選択されていた場合false
     */
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

    /**
     * パーミッションがすべて許可された場合に実行する処理のインターフェース
     */
    public interface WhenGrantedListener {
        void whenGranted();
    }

    /**
     * パーミッションがすべて許可された場合に実行する処理のインターフェースを登録する
     *
     * @param listener リスナー
     * @return 自分
     */
    public MyPermissionChecker setWhenGrantedListener(WhenGrantedListener listener) {
        Log.d(MyLog.TAG, "MyPermissionChecker: setWhenGrantedListener(WhenGrantedListener)");
        mWhenGrantedListener = listener;
        return this;
    }

    /**
     * パーミッションがすべて許可された場合に実行する処理
     */
    private void executionWhenGranted() {
        Log.d(MyLog.TAG, "MyPermissionChecker: executionWhenGranted()");

        if(mWhenGrantedListener == null) {
            return;
        }
        mWhenGrantedListener.whenGranted();
    }

    /**
     * 「今後表示しない」を設定された場合に実行するインターフェース
     */
    public interface NotGetPermissionListener {
        void notGetPermission();
    }

    /**
     * 「今後表示しない」を設定された場合に実行するインターフェースを登録する
     * @param listener リスナー
     * @return 自分
     */
    public MyPermissionChecker setNotGetPermissionListener(NotGetPermissionListener listener) {
        mNotGetPermissionListener = listener;
        return this;
    }

    /**
     * 「今後表示しない」を設定された場合に実行する処理
     */
    private void executionNotGetPermission() {
        if(mNotGetPermissionListener == null) {
            return;
        }
        mNotGetPermissionListener.notGetPermission();
    }
}
