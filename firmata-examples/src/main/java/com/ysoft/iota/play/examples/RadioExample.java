package com.ysoft.iota.play.examples;

import java.util.Date;
import com.ysoft.firmata.AbstractCustomSysexEvent;
import com.ysoft.firmata.CustomSysexEventListener;
import com.ysoft.firmata.DeviceConfiguration;
import com.ysoft.firmata.impl.FirmataDevice;
import com.ysoft.firmata.impl.FirmataUtils;
import com.ysoft.firmata.impl.fsm.AbstractCustomState;
import com.ysoft.firmata.impl.fsm.Event;

/**
 *
 * @author Stepan Novacek &lt;stepan.novacek@ysoft.com&gt;
 */
public class RadioExample {

    public static void main(String[] args) throws Exception {

        DeviceConfiguration deviceConfiguration = new DeviceConfiguration("COM13")
                .addCustomSysex((byte) 0x03, RadioMessageState.class, RadioSysexEvent.class);

        FirmataDevice device = new FirmataDevice(deviceConfiguration);
        device.start();
        device.ensureInitializationIsDone();

        //send RF configuration(sysex 0x02) - net ID 66, node ID 2
        device.sendCustomSysex((byte) 0x02, new byte[]{66, 2});

        device.addCustomSysexEventListener((CustomSysexEventListener<RadioSysexEvent>) (byte sysexByte, RadioSysexEvent event) -> {
            System.out.println(new Date() + String.format(" Recieved temperature: %.2f°C from node %d, RSSI:%d dBm",
                    event.temperature, event.nodeId, event.rssi));
        });
    }

    public static class RadioMessageState extends AbstractCustomState {

        @Override
        protected boolean handleEndSysexState(Event event) {
            byte[] buffer = getBuffer();
            byte[] decodedBuffer = FirmataUtils.decodeBytes(buffer);
            event.setBodyItem("nodeId", decodedBuffer[0]);
            event.setBodyItem("rssi", decodedBuffer[1]);
            if (decodedBuffer.length > 3) {
                int t = ((decodedBuffer[2] * 256) + decodedBuffer[3]);
                double ctemp = ((175.72 * t) / 65536.0) - 46.85;
                event.setBodyItem("temp", ctemp);
                return true;
            } else {
                return false;
            }
        }

    }

    public static class RadioSysexEvent extends AbstractCustomSysexEvent {

        private byte nodeId;
        private byte rssi;
        private double temperature;

        @Override
        protected void loadCustomContent(Event event) {
            if (event.getBodyItem("temp") != null) {
                temperature = (double) event.getBodyItem("temp");
            }
            rssi = (byte) event.getBodyItem("rssi");
            nodeId = (byte) event.getBodyItem("nodeId");
        }
    }

}
