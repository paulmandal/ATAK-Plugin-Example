package com.paulmandal.atak.plugin.example;

import android.content.Context;
import android.content.Intent;

import com.atakmap.android.dropdown.DropDownMapComponent;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;

public class PluginMapComponent  extends DropDownMapComponent {
    private PluginMarkerIconWidget pluginMarkerIconWidget;

    public void onCreate(final Context pluginContext,
                         Intent intent,
                         final MapView mapView) {
        pluginContext.setTheme(R.style.ATAKPluginTheme);
        super.onCreate(pluginContext, intent, mapView);

        Context atakContext = mapView.getContext();

        PluginDropDownReceiver pluginDropDownReceiver = new PluginDropDownReceiver(mapView, pluginContext);

        AtakBroadcast.DocumentedIntentFilter ddFilter = new AtakBroadcast.DocumentedIntentFilter();
        ddFilter.addAction(PluginDropDownReceiver.SHOW_PLUGIN);
        registerDropDownReceiver(pluginDropDownReceiver, ddFilter);

        this.pluginMarkerIconWidget = new PluginMarkerIconWidget(mapView, pluginDropDownReceiver);
    }

    @Override
    protected void onDestroyImpl(Context context, MapView view) {
        super.onDestroyImpl(context, view);
        this.pluginMarkerIconWidget.onDestroy();
    }
}
