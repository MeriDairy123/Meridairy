package b2infosoft.milkapp.com.useful;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.appglobal.Constant.AppGooglePlayStoreUrl;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getAppVersionInfo;


/**
 * Created by Amar Suthar on 13-Oct-17.
 */

public class MyApp extends Application {

    public static String TAG = MyApp.class.getSimpleName();
    private static MyApp mInstance;

    public static synchronized MyApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        SessionManager sessionManager=new SessionManager(this);
        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, true);
        String appVerson = getAppVersionInfo(getApplicationContext());
        System.out.println("app version==" + appVerson);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        // Update Here
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, appVerson);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL, AppGooglePlayStoreUrl);
        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(60) // fetch every minutes
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println(TAG + "remote config is fetched.");
                            firebaseRemoteConfig.fetchAndActivate();
                        }
                    }
                });
        firebaseRemoteConfig.fetch(60);
        mInstance = this;
        UtilityMethod.overrideFont(getApplicationContext(), "DEFAULT", "fonts/OpenSans-Regular.ttf");
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}