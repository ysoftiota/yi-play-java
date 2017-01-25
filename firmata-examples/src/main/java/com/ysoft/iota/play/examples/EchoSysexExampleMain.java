package com.ysoft.iota.play.examples;

import org.firmata4j.AbstractCustomSysexEvent;
import org.firmata4j.CustomSysexEventListener;
import org.firmata4j.DeviceConfiguration;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.firmata.FirmataMessageFactory;
import org.firmata4j.firmata.parser.FirmataToken;
import org.firmata4j.firmata.parser.WaitingForMessageState;
import org.firmata4j.fsm.AbstractCustomState;
import org.firmata4j.fsm.Event;
import org.firmata4j.fsm.EventType;

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
        
        device.addCustomSysexEventListener(new CustomSysexEventListener<SysexEvent>() {
            @Override
            public void onCustomSysexMessage(byte sysexByte, SysexEvent event) {
                System.out.println("Recieved custom sysex message:" + event.getMessage());
            }

        });
        
        device.sendCustomSysex((byte) 0x01, FirmataMessageFactory.encodeString("Hello world!"));

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
        public void process(byte b) {
            if (b == FirmataToken.END_SYSEX) {
                byte[] buffer = getBuffer();
                String value = decode(buffer, 0, buffer.length);
                Event event = new Event(FirmataToken.CUSTOM_SYSEX_MESSAGE, EventType.FIRMATA_MESSAGE_EVENT_TYPE);
                event.setBodyItem(FirmataToken.STRING_MESSAGE, value);
                transitTo(WaitingForMessageState.class, b);
                publish(event);
            } else {
                bufferize(b);
            }
        }

        private String decode(byte[] buffer, int offset, int length) {
            StringBuilder result = new StringBuilder();
            length = length >>> 1;
            for (int i = 0; i < length; i++) {
                result.append((char) (buffer[offset++] + (buffer[offset++] << 7)));
            }
            return result.toString();
        }
    }

}
