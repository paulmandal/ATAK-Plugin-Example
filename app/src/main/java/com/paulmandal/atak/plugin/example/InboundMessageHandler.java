package com.paulmandal.atak.plugin.example;

import android.util.Log;

import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;
import com.atakmap.coremap.cot.event.CotPoint;
import com.atakmap.coremap.maps.time.CoordinatedTime;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class InboundMessageHandler {
    private static final String TAG = InboundMessageHandler.class.getSimpleName();

    private static final int DELAY_BETWEEN_REPOSTING_FAKE_MARKER = 60000;

    private static final int INBOUND_MESSAGE_DEST_PORT = 4242;
    private static final int INBOUND_MESSAGE_SRC_PORT = 12345; // 1024 < x < 65535

    private static final int STALE_TIME_OFFSET_MS = 75000;
    private static final double UNKNOWN_LE_CE = 9999999.0;

    private static final String TAG_DETAIL = "detail";
    private static final String TAG_TAKV = "takv";
    private static final String TAG_CONTACT = "contact";
    private static final String TAG_OS = "os";
    private static final String TAG_VERSION = "version";
    private static final String TAG_DEVICE = "device";
    private static final String TAG_PLATFORM = "platform";
    private static final String TAG_CALLSIGN = "callsign";
    private static final String TAG_UID = "uid";
    private static final String TAG_DROID = "Droid";
    private static final String TAG_PRECISION_LOCATION = "precisionlocation";
    private static final String TAG_ALTSRC = "altsrc";
    private static final String TAG_GEOPOINTSRC = "geopointsrc";
    private static final String TAG_GROUP = "__group";
    private static final String TAG_ROLE = "role";
    private static final String TAG_NAME = "name";
    private static final String TAG_STATUS = "status";
    private static final String TAG_BATTERY = "battery";

    private static final String VALUE_OS = "1";
    private static final String VALUE_UID = "FakeMarker";
    private static final String VALUE_FAKE_MARKER = "Fake Marker";
    private static final String VALUE_PLUGIN_EXAMPLE = "ATAK Plugin Example";
    private static final String VALUE_DTED0 = "DTED0";
    private static final String VALUE_USER = "USER";
    private static final String VALUE_ROLE = "HQ";
    private static final String VALUE_TEAM = "Green";
    private static final String VALUE_100_PERCENT = "100";


    private static final String VALUE_PLI = "a-f-G-U-C";
    private static final String VALUE_HOW = "h-e";

    private CotEvent fakeCotEvent;

    private boolean keepAlive = true;

    public InboundMessageHandler(String pluginVersion) {
        new Thread(this::createFakePliMarkerLoop).start();

        CotEvent fakeCotEvent = new CotEvent();

        CoordinatedTime nowCoordinatedTime = new CoordinatedTime(System.currentTimeMillis());
        CoordinatedTime staleCoordinatedTime = new CoordinatedTime(nowCoordinatedTime.getMilliseconds() + STALE_TIME_OFFSET_MS);

        fakeCotEvent.setUID(VALUE_UID);
        fakeCotEvent.setType(VALUE_PLI);
        fakeCotEvent.setTime(nowCoordinatedTime);
        fakeCotEvent.setStart(nowCoordinatedTime);
        fakeCotEvent.setStale(staleCoordinatedTime);
        fakeCotEvent.setHow(VALUE_HOW);
        fakeCotEvent.setPoint(new CotPoint(0, 0, 0, UNKNOWN_LE_CE, UNKNOWN_LE_CE));

        CotDetail cotDetail = new CotDetail(TAG_DETAIL);

        CotDetail takvDetail = new CotDetail(TAG_TAKV);
        takvDetail.setAttribute(TAG_OS, VALUE_OS);
        takvDetail.setAttribute(TAG_VERSION, pluginVersion);
        takvDetail.setAttribute(TAG_DEVICE, VALUE_FAKE_MARKER);
        takvDetail.setAttribute(TAG_PLATFORM, VALUE_PLUGIN_EXAMPLE);
        cotDetail.addChild(takvDetail);

        CotDetail contactDetail = new CotDetail(TAG_CONTACT);
        contactDetail.setAttribute(TAG_CALLSIGN, VALUE_UID);
        cotDetail.addChild(contactDetail);

        CotDetail uidDetail = new CotDetail(TAG_UID);
        uidDetail.setAttribute(TAG_DROID, VALUE_UID);
        cotDetail.addChild(uidDetail);

        CotDetail precisionLocationDetail = new CotDetail(TAG_PRECISION_LOCATION);
        precisionLocationDetail.setAttribute(TAG_ALTSRC, VALUE_DTED0);
        precisionLocationDetail.setAttribute(TAG_GEOPOINTSRC, VALUE_USER);
        cotDetail.addChild(precisionLocationDetail);

        CotDetail groupDetail = new CotDetail(TAG_GROUP);
        groupDetail.setAttribute(TAG_ROLE, VALUE_ROLE);
        groupDetail.setAttribute(TAG_NAME, VALUE_TEAM);
        cotDetail.addChild(groupDetail);

        CotDetail statusDetail = new CotDetail(TAG_STATUS);
        statusDetail.setAttribute(TAG_BATTERY, VALUE_100_PERCENT);
        cotDetail.addChild(statusDetail);

        fakeCotEvent.setDetail(cotDetail);

        this.fakeCotEvent = fakeCotEvent;
    }

    public void destroy() {
        keepAlive = false;
    }

    private void createFakePliMarkerLoop() {
        while (keepAlive) {
            sendCotToLocalhost(this.fakeCotEvent);

            try {
                Thread.sleep(DELAY_BETWEEN_REPOSTING_FAKE_MARKER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendCotToLocalhost(CotEvent cotEvent) {
        String cotEventString = cotEvent.toString();
        byte[] cotEventBytes = cotEventString.getBytes();

        Log.d(TAG, "sendCotToLocalhost(): " + cotEventString);
        try (DatagramSocket socket = new DatagramSocket(INBOUND_MESSAGE_SRC_PORT)) {
            InetAddress serverAddr = InetAddress.getLocalHost();
            DatagramPacket packet = new DatagramPacket(cotEventBytes, cotEventBytes.length, serverAddr, INBOUND_MESSAGE_DEST_PORT);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException while trying to send message to UDP");
        }
    }
}
