package com.ysoft.iota.play.examples;

import com.ysoft.firmata.AbstractCustomSysexEvent;
import com.ysoft.firmata.CustomSysexEventListener;
import com.ysoft.firmata.DeviceConfiguration;
import com.ysoft.firmata.impl.FirmataDevice;
import com.ysoft.firmata.impl.FirmataUtils;
import com.ysoft.firmata.impl.parser.FirmataToken;
import com.ysoft.firmata.impl.fsm.AbstractCustomState;
import com.ysoft.firmata.impl.fsm.Event;

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
