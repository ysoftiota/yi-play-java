package com.ysoft.iota.play.examples;

import org.firmata4j.DeviceConfiguration;
import org.firmata4j.I2CDevice;
import org.firmata4j.I2CEvent;
import org.firmata4j.I2CListener;
import org.firmata4j.firmata.FirmataDevice;

/**
 *
 * @author Stepan Novacek &lt;stepan.novacek@ysoft.com&gt;
 */
public class AccelerometerExampleMain {

    private static final byte LIS3DE_DATARATE_10_HZ = 0x02;
    private static final byte LIS3DE_DATARATE_50_HZ = 0x04;
    private static final byte LIS3DE_CTRL_REG1 = 0x20;
    private static final byte LIS3DE_X_L_REGISTER = 0x28;
    private static final byte LIS3DE_AUTO_INCREMENT = (byte) 0x80;

    public static void main(String[] args) throws Exception {
        DeviceConfiguration deviceConfiguration = new DeviceConfiguration("COM13");
        FirmataDevice device = new FirmataDevice(deviceConfiguration);
        device.start();
        device.ensureInitializationIsDone();

        I2CDevice i2cDevice = device.getI2CDevice((byte) 0x28);

        byte value = (byte) 0x07;  //Enables all three axis
        value |= (LIS3DE_DATARATE_10_HZ << 4);  // Set sampling interval
        i2cDevice.tell(LIS3DE_CTRL_REG1, value);
        while (true) {
//            i2cDevice.tell((byte) (LIS3DE_X_L_REGISTER | LIS3DE_AUTO_INCREMENT));
//            Thread.sleep(20L);
            i2cDevice.ask((byte) 6, new I2CListener() {
                @Override
                public void onReceive(I2CEvent event) {
                System.out.print("values:");
                    for (byte b : event.getData()) {
                        System.out.print(" " + b);
                    }
                    System.out.println("");
                    byte accX = event.getData()[1];
                    byte accY = event.getData()[3];
                    byte accZ = event.getData()[5];
                    if (accX > 128) {
                        accX -= 256;
                    }
                    if (accY > 128) {
                        accY -= 256;
                    }
                    if (accZ > 128) {
                        accZ -= 256;
                    }
//                    System.out.println(String.format("X:%d Y:%d Z:%d", accX, accY, accZ));
                }
            });
            Thread.sleep(30L);
        }
//        i2cDevice.startReceivingUpdates((byte) 6);

    }

}
