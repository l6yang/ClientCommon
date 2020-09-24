package com.loyal.client.impl;

import androidx.annotation.NonNull;

/**
 * 多项权限申请
 */
public interface MultiplePermissionsListener {
    /**
     * if(successful){
     * // `singlePermission.name` is granted !
     * //成功获取权限 do something
     * }else if(shouldShow){
     * <p>
     * //本次拒绝，下次接着弹窗提示请求
     * }else{
     * //权限申请被拒，下次也不会弹窗提示
     * }
     *
     * @param permissionName 权限名称
     * @param successful     是否成功授予权限，true:可进行下一步操作；false：
     * @param shouldShow     true：本次拒绝，下次请求时；false：请求权限失败
     */
    void onMultiplePermissions(@NonNull String permissionName, boolean successful, boolean shouldShow);
}
