package com.listoit.location.app;

import android.support.multidex.MultiDexApplication;

import com.listoit.location.helpers.ConnectivityReceiver;
import com.listoit.location.retrofit.services.RestClient;

/**
 * Created by ${ChandraMohanReddy} on 1/21/2017.
 */

public class MainApplication extends MultiDexApplication {
    private static MainApplication mInstance;

    private static RestClient restClient;

    @Override
    public void onCreate() {
        super.onCreate();
        restClient = new RestClient();
    }

    public static synchronized MainApplication getInstance() {
        return mInstance;
    }

    public static RestClient getRestClient() {
        return restClient;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}

