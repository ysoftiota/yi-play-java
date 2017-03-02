package com.ysoft.iota.play.examples;

import java.awt.Dimension;
import java.awt.Robot;
import org.firmata4j.AbstractCustomSysexEvent;
import org.firmata4j.CustomSysexEventListener;
import org.firmata4j.DeviceConfiguration;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.firmata.FirmataUtils;
import org.firmata4j.fsm.AbstractCustomState;
import org.firmata4j.fsm.Event;

/**
 * Google Earth Flight Simulator.
 * @author Stepan Novacek &lt;stepan.novacek@ysoft.com&gt;
 */
public class FlightSimulator {

    private static final int SENSITIVITY = 9;

    public static void main(String[] args) throws Exception {
        DeviceConfiguration deviceConfiguration = new DeviceConfiguration("COM13")
                .addCustomSysex((byte) 0x03, RadioMessageState.class, RadioSysexEvent.class);

        FirmataDevice device = new FirmataDevice(deviceConfiguration);
        device.start();
        device.ensureInitializationIsDone();
        device.sendCustomSysex((byte) 0x02, new byte[]{66, 1});

        final Robot robot = new Robot();
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        final int centerY = (int) screen.getHeight() / 2;
        final int centerX = (int) screen.getWidth() / 2;

        device.addCustomSysexEventListener((CustomSysexEventListener<RadioSysexEvent>) (byte sysexByte, RadioSysexEvent event) -> {
            if (sysexByte == 0x03) {
                robot.mouseMove(centerX + -SENSITIVITY * event.x, centerY + SENSITIVITY * event.y);
            }
        });
    }

    public static class RadioMessageState extends AbstractCustomState {

        @Override
        protected boolean handleEndSysexState(Event event) {
            if (getBuffer().length < 10) {
                return false;
            }
            byte[] data = FirmataUtils.decodeBytes(getBuffer());
            event.setBodyItem("x", data[2]);
            event.setBodyItem("y", data[3]);
            event.setBodyItem("z", data[4]);
            return true;
        }
    }

    public static class RadioSysexEvent extends AbstractCustomSysexEvent {

        private int x, y, z;

        @Override
        protected void loadCustomContent(Event event) {
            float dgrX = (byte) event.getBodyItem("x") / 256f * 360f;
            float dgrY = (byte) event.getBodyItem("y") / 256f * 360f;
            float dgrZ = (byte) event.getBodyItem("z") / 256f * 360f;

            x = (int) dgrX;
            y = (int) dgrY;
            z = (int) dgrZ;
        }
    }

}
