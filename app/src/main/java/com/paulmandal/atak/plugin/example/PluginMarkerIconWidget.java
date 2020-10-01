package com.paulmandal.atak.plugin.example;

import android.content.Intent;
import android.graphics.Color;
import android.view.MotionEvent;

import com.atakmap.android.dropdown.DropDownManager;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.widgets.LinearLayoutWidget;
import com.atakmap.android.widgets.MapWidget;
import com.atakmap.android.widgets.MarkerIconWidget;
import com.atakmap.android.widgets.RootLayoutWidget;
import com.atakmap.coremap.maps.assets.Icon;

public class PluginMarkerIconWidget extends MarkerIconWidget implements MapWidget.OnClickListener {
    private final static int ICON_WIDTH = 32;
    private final static int ICON_HEIGHT = 32;

    private PluginDropDownReceiver pluginDropDownReceiver;
    private MapView mapView;

    public PluginMarkerIconWidget(MapView mapView, PluginDropDownReceiver pluginDropDownReceiver) {
        this.pluginDropDownReceiver = pluginDropDownReceiver;
        this.mapView = mapView;

        setName("Plugin Icon Widget");
        addOnClickListener(this);

        RootLayoutWidget root = (RootLayoutWidget) mapView.getComponentExtra("rootLayoutWidget");
        LinearLayoutWidget brLayout = root.getLayout(RootLayoutWidget.BOTTOM_RIGHT);
        brLayout.addWidget(this);

        String imageUri = "android.resource://com.paulmandal.atak.plugin.example/" + R.drawable.ic_map_overlay;

        Icon.Builder builder = new Icon.Builder();
        builder.setAnchor(0, 0);
        builder.setColor(Icon.STATE_DEFAULT, Color.WHITE);
        builder.setSize(ICON_WIDTH, ICON_HEIGHT);
        builder.setImageUri(Icon.STATE_DEFAULT, imageUri);

        Icon icon = builder.build();
        setIcon(icon);
    }

    @Override
    public void onMapWidgetClick(MapWidget widget, MotionEvent event) {
        if (widget == this) {
            if (!this.pluginDropDownReceiver.isDropDownOpen()) {
                Intent intent = new Intent();
                intent.setAction(PluginDropDownReceiver.SHOW_PLUGIN);
                AtakBroadcast.getInstance().sendBroadcast(intent);
            } else {
                DropDownManager.getInstance().unHidePane();
            }
        }
    }

    public void onDestroy() {
        RootLayoutWidget root = (RootLayoutWidget) mapView.getComponentExtra("rootLayoutWidget");
        LinearLayoutWidget brLayout = root.getLayout(RootLayoutWidget.BOTTOM_RIGHT);
        brLayout.removeWidget(this);
    }
}
