package com.ysoft.iota.play.examples;

import org.firmata4j.DeviceConfiguration;
import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
import org.firmata4j.PinEventListener;
import org.firmata4j.firmata.FirmataDevice;

/**
 *
 * @author Stepan Novacek &lt;stepan.novacek@ysoft.com&gt;
 */
public class ButtonExampleMain {

    public static void main(String[] args) throws Exception {
        DeviceConfiguration deviceConfiguration = new DeviceConfiguration("COM13");

        FirmataDevice device = new FirmataDevice(deviceConfiguration);
        device.start();
        device.ensureInitializationIsDone();
        device.getPin(30).setMode(Pin.Mode.INPUT);
        device.getPin(30).addEventListener(new PinEventListener() {
            @Override
            public void onModeChange(IOEvent event) {
            }

            @Override
            public void onValueChange(IOEvent event) {
                if (event.getValue() == 0) {
                    Pin pin = device.getPin(26);
                    try {
                        pin.setValue(pin.getValue() == 1L ? 0 : 1L);
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            }
        });

    }

}
