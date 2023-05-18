package com.luck.picture.lib;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.luck.picture.lib.basic.PictureCommonFragment;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.manager.SelectedManager;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.permissions.PermissionConfig;
import com.luck.picture.lib.permissions.PermissionResultCallback;
import com.luck.picture.lib.utils.SdkVersionUtils;
import com.luck.picture.lib.utils.ToastUtils;
import com.permissionx.guolindev.Permission;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.ForwardScope;

import java.util.List;

/**
 * @author：luck
 * @date：2021/11/22 2:26 下午
 * @describe：PictureOnlyCameraFragment
 */
public class PictureOnlyCameraFragment extends PictureCommonFragment {
    public static final String TAG = PictureOnlyCameraFragment.class.getSimpleName();

    public static PictureOnlyCameraFragment newInstance() {
        return new PictureOnlyCameraFragment();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getResourceId() {
        return R.layout.ps_empty;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 这里只有非内存回收状态下才走，否则当内存不足Fragment被回收后会重复执行
        if (savedInstanceState == null) {
            if (SdkVersionUtils.isQ()) {
                openSelectedCamera();
            } else {
                String[] writePermissionArray = new String[]{Permission.WRITE_EXTERNAL_STORAGE};
//                PermissionChecker.getInstance().requestPermissions(this, writePermissionArray, new PermissionResultCallback() {
//                    @Override
//                    public void onGranted() {
//                        openSelectedCamera();
//                    }
//
//                    @Override
//                    public void onDenied() {
//                        handlePermissionDenied(writePermissionArray);
//                    }
//                });

                PermissionX.init(this)
                        .permissions(writePermissionArray)
                        .onExplainRequestReason((scope, deniedList, beforeRequest) -> {
                            //TODO 接口获取解释文案
                            if(!beforeRequest) {
                                String message = "需要访外部存储权限，缺少外部存储权限可能会导致该功能无法使用";
                                scope.showRequestReasonDialog(deniedList, message, "同意", "拒绝");
                            }
                        })
                        .onForwardToSettings((scope, deniedList) -> {
                            //TODO 接口获取跳转文案
                            String message = "需要前往\"设置\"页中开启下列权限";
                            scope.showForwardToSettingsDialog(deniedList, message, "同意", "拒绝");
                        })
                        .request((allGranted, grantedList, deniedList) -> {
                            if(allGranted) {
                               openSelectedCamera();
                            }else {
                                handlePermissionDenied(writePermissionArray);
                            }
                        });
            }
        }
    }

    @Override
    public void dispatchCameraMediaResult(LocalMedia media) {
        int selectResultCode = confirmSelect(media, false);
        if (selectResultCode == SelectedManager.ADD_SUCCESS) {
            dispatchTransformResult();
        } else {
            onKeyBackFragmentFinish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            onKeyBackFragmentFinish();
        }
    }

    @Override
    public void handlePermissionSettingResult(String[] permissions) {
        boolean isHasPermissions;
        if (PictureSelectionConfig.onPermissionsEventListener != null) {
            isHasPermissions = PictureSelectionConfig.onPermissionsEventListener
                    .hasPermissions(this, permissions);
        } else {
            isHasPermissions = PermissionChecker.isCheckCamera(getContext());
            if (SdkVersionUtils.isQ()) {

            } else {
                isHasPermissions = PermissionChecker.isCheckWriteExternalStorage(getContext());
            }
        }
        if (isHasPermissions) {
            openSelectedCamera();
        } else {
            defaultHandlePermissionDenied(permissions);
        }
        PermissionConfig.CURRENT_REQUEST_PERMISSION = new String[]{};
    }

    @Override
    protected void defaultHandlePermissionDenied(String[] permissionArray) {
        if (!PermissionChecker.isCheckCamera(getContext())) {
            ToastUtils.showToast(getContext(), getString(R.string.ps_camera));
        } else {
            if (!PermissionChecker.isCheckWriteExternalStorage(getContext())) {
                ToastUtils.showToast(getContext(), getString(R.string.ps_jurisdiction));
            }
        }
        onKeyBackFragmentFinish();
    }
}
