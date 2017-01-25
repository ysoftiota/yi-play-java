package com.ysoft.iota.play.examples;

import org.apache.commons.codec.binary.Hex;
import org.firmata4j.DeviceConfiguration;
import org.firmata4j.I2CDevice;
import org.firmata4j.I2CEvent;
import org.firmata4j.I2CListener;
import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
import org.firmata4j.PinEventListener;
import org.firmata4j.firmata.FirmataDevice;

/**
 *
 * @author Stepan Novacek &lt;stepan.novacek@ysoft.com&gt;
 */
public class HumidityExampleMain {

    public static void main(String[] args) throws Exception {
        DeviceConfiguration deviceConfiguration = new DeviceConfiguration("COM13");

        FirmataDevice device = new FirmataDevice(deviceConfiguration);
        device.start();
        device.ensureInitializationIsDone();
        
        
        I2CDevice therm = device.getI2CDevice(((byte) 0x40));
        therm.startReceivingUpdates((byte) 2);
        
       
        therm.subscribe(new I2CListener() {
            @Override
            public void onReceive(I2CEvent event) {
                final byte[] data = event.getData();
                int humidity = data[0] * 256 + data[1];
                double h= ((125 * humidity) / 65536.0) - 6;
                System.out.println(String.format("Humidity: %.2f %%", h));
            }
        });
        therm.tell((byte) 0xf5);

    }

}
