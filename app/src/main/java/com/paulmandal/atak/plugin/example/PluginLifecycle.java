package com.paulmandal.atak.plugin.example;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;

import com.atakmap.android.maps.MapComponent;
import com.atakmap.android.maps.MapView;
import com.atakmap.comms.CommsMapComponent;
import com.atakmap.coremap.log.Log;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import transapps.maps.plugin.lifecycle.Lifecycle;

import static com.atakmap.android.util.ATAKConstants.getPackageName;

public class PluginLifecycle implements Lifecycle {
    private final static String TAG = PluginLifecycle.class.getSimpleName();

    private Context pluginContext;
    private MapView mapView;
    private final Collection<MapComponent> overlays;

    private OutboundMessageHandler outboundMessageHandler;
    private InboundMessageHandler inboundMessageHandler;

    public PluginLifecycle(Context context) {
        pluginContext = context;
        overlays = new LinkedList<>();
    }

    @Override
    public void onCreate(final Activity activity, final transapps.mapi.MapView transappsMapView) {
        if (transappsMapView == null || !(transappsMapView.getView() instanceof MapView)) {
            Log.e(TAG, "This plugin is only compatible with ATAK MapView");
            return;
        }
        mapView = (MapView)transappsMapView.getView();

        String pluginVersion = "0.0";
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(getPackageName(), 0);
            pluginVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // AtakContext should be used for Toasts
        Context atakContext = mapView.getContext();

        Handler uiThreadHandler = new Handler(Looper.getMainLooper());

        this.outboundMessageHandler = new OutboundMessageHandler(CommsMapComponent.getInstance(), uiThreadHandler, atakContext);
        this.inboundMessageHandler = new InboundMessageHandler(pluginVersion);

        overlays.add(new PluginMapComponent());

        // create components
        Iterator<MapComponent> iter = overlays.iterator();
        MapComponent c;
        while (iter.hasNext()) {
            c = iter.next();
            try {
                c.onCreate(pluginContext, activity.getIntent(), mapView);
            } catch (Exception e) {
                Log.w(TAG, "Unhandled exception trying to create overlays MapComponent", e);
                iter.remove();
            }
        }
    }

    @Override
    public void onDestroy() {
        this.outboundMessageHandler.destroy();
        this.inboundMessageHandler.destroy();

        for (MapComponent c : overlays) {
            c.onDestroy(pluginContext, mapView);
        }
    }

    @Override
    public void onStart() {
        for (MapComponent c : overlays) {
            c.onStart(pluginContext, mapView);
        }
    }

    @Override
    public void onPause() {
        for (MapComponent c : overlays) {
            c.onPause(pluginContext, mapView);
        }
    }

    @Override
    public void onResume() {
        for (MapComponent c : overlays) {
            c.onResume(pluginContext, mapView);
        }
    }

    @Override
    public void onFinish() {}

    @Override
    public void onStop() {
        for (MapComponent c : overlays) {
            c.onStop(pluginContext, mapView);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        for (MapComponent c : overlays) {
            c.onConfigurationChanged(configuration);
        }
    }
}
