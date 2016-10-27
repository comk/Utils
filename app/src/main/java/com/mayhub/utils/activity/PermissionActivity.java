package com.mayhub.utils.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.mayhub.utils.common.ToastUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/21.
 */
public abstract class PermissionActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int REQUEST_PERMISSION = 0x109;

    private Map<String, String>  mRequestPermissionDoc;
    private String mRequirePermissions[];
    private Runnable mAction;
    private boolean isPermissionRequesting = false;
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION){
            ArrayList<String> ungrantedPermission = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    ungrantedPermission.add(permissions[i]);
                }
            }
            if(ungrantedPermission.size() == 0){
                if(mAction != null){
                    mAction.run();
                }
            }else if(mRequirePermissions != null){
                boolean requiredPermissionMiss = false;
                for (String permission : mRequirePermissions) {
                    if(ungrantedPermission.contains(permission)){
                        if(mRequestPermissionDoc != null) {
                            ToastUtils.getInstance().showShortToast(getApplicationContext(), mRequestPermissionDoc.get(permission));
                        }
                        requiredPermissionMiss = true;
                    }
                }
                if(requiredPermissionMiss){
                    ToastUtils.getInstance().showShortToast(getApplicationContext(), "请同意相关权限或到设置中开启相关权限后再试");
                }else if(mAction != null){
                    mAction.run();
                }
            }else if(mAction != null){
                mAction.run();
            }
            mRequirePermissions = null;
            mRequestPermissionDoc = null;
            mAction = null;
            isPermissionRequesting = false;
        }
    }

    /**
     * 请求相关的权限，对相关的权限进行说明，并给出执行相关动作的必须权限集合与满足条件后的执行动作
     * @param requestPermissions 请求的权限集合
     * @param requestPermissionDocMap 请求的权限的说明
     * @param requiredPermissions 执行动作的必须权限
     * @param action 满足必须权限集合执行的动作
     */
    public void permissionCheckAndRequest(String requestPermissions[], Map<String, String> requestPermissionDocMap, String requiredPermissions[], Runnable action){
        if(isPermissionRequesting){
            ToastUtils.getInstance().showShortToast(getApplicationContext(), "已经在申请权限了");
            return;
        }
        isPermissionRequesting = true;
        ArrayList<String> ungrantedPermission = new ArrayList<>();
        for (String permission : requestPermissions) {
            if(ActivityCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        permission)) {
                    if(requestPermissionDocMap != null) {
                        ToastUtils.getInstance().showLongToast(getApplicationContext(), requestPermissionDocMap.get(permission));
                    }
                }
                ungrantedPermission.add(permission);
            }
        }
        if(ungrantedPermission.size() == 0){
            if(action != null){
                action.run();
            }
            isPermissionRequesting = false;
        }else {
            mRequestPermissionDoc = requestPermissionDocMap;
            mRequirePermissions = requiredPermissions;
            mAction = action;
            ActivityCompat.requestPermissions(this, ungrantedPermission.toArray(new String[ungrantedPermission.size()]), REQUEST_PERMISSION);
        }
    }

    /**
     * 只是申请相关权限，不关心请求的结果
     * @param requestPermissions 需要的权限集合
     */
    public void permissionCheckAndRequest(String requestPermissions[]){
        permissionCheckAndRequest(requestPermissions, null, null, null);
    }

    /**
     * 申请相关的权限并在权限申请成功后执行相关方法
     * @param requestPermissions 请求的权限集合
     * @param action 请求完毕执行的动作
     */
    public void permissionCheckAndRequest(String requestPermissions[], Runnable action){
        permissionCheckAndRequest(requestPermissions, null, requestPermissions, action);
    }

    /**
     * 申请相关的权限的同时给出相应的说明并在权限申请成功后执行相关方法
     * @param requestPermissions 请求的权限集合
     * @param requestPermissionDocMap 请求的权限的说明
     * @param action 请求完毕执行的动作
     */
    public void permissionCheckAndRequest(String requestPermissions[], Map<String, String> requestPermissionDocMap, Runnable action){
        permissionCheckAndRequest(requestPermissions, requestPermissionDocMap, null, action);
    }

}
