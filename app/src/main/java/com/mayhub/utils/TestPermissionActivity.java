package com.mayhub.utils;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import com.mayhub.utils.activity.PermissionActivity;
import com.mayhub.utils.common.ToastUtils;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/10/21.
 */
public class TestPermissionActivity extends PermissionActivity implements View.OnClickListener{
    @Override
    public int getContentResId() {
        return R.layout.permission;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.permission_request_1).setOnClickListener(this);
        findViewById(R.id.permission_request_2).setOnClickListener(this);
        findViewById(R.id.permission_request_3).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.permission_request_1:
                HashMap<String, String> permissionDoc = new HashMap<>();
                permissionDoc.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "文件读写权限用于存储您的图片，并在下次使用时读取图片进行显示");
                permissionCheckAndRequest(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, permissionDoc, new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.getInstance().showShortToast(getApplicationContext(), "Permission Granted");
                    }
                });
                break;
            case R.id.permission_request_2:
                HashMap<String, String> permissionDoc2 = new HashMap<>();
                permissionDoc2.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "文件读写权限用于存储您的图片，并在下次使用时读取图片进行显示");
                permissionDoc2.put(Manifest.permission.CAMERA, "摄像头权限用于拍摄您感兴趣的照片");
                String requestPermission[] = new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                };
                permissionCheckAndRequest(requestPermission , permissionDoc2, requestPermission, new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.getInstance().showShortToast(getApplicationContext(), "Permission Granted");
                    }
                }, null);
                break;
            case R.id.permission_request_3:
                String requestPermission3[] = new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                };
                permissionRequest(requestPermission3 , new PermissionRequestListener() {
                    @Override
                    public void onRequestEnd(String[] deniedPermissions) {
//                        ToastUtils.getInstance().showShortToast(getApplicationContext(), "onRequestEnd " + deniedPermissions.toString());
                    }

                    @Override
                    public void onAllRequestGrant() {
                        ToastUtils.getInstance().showShortToast(getApplicationContext(), "onAllRequestGrant ");
                    }

                    @Override
                    public void onPartRequestGrant(String[] deniedPermissions, boolean isRequiredGranted) {
                        ToastUtils.getInstance().showShortToast(getApplicationContext(), "onPartRequestGrant " + deniedPermissions.toString());
                    }

                    @Override
                    public void onAllDenied(String[] deniedPermissions) {
                        ToastUtils.getInstance().showShortToast(getApplicationContext(), "onAllDenied " + deniedPermissions.toString());
                    }
                });
                break;
        }
    }
}
