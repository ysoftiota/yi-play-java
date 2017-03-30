package com.ysoft.iota.play.examples;

import com.ysoft.firmata.DeviceConfiguration;
import com.ysoft.firmata.I2CDevice;
import com.ysoft.firmata.I2CEvent;
import com.ysoft.firmata.impl.FirmataDevice;

/**
 *
 * @author Stepan Novacek &lt;stepan.novacek@ysoft.com&gt;
 */
public class TemperatureExampleMain {

    public static void main(String[] args) throws Exception {
        DeviceConfiguration deviceConfiguration = new DeviceConfiguration("COM13");

        FirmataDevice device = new FirmataDevice(deviceConfiguration);
        device.start();
        device.ensureInitializationIsDone();
        I2CDevice therm = device.getI2CDevice(((byte) 0x40));
        final byte register = (byte) 0xf3;

        therm.subscribe((I2CEvent event) -> {
            final byte[] data = event.getData();
            int t = ((data[0] * 256) + data[1]);
            double ctemp = ((175.72 * t) / 65536.0) - 46.85;
            System.out.println(String.format("Temperature: %.2f°C", ctemp));
        });

        //start continuous reading from device
        therm.ask((byte) 2, true);
        
        //measure temperature every 500ms
        while (true) {
            therm.tell(register);
            Thread.sleep(500L);
        }

    }

}
