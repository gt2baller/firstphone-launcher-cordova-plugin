package com.homedepot.cordova.plugin.firstphone.launcher;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import android.view.inputmethod.InputMethodManager;
import com.homedepot.ngfp.session.service.IRemoteSessionService;
import com.homedepot.ngfp.session.support.SharedDeviceSessionData;
import com.homedepot.ngfp.session.support.SharedUserSessionData;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FirstPhoneLauncherPlugin extends CordovaPlugin {

    private static final String PLUGIN_ACTION_START_APP_SESSION = "startApp";
    private static final String PLUGIN_ACTION_GET_DEVICE_SESSION = "getDevice";
    private static final String PLUGIN_ACTION_GET_USER_SESSION = "getUser";
    private static final String PLUGIN_ACTION_GET_SETTINGS = "getSettings";
    private static final String PLUGIN_ACTION_EXIT = "exit";
    private static final String PLUGIN_ACTION_GOTO_HOME = "goHome";
    private static final String PLUGIN_ACTION_DIAL = "dial";
    private static final String PLUGIN_ACTION_SHOW_KEYBOARD = "showKeyboard";
    private static final String PLUGIN_ACTION_HIDE_KEYBOARD = "hideKeyboard";

    private static final String INTENT_ACTION_REQUEST_APP_SHUTDOWN = "com.homedepot.ACTION_REQUEST_APP_SHUTDOWN";
    private static final String INTENT_EXTRA_DEVICE_SESSION = "com.homedepot.EXTRA_DEVICE_SESSION";
    private static final String INTENT_EXTRA_USER_SESSION = "com.homedepot.EXTRA_USER_SESSION";

    public static final String ACTION_USER_SESSION_CHANGED = "com.homedepot.ngfp.ACTION_USER_SESSION_CHANGED";
    public static final String ACTION_DEVICE_SESSION_CHANGED = "com.homedepot.ngfp.ACTION_DEVICE_SESSION_CHANGED";

    private SharedDeviceSessionData cachedDeviceSession = null;
    private SharedUserSessionData cachedUserSession = null;
    private Activity activity = null;
    private Context appContext = null;

    private boolean isBound = false;
    private IRemoteSessionService remoteSessionService = null;

    private static final String TAG = FirstPhoneLauncherPlugin.class.getSimpleName();


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        activity = cordova.getActivity();
        appContext = cordova.getActivity().getApplicationContext();

        Intent i = activity.getIntent();

        if(i != null) {
            //though I wouldn't expect init to be called unless this activity had just been created,
            //there's a chance the activity no longer has access to the start intent.
            if(i.getParcelableExtra(INTENT_EXTRA_DEVICE_SESSION) != null) {
                cachedDeviceSession = i.getParcelableExtra(INTENT_EXTRA_DEVICE_SESSION);
                Log.i(TAG, "Device session data provided in start intent. Caching provided object.");
            } else {
                Log.w(TAG, "Device session data not provided in start intent.");
            }

            if(i.getParcelableExtra(INTENT_EXTRA_USER_SESSION) != null) {
                cachedUserSession = i.getParcelableExtra(INTENT_EXTRA_USER_SESSION);
                Log.i(TAG, "User session data provided in start intent. Caching provided object.");
            } else {
                Log.i(TAG, "User session data not provided in start intent.");
            }
        }

        activity.registerReceiver(appShutdownReceiver, new IntentFilter(INTENT_ACTION_REQUEST_APP_SHUTDOWN));


        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DEVICE_SESSION_CHANGED);
        filter.addAction(ACTION_USER_SESSION_CHANGED);
        activity.registerReceiver(sessionChangedReceiver, filter);

        isBound = createServiceIntent();

        if(isBound && remoteSessionService == null) {
            //Since init gets called just before the first method call, let's try to make sure the
            //service has time for binding.
            try {
                Thread.sleep(250);
            } catch(InterruptedException ex) {
                Log.e(TAG, "Interrupted while waiting for service binding.");
            }
        }

    }


    /**
     * Lollipop and above, bound service must be started with an explicit intent
     * @return boolean result of bound service connection.
     */
    private boolean createServiceIntent() {

        boolean result = false;

        if (appContext.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.LOLLIPOP) {
            Intent explicitIntent = new Intent(IRemoteSessionService.class.getName());
            ResolveInfo info = appContext.getPackageManager().resolveService(explicitIntent, 0);
            explicitIntent.setComponent(new ComponentName(info.serviceInfo.packageName,info.serviceInfo.name));
            result = appContext.bindService(explicitIntent, sessionServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            result = activity.bindService(new Intent(IRemoteSessionService.class.getName()),
                    sessionServiceConnection, Context.BIND_AUTO_CREATE);
            Log.w(TAG, "Implicit intents with startService are not safe");
        }

        return result;
    }

    @Override
    public void onDestroy() {
        try {
            activity.unregisterReceiver(appShutdownReceiver);
        } catch(Exception ex) {
            Log.e(TAG, "Exception during unregistration: " + ex.getMessage(), ex);
        }

        try {
            activity.unregisterReceiver(sessionChangedReceiver);
        } catch(Exception ex) {
            Log.e(TAG, "Exception during unregistration: " + ex.getMessage(), ex);
        }

        try {
            activity.unbindService(sessionServiceConnection);
        } catch(Exception ex) {
            Log.e(TAG, "Exception while unbinding from service: " + ex.getMessage(), ex);
        }

        super.onDestroy();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if(PLUGIN_ACTION_START_APP_SESSION.equals(action)) {
                return handleStartApp(args, callbackContext);
            } else if(PLUGIN_ACTION_GET_DEVICE_SESSION.equals(action)) {
                return handleGetDevice(callbackContext);
            } else if(PLUGIN_ACTION_GET_USER_SESSION.equals(action)) {
                return handleGetUser(callbackContext);
            } else if(PLUGIN_ACTION_GET_SETTINGS.equals(action)) {
                return handleGetSettings(callbackContext);
            } else if(PLUGIN_ACTION_DIAL.equals(action)) {
                String number = "";
                if(args != null && args.length() > 0) {
                    number = String.valueOf(args.get(0));
                }
                return handleDial(number, callbackContext);
            } else if(PLUGIN_ACTION_EXIT.equals(action)) {
                return handleExit(callbackContext);
            } else if(PLUGIN_ACTION_GOTO_HOME.equals(action)) {
                return handleNavigateHome(callbackContext);
            } else if (PLUGIN_ACTION_SHOW_KEYBOARD.equals(action)){
                return handleShowKeyboard(callbackContext);
            } else if (PLUGIN_ACTION_HIDE_KEYBOARD.equals(action)){
                return handleHideKeyboard(callbackContext);
            } else {
                PluginResult result = new PluginResult(PluginResult.Status.INVALID_ACTION);
                if(!result.getKeepCallback()) {
                    callbackContext.sendPluginResult(result);
                }

                return false;
            }
        } catch(Exception ex) {
            callbackContext.error("Exception: " + ex.getMessage());
            return false;
        }
    }

    private boolean handleShowKeyboard(CallbackContext callbackContext){
        InputMethodManager mgr = (InputMethodManager) cordova.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(webView.getView(), InputMethodManager.SHOW_IMPLICIT);

        ((InputMethodManager) cordova.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(webView.getView(), 0);

        return true;
    }

    private boolean handleHideKeyboard(CallbackContext callbackContext){
        InputMethodManager mgr = (InputMethodManager) cordova.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(webView.getView().getWindowToken(), 0);

        return true;
    }

    /**
     * Receives Packagename and DeepLink Parameters and Opens up the App with Extras
     *
     * via the callback context.
     *
     * @param args
     * @param callbackContext
     * @return
     */

    private synchronized boolean handleStartApp(JSONArray args, CallbackContext callback) {
        String com_name = null;
        String activity = null;
        Intent LaunchIntent;

        try {
            if (args.get(0) instanceof JSONArray) {
                JSONObject jsonObject = ((JSONArray) args.get(0)).getJSONObject(0);
                com_name = ((JSONArray) args.get(0)).getJSONObject(0).getString("packageName");
            } else {
                com_name = args.getString(0);
                System.out.println("PackageName" + com_name);
            }

            LaunchIntent = this.cordova.getActivity().getPackageManager().getLaunchIntentForPackage(com_name);

            /**
             * put arguments
             */
            if(args.length() > 1) {
                JSONArray params = args.getJSONArray(1);
                JSONObject key_value;
                String key;
                String value;

                for(int i = 0; i < params.length(); i++) {
                    if (params.get(i) instanceof JSONObject) {
                        Iterator<String> iter = ((JSONObject) params.get(i)).keys();
                        while (iter.hasNext()) {
                            key = iter.next();
                            try {
                                value = params.getJSONObject(i).getString(key);
                                LaunchIntent.putExtra(key, value);
                                System.out.println("Key: " + key + "; Value: " + value);
                            } catch (JSONException e) {
                                callback.error("json params: " + e.toString());
                            }
                        }
                    }
                    else {
                        LaunchIntent.setData(Uri.parse(params.getString(i)));
                    }
                }
            }

            this.cordova.getActivity().startActivity(LaunchIntent);
            callback.success();

        } catch (JSONException e) {
            callback.error("json: " + e.toString());
        } catch (Exception e) {
            callback.error("intent: " + e.toString());
        }
        return true;
    }

    /**
     * Retrieves device info from the launcher (or looks to cached copy) and returns to the caller
     * via the callback context.
     *
     * @param callbackContext
     * @return
     */
    private synchronized boolean handleGetDevice(CallbackContext callbackContext) {
        PluginResult result = null;
        boolean success = true;

        if(cachedDeviceSession == null) {
            refreshCachedDevice();
        }

        if(cachedDeviceSession == null) {
            success = false;
            result = new PluginResult(PluginResult.Status.ERROR); //Can't use no result here because we should _always_ have a device session (need store # no matter what).
        } else {
            result = new PluginResult(PluginResult.Status.OK, deviceSessionToJson(cachedDeviceSession));
        }

        if(!result.getKeepCallback()) {
            callbackContext.sendPluginResult(result);
        }

        return success;
    }

    /**
     * Retrieves user info from the launcher (or looks to cached copy) and returns to the caller
     * via the callback context.
     *
     * @param callbackContext
     * @return
     */
    private synchronized boolean handleGetUser(CallbackContext callbackContext) {
        PluginResult result = null;
        boolean success = true;

        if(cachedUserSession == null) {
            refreshCachedUser();
        }

        if(cachedUserSession == null) {
            result = new PluginResult(PluginResult.Status.ERROR);
        } else {
            result = new PluginResult(PluginResult.Status.OK, userSessionToJson(cachedUserSession));
        }


        if(!result.getKeepCallback()) {
            callbackContext.sendPluginResult(result);
        }

        return success;
    }

    private synchronized boolean handleGetSettings(CallbackContext callbackContext) {
        PluginResult result = null;
        boolean success = true;

        if(cachedDeviceSession == null) {
            refreshCachedDevice();
        }

        if(cachedUserSession == null) {
            refreshCachedUser();
        }

        if(cachedDeviceSession == null) {
            //If the device session is null, we really have no business being in an application as we don't
            //know store number or other device details.  This equals a failure.
            Log.e(TAG, "Device session is null, unable to return device data to cordova plugin.");
            result = new PluginResult(PluginResult.Status.ERROR);
        } else {
            //Note that user session can be null if the user is not logged in - several apps are available
            //in that state.  We will return a settings object with as much data as possible based on the
            //state of the device.
            Log.i(TAG, "Session data has been retrieved or is cached, returning to cordova plugin.");
            result = new PluginResult(PluginResult.Status.OK,
                    sessionDataToFrameworkSettingsJson(cachedUserSession, cachedDeviceSession));
        }

        if(!result.getKeepCallback()) {
            callbackContext.sendPluginResult(result);
        }

        return success;
    }

    /**
     * Attempts to get device info using the launcher service
     */
    private void refreshCachedDevice() {
        SharedDeviceSessionData deviceSession = deviceSessionFromService();

        //make sure device session isn't empty
        if(validateDeviceSession(deviceSession)) {
            //validated, cache it.
            cachedDeviceSession = deviceSessionFromService();
        } else {
            cachedDeviceSession = null;
        }

    }

    /**
     * Attempts to get user info using the launcher service
     */
    private void refreshCachedUser() {
        SharedUserSessionData userSession = userSessionFromService();

        //make sure user session isn't empty
        if(validateUserSession(userSession)) {
            //validated, cache it.
            cachedUserSession = userSessionFromService();
        } else {
            cachedUserSession = null;
        }
    }

    /**
     * Validate the device session.  The device ID and store number should be present. Note that
     * it is a valid scenario for no store number to exist, though users should not be able to get into
     * applications when this occurs.
     * @param device
     * @return
     */
    private boolean validateDeviceSession(SharedDeviceSessionData device) {
        if(device != null && device.getDeviceId() != null && !device.getDeviceId().isEmpty() &&
                device.getStoreNumber() != null && !device.getStoreNumber().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Validate the user session - the user ID, at the very least, should be present.
     * @param user
     * @return
     */
    private boolean validateUserSession(SharedUserSessionData user) {
        if(user != null && user.getUserId() != null && !user.getUserId().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Forces the application / activity to exit at request of the caller.
     * @param callbackContext
     * @return
     */
    private boolean handleExit(CallbackContext callbackContext) {
        PluginResult result = new PluginResult(PluginResult.Status.OK);
        callbackContext.sendPluginResult(result);

        activity.finish();

        return true;
    }


    /**
     * Forces the application to close without exiting and show home screen.
     * @param callbackContext
     * @return
     */
    private boolean handleNavigateHome(CallbackContext callbackContext) {

        Log.i(TAG, "handleNavigateHome method called");        

        try {
          Intent i = new Intent(Intent.ACTION_MAIN);
          i.addCategory(Intent.CATEGORY_HOME);
          activity.startActivity(i);
        } catch (Exception e) {
          Log.e(TAG, "Exception occurred: ".concat(e.getMessage()));
          return false;
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK);
        callbackContext.sendPluginResult(result);

        return true;
    }
    
    /**
     * Brings up the default system dialer with the number specified already entered.
     * @param phoneNumber
     * @param callbackContext
     * @return
     */
    private boolean handleDial(String phoneNumber, CallbackContext callbackContext) {
        Intent i = new Intent(Intent.ACTION_DIAL);
        i.setData(Uri.parse(phoneNumber));
        activity.startActivity(i);

        PluginResult result = new PluginResult(PluginResult.Status.OK);
        callbackContext.sendPluginResult(result);

        return true;
    }

    /**
     * Converts the device session object cached in this class to a JSON string.  JSON Fields here will
     * match the field names on the input POJO - these may not necessarily match the object structure
     * and format expected by the HTML5 application framework.
     * @return
     */
    private JSONObject deviceSessionToJson(SharedDeviceSessionData device) {
        JSONObject asJson = new JSONObject();
        if(device != null) {
            try {
                asJson.put("storeNumber", device.getStoreNumber());
                asJson.put("extensionNumber", device.getExtensionNumber());
                asJson.put("deviceId", device.getDeviceId());
                asJson.put("deviceModelNumber", device.getDeviceModelNumber());
                asJson.put("ipAddress", device.getIpAddress());
                asJson.put("locale", device.getLocale());
            } catch(JSONException ex) {
                Log.e(TAG, "Unable to convert device session object to JSON.", ex);
            }
        }

        return asJson;
    }

    /**
     * Converts the user session object cached in this class to a JSON string. JSON Fields here will
     * match the field names on the input POJO - these may not necessarily match the object structure
     * and format expected by the HTML5 application framework.
     * @return
     */
    private JSONObject userSessionToJson(SharedUserSessionData user) {
        JSONObject asJson = new JSONObject();
        if(user != null) {
            try {
                asJson.put("departmentNumber", user.getDepartmentNumber());
                asJson.put("firstName", user.getFirstName());
                asJson.put("middleName", user.getMiddleName());
                asJson.put("lastName", user.getLastName());
                asJson.put("thdSsoToken", user.getThdSsoToken());

                if(user.getLdapGroups() != null) {
                    JSONArray ldapGroupArray = new JSONArray();
                    for(String group : user.getLdapGroups()) {
                        ldapGroupArray.put(group);
                    }
                    asJson.put("ldapGroups", ldapGroupArray);
                }

                asJson.put("locationNumber", user.getLocationNumber());
                asJson.put("locationType", user.getLocationType());
                asJson.put("associateUserID", user.getUserId());
                asJson.put("userType", user.getUserType());
                asJson.put("imsUserLevel", user.getImsUserLevel());
            } catch(JSONException ex) {
                Log.e(TAG, "Unable to convert user session object to JSON.", ex);
                asJson = null;
            }
        }

        return asJson;
    }

    /**
     * Creates JSON that can be used to overlay the static device settings loaded from configuration
     * in the HTML5 framework.  The object structure and field names here should match the settings
     * variable used throughout the HTML5 applications.  Not all data within the user and device will
     * be returned.
     *
     * @param userData
     * @param deviceData
     * @return
     */
    private JSONObject sessionDataToFrameworkSettingsJson(SharedUserSessionData userData,
                                                          SharedDeviceSessionData deviceData) {
        JSONObject settings = new JSONObject();
        JSONObject location = new JSONObject();
        JSONObject device = new JSONObject();
        JSONObject services = new JSONObject();
        JSONObject user = new JSONObject();

        String estimatorServiceUri = null;

        try {
            //should never be null but check anyway
            if(deviceData != null) {
                //.putOpt() will only add the field if the value is not null.
                device.putOpt("name", deviceData.getDeviceModelNumber());
                device.putOpt("version", deviceData.getLauncherVersion());
                device.putOpt("languageCode", deviceData.getLocale());
                device.putOpt("isQA", deviceData.isQa());

                if(deviceData.getStoreNumber() != null) {
                    String isp = "st" + deviceData.getStoreNumber();
                    location.put("locationName", "Store " + deviceData.getStoreNumber());
                    location.put("storeNumber", deviceData.getStoreNumber());
                    location.put("storeServer", "http://" + isp + ".homedepot.com");

                    services.put("rootUrl", "http://" + isp + ".homedepot.com");

                    estimatorServiceUri = "http://" + isp + ".homedepot.com:12100/ECommProxy/rs/calculatorService/";
                }
            }

            if(userData != null) {
                user.putOpt("associateUserID", userData.getUserId());
                user.putOpt("userType", userData.getUserType());
                user.putOpt("imsUserLevel", userData.getImsUserLevel());

                if(userData.getLdapGroups() != null) {
                    JSONArray ldapGroupArray = new JSONArray();
                    for(String group : userData.getLdapGroups()) {
                        ldapGroupArray.put(group);
                    }
                    user.put("ldapGroups", ldapGroupArray);
                }
            }

            settings.putOpt("device", device);
            settings.putOpt("user", user);
            settings.putOpt("location", location);
            settings.putOpt("services", services);
            settings.putOpt("estimatorServiceUri", estimatorServiceUri);
        } catch(JSONException jex) {
            Log.e(TAG, "Error writing objects to JSON, unable to return session object.", jex);
        }

        return settings;
    }

    /**
     * Attempts to retrieve device session from AIDL service. Returns null if an exception occurs or
     * service is not bound / available.
     * @return
     */
    private SharedDeviceSessionData deviceSessionFromService() {
        SharedDeviceSessionData data = null;
        if(remoteSessionService != null) {
            try {
                data = remoteSessionService.getDeviceData();
            } catch(RemoteException ex) {
                Log.e(TAG, "Remote exception thrown while attempting to retrieve device data from AIDL service.", ex);
                data = null;
            }
        } else {
            Log.e(TAG, "Attempt to retrieve device data when session service null (not bound/available).");
            data = null;
        }

        return data;
    }

    /**
     * Attempts to retrieve user session from AIDL service. Returns null if an exception occurs or
     * service is not bound / available.
     * @return
     */
    private SharedUserSessionData userSessionFromService() {
        SharedUserSessionData data = null;
        if(remoteSessionService != null) {
            try {
                data = remoteSessionService.getUserData();
            } catch(RemoteException ex) {
                Log.e(TAG, "Remote exception thrown while attempting to retrieve user data from AIDL service.", ex);
                data = null;
            }
        } else {
            Log.e(TAG, "Attempt to retrieve user data when session service null (not bound/available).");
            data = null;
        }

        return data;
    }

    private final BroadcastReceiver appShutdownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Application shutdown request received.  Calling 'finish()' on Phonegap activity.");

            if(activity != null) {
                activity.finish();
            } else {
                Log.e(TAG, "Activity is null when attempting to shutdown.  Unable to call finish()");
            }
        }
    };

    private final BroadcastReceiver sessionChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Make sure we clear the cache when the session has changed.  This will force a pull from
            //the Launcher IPC service next time an app requests session data.  Note that an app's
            //process should be killed as well for most session change events, so that would effectively
            //force a re-retrieval as well.
            cachedDeviceSession = null;
            cachedUserSession = null;
        }
    };

    private ServiceConnection sessionServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteSessionService = IRemoteSessionService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            remoteSessionService = null;
        }
    };
}
