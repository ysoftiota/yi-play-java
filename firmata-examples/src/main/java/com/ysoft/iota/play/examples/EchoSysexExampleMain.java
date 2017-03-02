package com.ysoft.iota.play.examples;

import org.firmata4j.AbstractCustomSysexEvent;
import org.firmata4j.CustomSysexEventListener;
import org.firmata4j.DeviceConfiguration;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.firmata.FirmataUtils;
import org.firmata4j.firmata.parser.FirmataToken;
import org.firmata4j.fsm.AbstractCustomState;
import org.firmata4j.fsm.Event;

/**
 *
 * @author Stepan Novacek &lt;stepan.novacek@ysoft.com&gt;
 */
public class EchoSysexExampleMain {

    public static void main(String[] args) throws Exception {
        DeviceConfiguration deviceConfiguration = new DeviceConfiguration("COM13")
                .addCustomSysex((byte) 0x01, CustomStringMessageState.class, SysexEvent.class);

        FirmataDevice device = new FirmataDevice(deviceConfiguration);
        device.start();
        device.ensureInitializationIsDone();

        device.addCustomSysexEventListener((CustomSysexEventListener<SysexEvent>) (byte sysexByte, SysexEvent event) -> {
            System.out.println("Recieved custom sysex message:" + event.getMessage());
        });

        device.sendCustomSysex((byte) 0x01, "Hello world!");

    }

    public static class SysexEvent extends AbstractCustomSysexEvent {

        private String message;

        public String getMessage() {
            return message;
        }

        public SysexEvent() {
            super();
        }

        @Override
        protected void loadCustomContent(Event event) {
            message = (String) event.getBodyItem(FirmataToken.STRING_MESSAGE);
        }
    }

    public static class CustomStringMessageState extends AbstractCustomState {

        @Override
        protected boolean handleEndSysexState(Event event) {
            String value = new String(FirmataUtils.decodeBytes(getBuffer()));
            event.setBodyItem(FirmataToken.STRING_MESSAGE, value);
            return true;
        }

    }

}
