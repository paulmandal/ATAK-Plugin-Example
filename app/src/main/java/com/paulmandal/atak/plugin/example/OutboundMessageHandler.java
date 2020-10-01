package com.paulmandal.atak.plugin.example;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.atakmap.comms.CommsMapComponent;
import com.atakmap.coremap.cot.event.CotEvent;

import java.util.Arrays;

public class OutboundMessageHandler implements CommsMapComponent.PreSendProcessor {
    private static final String TAG = OutboundMessageHandler.class.getSimpleName();

    private CommsMapComponent commsMapComponent;
    private Handler uiThreadHandler;
    private Context atakContext;

    public OutboundMessageHandler(CommsMapComponent commsMapComponent,
                                  Handler uiThreadHandler,
                                  Context atakContext) {
        this.commsMapComponent = commsMapComponent;
        this.uiThreadHandler = uiThreadHandler;
        this.atakContext = atakContext;

        commsMapComponent.registerPreSendProcessor(this);
    }

    public void destroy() {
        this.commsMapComponent.registerPreSendProcessor(null);
    }

    @Override
    public void processCotEvent(CotEvent cotEvent, String[] toUIDs) {
        Log.d(TAG, "processCotEvent: " + cotEvent + " UIDs: " + Arrays.toString(toUIDs));

        // Use atakContext for Toasts, pluginContext for getting resources (e.g. strings)
        uiThreadHandler.post(() -> Toast.makeText(this.atakContext, "Outbound CoT, type: " + cotEvent.getType(), Toast.LENGTH_SHORT).show());
    }
}
