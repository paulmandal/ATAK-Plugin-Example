package com.paulmandal.atak.plugin.example;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.atakmap.comms.CommsMapComponent;
import com.atakmap.coremap.cot.event.CotEvent;

import java.util.Arrays;

public class OutboundMessageHandler implements CommsMapComponent.PreSendProcessor {
    private static final String TAG = OutboundMessageHandler.class.getSimpleName();

    private CommsMapComponent commsMapComponent;
    private Context atakContext;

    public OutboundMessageHandler(CommsMapComponent commsMapComponent, Context atakContext) {
        this.commsMapComponent = commsMapComponent;
        this.atakContext = atakContext;

        commsMapComponent.registerPreSendProcessor(this);
    }

    public void destroy() {
        this.commsMapComponent.registerPreSendProcessor(null);
    }

    @Override
    public void processCotEvent(CotEvent cotEvent, String[] toUIDs) {
        String msg = "processCotEvent: " + cotEvent + " UIDs: " + Arrays.toString(toUIDs);
        Log.d(TAG, msg);

        // Use atakContext for Toasts, pluginContext for getting resources (e.g. strings)
        Toast.makeText(this.atakContext, msg, Toast.LENGTH_SHORT).show();
    }
}
