package com.jerry_mar.mvc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;

public class PermissionController extends DataController {
    public static final String[] STORAGE = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final int STORAGE_CODE = 1;
    public static final String[] LOCATION = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    public static final int LOCATION_CODE = 2;
    public static final String[] MICROPHONE = new String[]{
            Manifest.permission.RECORD_AUDIO
    };
    public static final int MICROPHONE_CODE = 3;
    public static final String[] CAMERA = new String[]{
            Manifest.permission.CAMERA,
    };
    public static final int CAMERA_CODE = 4;
    public static final String[] SENSORS = new String[]{
            Manifest.permission.BODY_SENSORS,
    };
    public static final int SENSORS_CODE = 5;
    public static final String[] CALENDAR = new String[]{
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
    };
    public static final int CALENDAR_CODE = 6;
    public static final String[] CONTACTS = new String[]{
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS
    };
    public static final int CONTACTS_CODE = 7;
    public static final String[] PHONE = new String[]{
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS
    };
    public static final int PHONE_CODE = 8;
    public static final String[] SMS = new String[]{
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS
    };
    public static final int SMS_CODE = 9;

    public boolean checkPermission(String permission) {
        return checkPermission(permission, Process.myPid(),
                Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkPermission(String[] permissiones) {
        boolean result = false;
        int size = permissiones.length;
        for (int i = 0; i < size; i++) {
            if (checkPermission(permissiones[i])) {
                result = true;
                break;
            }
        }
        return result;
    }

    public void requestPermission(String[] permissiones, int requestCode) {
        if (checkPermission(permissiones)) {
            onActivityResult(requestCode, RESULT_OK, null);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissiones, requestCode);
            } else {
                onActivityResult(requestCode, RESULT_OK, null);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] result) {
        boolean allow = false;
        for (int code : result) {
            if (code == PackageManager.PERMISSION_GRANTED) {
                allow = true;
                break;
            }
        }
        if (allow) {
            onActivityResult(requestCode + RESULT_OK,
                    0, null);
        } else {
            onActivityResult(requestCode, 0, null);
        }
    }
}
