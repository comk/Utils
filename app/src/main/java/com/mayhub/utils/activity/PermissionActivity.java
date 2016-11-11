package com.mayhub.utils.activity;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.mayhub.utils.common.ToastUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/21.
 */
public abstract class PermissionActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    public interface PermissionRequestListener{
        void onRequestEnd(String deniedPermissions[]);
        void onAllRequestGrant();
        void onPartRequestGrant(String deniedPermissions[], boolean isRequiredGranted);
        void onAllDenied(String deniedPermissions[]);
    }

    private static final int REQUEST_PERMISSION = 0x109;

    private Map<String, String>  mRequestPermissionDoc;
    private String mRequirePermissions[];
    private Runnable mAction;
    private boolean isPermissionRequesting = false;
    private PermissionRequestListener permissionRequestListener;
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION){
            ArrayList<String> deniedPermission = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    deniedPermission.add(permissions[i]);
                }
            }
            if(deniedPermission.size() == 0){
                if(permissionRequestListener != null){
                    permissionRequestListener.onRequestEnd(null);
                    permissionRequestListener.onAllRequestGrant();
                }
                if(mAction != null){
                    mAction.run();
                }
            }else {
                boolean requiredPermissionMiss = false;
                if (mRequirePermissions != null) {
                    for (String permission : mRequirePermissions) {
                        if (deniedPermission.contains(permission)) {
                            if (mRequestPermissionDoc != null) {
                                ToastUtils.getInstance().showShortToast(getApplicationContext(), mRequestPermissionDoc.get(permission));
                            }
                            requiredPermissionMiss = true;
                        }
                    }
                    if (requiredPermissionMiss) {
                        ToastUtils.getInstance().showShortToast(getApplicationContext(), "请同意相关权限或到设置中开启相关权限后再试");
                    } else if (mAction != null) {
                        mAction.run();
                    }
                } else if (mAction != null) {
                    mAction.run();
                }
                if (permissionRequestListener != null) {
                    String[] denied = new String[deniedPermission.size()];
                    deniedPermission.toArray(denied);
                    permissionRequestListener.onRequestEnd(denied);
                    if(deniedPermission.size() == permissions.length) {
                        permissionRequestListener.onAllDenied(permissions);
                    }else{
                        permissionRequestListener.onPartRequestGrant(denied, !requiredPermissionMiss);
                    }
                }
            }
            mRequirePermissions = null;
            mRequestPermissionDoc = null;
            mAction = null;
            isPermissionRequesting = false;
            permissionRequestListener = null;
        }
    }

    public void permissionRequest(String requestPermissions[], PermissionRequestListener permissionRequestListener){
        permissionCheckAndRequest(requestPermissions, null, null, null, permissionRequestListener);
    }

    public void permissionRequest(String requestPermissions[],String requiredPermissions[], PermissionRequestListener permissionRequestListener){
        permissionCheckAndRequest(requestPermissions, null, requiredPermissions, null, permissionRequestListener);
    }

    /**
     * 请求相关的权限，对相关的权限进行说明，并给出执行相关动作的必须权限集合与满足条件后的执行动作
     * @param requestPermissions 请求的权限集合
     * @param requestPermissionDocMap 请求的权限的说明
     * @param requiredPermissions 执行动作的必须权限
     * @param action 满足必须权限集合执行的动作
     */
    public void permissionCheckAndRequest(String requestPermissions[], Map<String, String> requestPermissionDocMap, String requiredPermissions[], Runnable action, PermissionRequestListener permissionRequestListener1){
        if(isPermissionRequesting){
            ToastUtils.getInstance().showShortToast(getApplicationContext(), "已经在申请权限了");
            return;
        }
        permissionRequestListener = permissionRequestListener1;
        isPermissionRequesting = true;
        ArrayList<String> deniedPermission = new ArrayList<>();
        for (String permission : requestPermissions) {
            if(ActivityCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        permission)) {
                    if(requestPermissionDocMap != null) {
                        ToastUtils.getInstance().showLongToast(getApplicationContext(), requestPermissionDocMap.get(permission));
                    }
                }
                deniedPermission.add(permission);
            }
        }
        if(deniedPermission.size() == 0){
            if(permissionRequestListener != null){
                permissionRequestListener.onRequestEnd(requestPermissions);
                permissionRequestListener.onAllRequestGrant();
            }
            if(action != null){
                action.run();
            }
            isPermissionRequesting = false;
        }else {
            mRequestPermissionDoc = requestPermissionDocMap;
            mRequirePermissions = requiredPermissions;
            mAction = action;
            ActivityCompat.requestPermissions(this, deniedPermission.toArray(new String[deniedPermission.size()]), REQUEST_PERMISSION);
        }
    }

    /**
     * 只是申请相关权限，不关心请求的结果
     * @param requestPermissions 需要的权限集合
     */
    public void permissionCheckAndRequest(String requestPermissions[]){
        permissionCheckAndRequest(requestPermissions, null, null, null, null);
    }

    /**
     * 申请相关的权限并在权限申请成功后执行相关方法
     * @param requestPermissions 请求的权限集合
     * @param action 请求完毕执行的动作
     */
    public void permissionCheckAndRequest(String requestPermissions[], Runnable action){
        permissionCheckAndRequest(requestPermissions, null, requestPermissions, action, null);
    }

    /**
     * 申请相关的权限的同时给出相应的说明并在权限申请结束后执行相关方法
     * @param requestPermissions 请求的权限集合
     * @param requestPermissionDocMap 请求的权限的说明
     * @param action 请求完毕执行的动作
     */
    public void permissionCheckAndRequest(String requestPermissions[], Map<String, String> requestPermissionDocMap, Runnable action){
        permissionCheckAndRequest(requestPermissions, requestPermissionDocMap, null, action, null);
    }

}
