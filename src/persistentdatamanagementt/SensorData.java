package persistentdatamanagement;

/**
 * Created by Mustafa on 13.2.2015.
 */
public interface SensorData {
    /**
     * a method to convert binary data to meaningful class variables
     * @param bytes sensor data in byte representation
     */
    public void fromBinary(byte[] bytes);

    /**
     * converts sensor data to binary format
     * @return sensor data in bineary representation
     */
    public byte[] asBinary();
}
