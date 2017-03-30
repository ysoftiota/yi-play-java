package com.ysoft.iota.play.examples;

import com.ysoft.firmata.DeviceConfiguration;
import com.ysoft.firmata.I2CDevice;
import com.ysoft.firmata.I2CEvent;
import com.ysoft.firmata.impl.FirmataDevice;

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
        final byte register = (byte) (LIS3DE_X_L_REGISTER | LIS3DE_AUTO_INCREMENT);

        i2cDevice.subscribe(register, (I2CEvent event) -> {
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
            float dgrX = accX / 256f * 360f;
            float dgrY = accY / 256f * 360f;
            float dgrZ = accZ / 256f * 360f;
            System.out.println(String.format("X:%.2f° Y:%.2f° Z:%.2f°", dgrX, dgrY, dgrZ));
        });

        //start continuous reading from device
        i2cDevice.ask(register, (byte) 6, true);

    }

}
