package hasan.mohamed.shehata.myapplication.types;

public interface PermissionRequestProvider {
    public void requireInternetPermission(PermissionRequestCallbacks permissionRequestCallbacks);
    public void requireRecordPermission(PermissionRequestCallbacks permissionRequestCallbacks);
    public void requireStoragePermissions(PermissionRequestCallbacks permissionRequestCallbacks);
    public void requireCameraPermission(PermissionRequestCallbacks permissionRequestCallbacks);
}
