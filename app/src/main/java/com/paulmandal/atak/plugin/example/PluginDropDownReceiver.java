package com.paulmandal.atak.plugin.example;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.dropdown.DropDown;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.maps.MapView;

public class PluginDropDownReceiver extends DropDownReceiver implements DropDown.OnStateListener {
    public static final String SHOW_PLUGIN = "com.paulmandal.atak.plugin.example.SHOW_PLUGIN";

    private final View pluginView;

    private boolean isDropdownOpen;

    public PluginDropDownReceiver(final MapView mapView, final Context pluginContext) {
        super(mapView);

        this.pluginView = PluginLayoutInflater.inflate(pluginContext, R.layout.main_layout, null);
    }

    public void disposeImpl() {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action == null)
            return;

        if (action.equals(SHOW_PLUGIN)) {
            showDropDown(this.pluginView, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH, HALF_HEIGHT, false, this);
        }
    }

    @Override
    public void onDropDownSelectionRemoved() {
    }

    @Override
    public void onDropDownVisible(boolean isVisible) {
        this.isDropdownOpen = true;
    }

    @Override
    public void onDropDownSizeChanged(double width, double height) {
    }

    @Override
    public void onDropDownClose() {
        this.isDropdownOpen = false;
    }

    public boolean isDropDownOpen() {
        return isDropdownOpen;
    }
}
