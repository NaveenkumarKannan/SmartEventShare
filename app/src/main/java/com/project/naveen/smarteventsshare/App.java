package com.project.naveen.smarteventsshare;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.project.naveen.smarteventsshare.receiver.NetworkStateChangeReceiver;
import com.project.naveen.smarteventsshare.socialintegration.managers.SharedPreferenceManager;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class App extends Application implements Application.ActivityLifecycleCallbacks{
    private static final String WIFI_STATE_CHANGE_ACTION = "android.net.wifi.WIFI_STATE_CHANGED";

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "EdoQgsMN6wywg7F1b7j06mWGN";
    private static final String TWITTER_SECRET = "4B3jROugm7d0yDExHQzwtTlzUqH7dhiv4Xy28FKfDGuLydrfcy";

    @Override
    public void onCreate() {
        super.onCreate();
        registerForNetworkChangeEvents(this);
        instantiateManagers();
    }

    public static void registerForNetworkChangeEvents(final Context context) {
        NetworkStateChangeReceiver networkStateChangeReceiver = new NetworkStateChangeReceiver();
        context.registerReceiver(networkStateChangeReceiver, new IntentFilter(CONNECTIVITY_ACTION));
        context.registerReceiver(networkStateChangeReceiver, new IntentFilter(WIFI_STATE_CHANGE_ACTION));
    }

    /**
     * Method to instantiate all the managers in this app
     */

    private void instantiateManagers() {
        FacebookSdk.sdkInitialize(this);
        SharedPreferenceManager.getSharedInstance().initiateSharedPreferences(getApplicationContext());
        Fresco.initialize(this);
        //TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        //Fabric.with(this, new Twitter(authConfig));
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
