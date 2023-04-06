package com.example.ocrpresenter;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.atomic.AtomicBoolean;

//import ActivityLifecycleCallbacks;

public class OcrWordApplication extends Application  {

    private static String TAG = "OcrWordApplicaiton";

    private static OcrWordApplication instance = new OcrWordApplication();
    private static final AtomicBoolean applicationBackgrounded = new AtomicBoolean(true);
    private static final long INTERVAL_BACKGROUND_STATE_CHANGE = 750L;

    public OcrWordApplication() {
        super();
        instance = this;

    }

    /**
     * @return The singleton instance
     */
    public static OcrWordApplication getApp() {
        return instance;
    }

    public static Context getAppContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
//            sDriverManager = DriverManager.getInstance();
//            PowerPreference.init(this);
//
//            FirebaseMessaging.getInstance().getToken()
//                    .addOnCompleteListener(new OnCompleteListener<String>() {
//                        @Override
//                        public void onComplete(@NonNull Task<String> task) {
//                            if (!task.isSuccessful()) {
//                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                                return;
//                            }
//
//                            // Get new FCM registration token
//                            String token = task.getResult();
//
//                            // Log and toast
////                            String msg = getString(R.string.msg_token_fmt, token);
//                            Log.d(TAG, token);
////                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                        }
//                    });

        }catch (Exception e) {
            //AlertDialogUtils.exceptionCustomDialog(this,e);
        }
//        MultiDex.install(this);

//        PreferenceHelper.OnCreate(getApplicationContext());
//        PreferenceHelper.DEVICE_UNIQUE_ID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);


    }



}
